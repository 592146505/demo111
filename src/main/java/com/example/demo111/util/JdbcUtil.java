package com.example.demo111.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author roamer
 * @version v1.0
 * @since 2022/5/18 18:30
 */
public class JdbcUtil {

    private static final Map<String, JdbcTemplate> cache = new ConcurrentHashMap<>(2);

    public static JdbcTemplate jdbcTemplate(String url, String username, String password) {
        if (cache.containsKey(url)) {
            return cache.get(url);
        }
        JdbcTemplate jdbcTemplate;
        synchronized (cache) {
            if (cache.containsKey(url)) {
                return cache.get(url);
            }
            //todo 检验连接是否有效（用户名，密码等错误可能造成无效），无效则不加入缓存
            jdbcTemplate = new JdbcTemplate(getDataSource(url, username, password),false);
            cache.put(url, jdbcTemplate);
        }
        return jdbcTemplate;
    }

    private static DataSource getDataSource(String url, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        return new HikariDataSource(config);
    }
}
