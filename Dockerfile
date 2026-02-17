FROM openjdk:17-jdk

COPY /target/playgame-0.0.1-SNAPSHOT.jar /playgame.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/playgame.jar"]
