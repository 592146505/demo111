package com.example.demo111.resp;

import lombok.Data;

/**
 * @author zgl
 * @version v1.0
 * @since 2022/5/18 16:53
 */
@Data
public class MigrateInfoResp {
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
}
