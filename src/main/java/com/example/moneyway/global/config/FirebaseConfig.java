package com.example.moneyway.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration // Spring이 설정 클래스로 인식
public class FirebaseConfig {

    @Bean // FirebaseApp을 Bean으로 등록
    public FirebaseApp initializeFirebase() throws IOException {
        // resources 폴더에서 Firebase 서비스 키 파일을 읽어옴
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new ClassPathResource("firebase-secret.json").getInputStream()
        );

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        // FirebaseApp은 한 번만 초기화되어야 함
        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options); // 최초 초기화
        } else {
            return FirebaseApp.getInstance(); // 이미 초기화된 경우 재사용
        }
    }
}
