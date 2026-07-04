FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /build

COPY shopflow-identity/pom.xml .
COPY shopflow-shared/pom.xml shopflow-shared/
COPY shopflow-identity/pom.xml shopflow-identity/
COPY shopflow-inventory/pom.xml shopflow-inventory/
COPY shopflow-order/pom.xml shopflow-order/

COPY shopflow-shared/src shopflow-shared/src
COPY shopflow-identity/src shopflow-identity/src

RUN mvn clean package -pl shopflow-identity -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=builder /build/shopflow-identity/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
