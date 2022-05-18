package com.example.demo111.req;

import lombok.Data;

/**
 * @author zgl
 * @version v1.0
 * @since 2022/5/18 15:35
 */
@Data
public class MigrateReq {

    private DataBaseInfo sourceDataSource;

    private DataBaseInfo targetDataSource;

    /**
     * 单次条数，默认50
     */
    private int size = 50;
}
