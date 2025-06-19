# Java 21 JDK 이미지로 빌드
FROM eclipse-temurin:21-jdk as builder

WORKDIR /app

COPY . /app

RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test -x check

# 실행 환경은 JRE로 가볍게
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
