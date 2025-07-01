# 🔧 1단계: Gradle 빌드용 컨테이너
FROM gradle:8.7.0-jdk21-alpine AS builder

WORKDIR /app

# Gradle Wrapper 및 설정 파일 복사 (종속성 캐싱을 위해)
COPY gradlew .
COPY gradle gradle/
COPY build.gradle settings.gradle ./

# Gradle 종속성 다운로드 및 캐싱
# chmod +x gradlew는 Dockerfile 내부에서 한 번만 실행해도 됩니다.
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# 전체 소스 복사
COPY src ./src

# 실행 가능한 JAR 파일 빌드
# build.gradle에서 archiveFileName을 "app.jar"로 설정했으므로, 이 이름으로 JAR가 생성됩니다.
RUN ./gradlew clean bootJar -x test --no-daemon

# 🔧 2단계: 경량 런타임 이미지 (JRE only)
# 빌드된 JAR 파일을 실행하기 위한 최소한의 환경을 제공합니다.
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Spring profile 설정 (환경 변수로)
ENV SPRING_PROFILES_ACTIVE=production

# 빌더 스테이지에서 생성된 JAR 파일 복사
# build/libs/app.jar로 생성되었음을 확인했으므로 해당 경로를 사용합니다.
COPY --from=builder /app/build/libs/app.jar app.jar

# 애플리케이션 포트 노출
EXPOSE 8080

# 애플리케이션 실행 명령
# java -jar 명령으로 Spring Boot 애플리케이션을 실행합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]

# 헬스체크 (선택 사항 - Spring Boot Actuator를 사용하는 경우 활성화)
# HEALTHCHECK --interval=30s --timeout=5s CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1