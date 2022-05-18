package com.example.demo111.req;

import lombok.Data;

import java.util.Objects;

/**
 * @author roamer
 * @version v1.0
 * @since 2022/5/18 18:34
 */
@Data
public class DataBaseInfo {

    private String url;

    private String username;

    private String password;

    private String tableName;


    // todo 程序测试用
    private Boolean isOracle;

    public Boolean getIsOracle() {
        return !Objects.isNull(isOracle) && isOracle;
    }
}
