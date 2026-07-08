#build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /build

ARG SERVICE_NAME

COPY . .

RUN mvn clean package -pl ${SERVICE_NAME} -am -DskipTests

#run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ARG SERVICE_NAME

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=builder /build/${SERVICE_NAME}/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]