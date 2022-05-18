package com.example.demo111.service;

import com.example.demo111.req.DataBaseInfo;
import com.example.demo111.util.JdbcUtil;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author roamer
 * @version v1.0
 * @since 2022/5/18 18:31
 */
@Service
public class MigrateService {

    public int migrate(DataBaseInfo source, DataBaseInfo target, String tableName) {
        JdbcTemplate sourceJdbcTemplate = JdbcUtil.jdbcTemplate(source.getUrl(), source.getUsername(), source.getPassword());
        // 先查询数量
        Integer count = sourceJdbcTemplate.query("select count(1)as cn from " + tableName, (RowMapper<Integer>) (rs, rowNum) -> {
            return rs.getInt("cn");
        }).get(0);
        if (count == 0) {
            return 0;
        }

        List<String> columns = new ArrayList<>();
        int size = 200;
        int totalPage = count % size == 0 ? count / size : count / size + 1;
        int page = 1;
        int start = (page - 1) * 200;

        // 分页查询
        List<Map<String, Object>> list = new ArrayList<>();
        do {
            list = sourceJdbcTemplate.query("select * from " + tableName + " limit " + start + "," + size, (RowMapper<Map<String, Object>>) (rs, rowNum) -> {
                Map<String, Object> map = new LinkedHashMap<>();
                if (columns.isEmpty()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    for (int i = 0; i < metaData.getColumnCount(); i++) {
                        columns.add(metaData.getColumnLabel(i + 1));
                    }
                }

                columns.forEach(column -> {
                    try {
                        map.put(column, rs.getObject(column));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                return map;
            });
            if (!list.isEmpty()) {
                StringBuilder commonValuesStr = new StringBuilder("");
                columns.forEach(column -> {
                    commonValuesStr.append("?,");
                });
                commonValuesStr.delete(commonValuesStr.length() - 1, commonValuesStr.length());
                // 插入
                JdbcTemplate targetJdbcTemplate = JdbcUtil.jdbcTemplate(target.getUrl(), target.getUsername(), target.getPassword());
                StringBuilder sqlBuilder = new StringBuilder("");
                sqlBuilder.append("insert into ");
                sqlBuilder.append(tableName);
                sqlBuilder.append(" values");
                for (int i = 0; i < list.size(); i++) {
                    sqlBuilder.append("( ");
                    sqlBuilder.append(commonValuesStr.toString());
                    sqlBuilder.append(" ),");
                }
                sqlBuilder.delete(sqlBuilder.length() - 1, sqlBuilder.length());
                sqlBuilder.append(";");
                // insert into tb_order_1 values(?,?,?,?,?,?,? ),(?,?,?,?,?,?,? )

                List<Object[]> finalList = list.stream()
                        .map(m -> m.values().toArray())
                        .collect(Collectors.toList());
                targetJdbcTemplate.batchUpdate(sqlBuilder.toString(), new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Object[] data = finalList.get(i);
                        for (int j = 0; j < data.length; j++) {
                            ps.setObject(j + 1, data[j]);
                        }
                    }

                    @Override
                    public int getBatchSize() {
                        return finalList.size();
                    }
                });
            }

            ++page;

        } while (list.size() == size && page < totalPage);
        return count;

    }
}
