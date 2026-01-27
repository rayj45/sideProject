# 1단계: 빌드 환경 (Gradle 빌드)
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# 권한 문제 방지를 위해 gradlew에 실행 권한 부여
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# 2단계: 실행 환경 (JRE만 포함하여 용량 최적화)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 빌드 단계에서 생성된 jar 파일만 복사
# 빌드 시 생성되는 파일이 여러 개일 수 있으므로 plain이 붙지 않은 실행 가능한 jar를 선택합니다.
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

# 컨테이너 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]