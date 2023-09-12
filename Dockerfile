FROM gradle:7.4 AS build

WORKDIR /usr/src/github.com/idean3885/BaseProject

COPY . .

RUN ./gradlew build

FROM openjdk:11-jre-slim

COPY --from=build /usr/src/github.com/idean3885/BaseProject/build/libs/base-0.0.2.jar /build/base.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/build/base.jar"]