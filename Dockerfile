#FROM ubuntu/jre:8_edge
#
#EXPOSE 8080
#ADD /target/challange4-0.0.1-SNAPSHOT.jar binarfud
#ENTRYPOINT ["java", "-jar", "hanif.jar"]


FROM adoptopenjdk/openjdk8:jre8u382-b05-ubuntu

EXPOSE 8080

ADD /target/challange4-0.0.1-SNAPSHOT.jar binarfud
ENTRYPOINT ["java", "-jar", "binarfud"]
