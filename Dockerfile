FROM openjdk:21-slim
ENV TZ="Europe/Moscow"
COPY /target/*.jar daily-bot.jar
ENTRYPOINT exec java $JAVA_ARG -jar /daily-bot.jar
