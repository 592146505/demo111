package com.example.demo111.dto;

import com.example.demo111.req.DataBaseInfo;
import lombok.Data;

/**
 * @author roamer
 * @version v1.0
 * @since 2022/5/18 21:34
 */
@Data
public class MigrateBatchTask {

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 表名
     */
    private String sourceTableName;

    /**
     * 表名
     */
    private String targetTableName;

    /**
     * 总数量
     */
    private Integer totalCount;

    /**
     * 每次同步大小
     */
    private Integer size;

    /**
     * 总页数
     */
    private Integer totalPage;

    /**
     * 最后一次同步成功的页码
     */
    private Integer lastSyncPage;

    /**
     * 最后一次同步成功的rowId
     */
    private Integer lastRowId;

    /**
     * 1进行中 2成功 3 失败
     */
    private Byte batchState;

    /**
     * 锁定
     */
    private boolean locked;

    /**
     * 锁定时间
     */
    private Long lockTime;

    /**
     * 失败原因
     */
    private String errorMessage;

    private DataBaseInfo sourceDataSource;

    private DataBaseInfo targetDataSource;
}
