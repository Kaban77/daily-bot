FROM bellsoft/liberica-openjre-alpine:20.0.1-10
ENV TZ="Europe/Moscow"
COPY /target/*.jar daily-bot.jar
ENTRYPOINT exec java $JAVA_ARG -jar /daily-bot.jar
