package com.example.moneyway.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "dbTaskExecutor")
    public Executor dbTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 기본 스레드 수: CPU 코어 수와 동일하게 설정
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        // 최대 스레드 수: CPU 코어 수의 2배로 설정 (I/O 작업이 많을 경우를 대비)
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        // 큐 용량
        executor.setQueueCapacity(50);
        // 스레드 이름 접두사
        executor.setThreadNamePrefix("db-task-");
        executor.initialize();
        return executor;
    }
}