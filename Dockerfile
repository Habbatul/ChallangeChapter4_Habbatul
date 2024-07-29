FROM adoptopenjdk/openjdk16:alpine-jre
COPY /target/challange4-0.0.1-SNAPSHOT.jar /app/challangebinar.jar
CMD ["java", "-jar", "/app/challangebinar.jar"]
