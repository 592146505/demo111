package com.example.demo111;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,JdbcRepositoriesAutoConfiguration.class,JdbcTemplateAutoConfiguration.class})
public class Demo111Application {


    public static void main(String[] args) {
        SpringApplication.run(Demo111Application.class, args);
    }

}
