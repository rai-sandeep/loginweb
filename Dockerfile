FROM maven as builder
WORKDIR /app
COPY . .
RUN mvn clean install


FROM openjdk:8-jre-alpine
EXPOSE 8080
WORKDIR /app
COPY --from=builder /app/target/login-0.0.1-SNAPSHOT.jar api.jar
CMD java -jar api.jar