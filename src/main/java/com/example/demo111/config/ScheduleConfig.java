package com.example.demo111.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zgl
 * @version v1.0
 * @since 2022/5/18 16:15
 */
@EnableScheduling
@Configuration
public class ScheduleConfig {

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // 核心线程
        taskExecutor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        // 最大线程.io繁忙型任务，适当放大
        taskExecutor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 5);
        // 等待队列
        taskExecutor.setQueueCapacity(Runtime.getRuntime().availableProcessors() * 10);
        // 空闲时间
        taskExecutor.setKeepAliveSeconds(10);
        // 拒绝策略：丢失当前任务
        // 因为有记录批次信息（虽然只是mock记录在内存中），所以丢弃只会拉长批次的总执行时间
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        return taskExecutor;
    }
}
