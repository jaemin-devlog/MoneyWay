# 🔧 1단계: Gradle 빌드용 컨테이너
FROM gradle:8.7.0-jdk21-alpine AS builder

WORKDIR /app

# 종속성 캐시를 위해 먼저 gradle 파일들만 복사
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# 종속성만 먼저 받아서 캐시 활용
RUN chmod +x gradlew && ./gradlew dependencies

# 이후 전체 소스 복사
COPY src ./src

# 테스트 제외하고 빌드
RUN ./gradlew build -x test

# 🔧 2단계: 런타임 이미지 (JRE만 있는 경량 이미지)
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# 환경 변수로 profile 지정 (Render에서도 중복 적용 대비)
ENV SPRING_PROFILES_ACTIVE=production

# JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 포트 명시 (Render에선 무시되지만 도커 run 시 유용)
EXPOSE 8080

# 헬스 체크 (옵션, 필요시 주석 해제)
# HEALTHCHECK --interval=30s --timeout=5s CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
