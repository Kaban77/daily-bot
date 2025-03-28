FROM eclipse-temurin:24_36-jre-noble
ENV TZ="Europe/Moscow"
COPY /target/*.jar daily-bot.jar
ENTRYPOINT exec java $JAVA_ARG -jar /daily-bot.jar
