# 🔧 1단계: Gradle 빌드용 컨테이너
FROM gradle:8.7.0-jdk21-alpine AS builder

WORKDIR /app

# 프로젝트 관련 파일 복사
COPY gradle ./gradle
COPY gradlew ./gradlew
COPY build.gradle ./build.gradle
COPY settings.gradle ./settings.gradle
COPY src ./src

# gradlew 실행 권한 추가 (Windows에선 필수)
RUN chmod +x gradlew

# 종속성 미리 다운 (캐시 활용) + 테스트 제외
RUN ./gradlew build -x test

# 🔧 2단계: 런타임 이미지
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# 위에서 만든 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
