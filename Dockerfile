FROM openjdk:20-jdk-slim
COPY /target/*.jar daily-bot.jar
ENTRYPOINT exec java $JAVA_ARG -jar /daily-bot.jar
