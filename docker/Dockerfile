FROM openjdk:8
LABEL maintainer="Jon Freer"
LABEL maintainerEmail="freerjm@miamioh.edu"

COPY target/noti-consumer*.jar noti-consumer/noti-consumer.jar
COPY src/main/java/configuration/configuration.yml noti-consumer/configuration.yml
CMD java -jar noti-consumer/noti-consumer.jar noti-consumer/configuration.yml
