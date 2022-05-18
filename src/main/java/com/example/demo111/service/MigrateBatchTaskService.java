package com.example.demo111.service;

import com.example.demo111.dto.MigrateBatchTask;
import com.example.demo111.req.DataBaseInfo;

/**
 * @author roamer
 * @version v1.0
 * @since 2022/5/19 00:28
 */
public interface MigrateBatchTaskService {

    void updateTask(MigrateBatchTask task);

    MigrateBatchTask getActiveMigrateBatchTask();

    MigrateBatchTask createMigrateBatchTask(DataBaseInfo source, DataBaseInfo target, int count, int size);
}
