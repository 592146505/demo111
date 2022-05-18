package com.example.demo111.req;

import lombok.Data;

/**
 * @author roamer
 * @version v1.0
 * @since 2022/5/18 18:35
 */
@Data
public class MigrateReq {

    private DataBaseInfo sourceDataSource;

    private DataBaseInfo targetDataSource;

    private String tableName;
}
