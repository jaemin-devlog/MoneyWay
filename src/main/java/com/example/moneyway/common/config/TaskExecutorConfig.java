package com.example.moneyway.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class TaskExecutorConfig {

    /**
     * 데이터 동기화와 같은 병렬 작업을 위한 전용 스레드 풀을 생성합니다.
     * CPU 코어 수의 2배만큼 스레드를 생성하여 I/O 작업에 최적화합니다.
     * @return ExecutorService 빈
     */
    @Bean(name = "dataSyncTaskExecutor")
    public ExecutorService dataSyncTaskExecutor() {
        // 시스템의 가용 프로세서 수를 확인하여 동적으로 스레드 수를 조절
        int coreCount = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(coreCount * 2);
    }
}