package com.example.demo111.service;

import cn.hutool.core.bean.BeanUtil;
import com.example.demo111.dto.MigrateBatchTask;
import com.example.demo111.req.DataBaseInfo;
import com.example.demo111.resp.MigrateInfoResp;
import com.example.demo111.util.JdbcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author zgl
 * @version v1.0
 * @since 2022/5/18 15:31
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MigrateService {

    private final MigrateBatchTaskService migrateBatchTaskService;

    public String migrate(DataBaseInfo source, DataBaseInfo target, int size) {
        JdbcTemplate sourceJdbcTemplate = JdbcUtil.jdbcTemplate(source.getUrl(), source.getUsername(), source.getPassword());
        // 先查询数量
        Integer count = sourceJdbcTemplate.queryForObject("select count(1)as cn from " + source.getTableName(), Integer.class);
        log.info("查询总记录数:{} ", count);

        if (Objects.isNull(count) || count == 0) {
            return "数据条数为0，无需迁移";
        }
        // 创建批次任务
        MigrateBatchTask task = migrateBatchTaskService.createMigrateBatchTask(source, target, count, size);
        // todo 未做全局异常，避免直接抛出错误;
        if (Objects.isNull(task)) {
            return "该表已存在迁移任务";
        }
        log.info("创建批次任务：>> 批次编号:{} 总次数:{} 单次数量:{}", task.getBatchNo(), task.getTotalPage(), task.getSize());
        return "批次号:" + task.getBatchNo();
    }

    public MigrateInfoResp getByBatchNo(String batchNo) {
        MigrateBatchTask task = migrateBatchTaskService.getByBatchNo(batchNo);
        if (Objects.isNull(task)) {
            return null;
        }
        return BeanUtil.copyProperties(task, MigrateInfoResp.class);
    }
}
