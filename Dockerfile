# 🔧 1단계: Gradle 빌드용 컨테이너
FROM gradle:8.7.0-jdk21-alpine AS builder

WORKDIR /app

# 종속성 캐시를 위해 먼저 gradle 파일들만 복사
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# 종속성만 미리 다운받아서 캐시 사용
RUN chmod +x gradlew && ./gradlew dependencies

# 전체 소스 복사
COPY src ./src

# 실행 JAR 파일명을 고정시키면 Dockerfile이 깔끔해짐
RUN ./gradlew clean bootJar -x test

# 🔧 2단계: 경량 런타임 이미지 (JRE only)
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Spring profile 고정
ENV SPRING_PROFILES_ACTIVE=production

# JAR 복사 (JAR 이름 고정했으면 단순하게 가능)
COPY --from=builder /app/build/libs/MoneyWay-0.0.1-SNAPSHOT.jar app.jar

# 포트 노출
EXPOSE 8080

# 헬스체크 (Spring Boot actuator를 사용하는 경우)
# HEALTHCHECK --interval=30s --timeout=5s CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
