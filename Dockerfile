FROM gradle

WORKDIR /usr/src/github.com/idean3885/BaseProject

COPY . .

RUN ./gradlew clean build

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "./build/libs/base-0.0.2.jar"]