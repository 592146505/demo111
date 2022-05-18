package com.example.demo111.service;

import com.example.demo111.dto.MigrateBatchTask;
import com.example.demo111.req.DataBaseInfo;
import com.example.demo111.util.JdbcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author roamer
 * @version v1.0
 * @since 2022/5/18 18:31
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MigrateService {

    private final MigrateBatchTaskService migrateBatchTaskService;

    public String migrate(DataBaseInfo source, DataBaseInfo target) {
        JdbcTemplate sourceJdbcTemplate = JdbcUtil.jdbcTemplate(source.getUrl(), source.getUsername(), source.getPassword());
        // 先查询数量
        Integer count = sourceJdbcTemplate.queryForObject("select count(1)as cn from " + source.getTableName(), Integer.class);
        log.info("查询总记录数:{} ", count);

        if (Objects.isNull(count) || count == 0) {
            return "";
        }

        int size = 2;
        // 创建批次任务
        MigrateBatchTask task = migrateBatchTaskService.createMigrateBatchTask(source, target, count, size);
        log.info("创建批次任务：>> 批次编号:{} 总次数:{} 单次数量:{}", task.getBatchNo(), task.getTotalPage(), task.getSize());
        return task.getBatchNo();
    }
}
