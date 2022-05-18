package com.example.demo111.job;

import com.example.demo111.dto.MigrateBatchTask;
import com.example.demo111.req.DataBaseInfo;
import com.example.demo111.service.MigrateBatchTaskService;
import com.example.demo111.util.JdbcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zgl
 * @version v1.0
 * @since 2022/5/18 15:24
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class MigrateBatchTaskJob {

    private final MigrateBatchTaskService migrateBatchTaskService;

    @Scheduled(cron = "0/10 * * * * ?")
    public void execute() {
        MigrateBatchTask task = migrateBatchTaskService.getActiveMigrateBatchTask();
        if (Objects.isNull(task)) {
            log.info("无可执行任务");
            return;
        }

        boolean isOracle = task.getSourceDataSource().getIsOracle();
        int size = task.getSize();
        int page = task.getLastSyncPage() + 1;
        int start = (page - 1) * size;
        log.info("开始执行：>> 批次编号:{} 总次数:{} 当前次数:{} 单次数量:{}", task.getBatchNo(), task.getTotalPage(), page, task.getSize());
        // 分页查询
        List<Map<String, Object>> list = new ArrayList<>();
        List<String> columns = new ArrayList<>();
        List<Integer> rowNums = new ArrayList<>(size);
        try {
            // 分页查询
            JdbcTemplate sourceJdbcTemplate = JdbcUtil.jdbcTemplate(task.getSourceDataSource().getUrl(), task.getSourceDataSource().getUsername(), task.getSourceDataSource().getPassword());
            String sql = "select t.* from  " + task.getSourceTableName() + " t limit " + start + "," + size;
            if (isOracle) {
                // todo 判断是否<=0，小于等于0就不用这个条件快速索引
                int rownum = task.getLastRowId();
                sql = "";
            }
            list = sourceJdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<String, Object> map = new LinkedHashMap<>();
                if (columns.isEmpty()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    for (int i = 0; i < metaData.getColumnCount(); i++) {
                        columns.add(metaData.getColumnLabel(i + 1));
                    }
                }
                columns.forEach(column -> {
                    try {
                        if (isOracle && Objects.equals("rownum", column)) {
                            rowNums.add(rs.getInt(column));
                        } else {
                            map.put(column, rs.getObject(column));
                        }

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                return map;
            });
        } catch (Exception e) {
            log.error("查询异常! 批次编号:{}", task.getBatchNo(), e);
            task.setBatchState((byte) 3);
            task.setErrorMessage(e.getMessage());
            task.setLocked(false);
            task.setLockTime(null);
            migrateBatchTaskService.updateTask(task);
            return;
        }

        log.info("查询到{}条：>> 批次编号:{} 总次数:{} 当前次数:{} 单次数量:{}", list.size(), task.getBatchNo(), task.getTotalPage(), page, task.getSize());


        // 为空则直接更新为成功
        if (list.isEmpty()) {
            task.setBatchState((byte) 2);
            task.setLocked(false);
            task.setLockTime(null);
            migrateBatchTaskService.updateTask(task);
            return;
        }

        List<Object[]> finalList = list.stream()
                .map(m -> m.values().toArray())
                .collect(Collectors.toList());
        log.info("开始插入：>> 批次编号:{} 总次数:{} 当前次数:{} 单次数量:{}", task.getBatchNo(), task.getTotalPage(), page, task.getSize());

        // 插入
        String errorMessage = batchInsert(columns, finalList, task.getTargetTableName(), task.getTargetDataSource());
        // 更新状态
        if (StringUtils.hasText(errorMessage)) {
            task.setBatchState((byte) 3);
            task.setErrorMessage(errorMessage);
        } else if (Objects.equals(page, task.getTotalPage())) {
            task.setBatchState((byte) 2);
        }
        task.setLastSyncPage(page);
        if (isOracle && !rowNums.isEmpty()) {
            task.setLastRowId(rowNums.get(rowNums.size() - 1));
        }
        task.setLocked(false);
        task.setLockTime(null);
        migrateBatchTaskService.updateTask(task);
        log.info("插入成功：>> 批次编号:{} 总次数:{} 当前次数:{} 单次数量:{}", task.getBatchNo(), task.getTotalPage(), page, task.getSize());

    }

    private String batchInsert(List<String> columns, List<Object[]> list, String targetTableName, DataBaseInfo targetDataSource) {
        String errorMessage = "";
        try {
            // 插入
            JdbcTemplate targetJdbcTemplate = JdbcUtil.jdbcTemplate(targetDataSource.getUrl(), targetDataSource.getUsername(), targetDataSource.getPassword());
            StringBuilder sqlBuilder = new StringBuilder("insert into ");
            sqlBuilder.append(targetTableName);
            sqlBuilder.append(" values(");
            columns.forEach(column -> sqlBuilder.append("?,"));
            sqlBuilder.delete(sqlBuilder.length() - 1, sqlBuilder.length());
            sqlBuilder.append(");");
            targetJdbcTemplate.batchUpdate(sqlBuilder.toString(), new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Object[] data = list.get(i);
                    for (int j = 0; j < data.length; j++) {
                        ps.setObject(j + 1, data[j]);
                    }
                }

                @Override
                public int getBatchSize() {
                    return list.size();
                }
            });
        } catch (Exception e) {
            errorMessage = e.getMessage();
            log.error("批量插入异常", e);
        }
        return errorMessage;
    }
}
