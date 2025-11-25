FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN ./gradlew clean build -x test

EXPOSE 8080

CMD ["java", "-jar", "build/libs/mutantes-ds-0.0.1-SNAPSHOT.jar"]
