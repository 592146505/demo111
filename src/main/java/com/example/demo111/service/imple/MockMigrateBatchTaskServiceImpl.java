package com.example.demo111.service.imple;

import cn.hutool.core.bean.BeanUtil;
import com.example.demo111.dto.MigrateBatchTask;
import com.example.demo111.req.DataBaseInfo;
import com.example.demo111.service.MigrateBatchTaskService;
import com.example.demo111.util.UIDGenerator;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MockMigrateBatchTaskServiceImpl
 *
 * @author zgl
 * @version v1.0
 * @since 2022/5/18 16:29
 */
@Service
public class MockMigrateBatchTaskServiceImpl implements MigrateBatchTaskService {

    // mock 数据库索引
    private final Map<String, MigrateBatchTask> sourceMockDBIndex = new ConcurrentHashMap<>();
    private final Map<String, MigrateBatchTask> targetMockDBIndex = new ConcurrentHashMap<>();
    private final Map<String, MigrateBatchTask> migrateBatchTableMockDB = new ConcurrentHashMap<>();

    @Override
    public MigrateBatchTask createMigrateBatchTask(DataBaseInfo source, DataBaseInfo target, int count, int size) {
        String sourceTableName = source.getTableName();
        String targetTableName = target.getTableName();
        if (sourceMockDBIndex.containsKey(sourceTableName) || targetMockDBIndex.containsKey(targetTableName)) {
            return null;
        }
        int totalPage = count % size == 0 ? count / size : count / size + 1;
        // 创建批次任务
        MigrateBatchTask migrateBatchTask = new MigrateBatchTask();
        migrateBatchTask.setBatchNo(UIDGenerator.generateId().toString());
        migrateBatchTask.setSourceTableName(sourceTableName);
        migrateBatchTask.setTargetTableName(targetTableName);
        migrateBatchTask.setTotalCount(count);
        migrateBatchTask.setSize(size);
        migrateBatchTask.setTotalPage(totalPage);
        migrateBatchTask.setLastSyncPage(0);
        migrateBatchTask.setLastRowId(0);
        migrateBatchTask.setBatchState((byte) 1);
        migrateBatchTask.setSourceDataSource(source);
        migrateBatchTask.setTargetDataSource(target);

        // todo mock db
        sourceMockDBIndex.put(migrateBatchTask.getSourceTableName(), migrateBatchTask);
        targetMockDBIndex.put(migrateBatchTask.getTargetTableName(), migrateBatchTask);
        migrateBatchTableMockDB.put(migrateBatchTask.getBatchNo(), migrateBatchTask);

        // 模拟新对象
        return BeanUtil.copyProperties(migrateBatchTask, MigrateBatchTask.class);
    }

    @Override
    public MigrateBatchTask getByBatchNo(String batchNo) {
        MigrateBatchTask task = migrateBatchTableMockDB.get(batchNo);
        if (Objects.isNull(task)) {
            return null;
        }
        return BeanUtil.copyProperties(task, MigrateBatchTask.class);
    }

    @Override
    public MigrateBatchTask getActiveMigrateBatchTask() {
        for (MigrateBatchTask task : migrateBatchTableMockDB.values()) {
            // 只要进行中的
            if (Objects.equals((byte) 1, task.getBatchState())
                    && (!task.isLocked() ||
                    Objects.isNull(task.getLockTime()) || System.currentTimeMillis() - task.getLockTime() > 5 * 60000)) {
                task.setLocked(true);
                task.setLockTime(System.currentTimeMillis());
                updateTask(task);

                // 模拟新对象
                return BeanUtil.copyProperties(task, MigrateBatchTask.class);
            }
        }
        return null;
    }

    @Override
    public void updateTask(MigrateBatchTask task) {
        MigrateBatchTask batchTask = BeanUtil.copyProperties(task, MigrateBatchTask.class);
        migrateBatchTableMockDB.put(batchTask.getBatchNo(), batchTask);
        sourceMockDBIndex.put(batchTask.getSourceTableName(), batchTask);
        targetMockDBIndex.put(batchTask.getTargetTableName(), batchTask);
    }
}
