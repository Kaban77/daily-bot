FROM eclipse-temurin:22.0.1_8-jre-jammy
ENV TZ="Europe/Moscow"
COPY /target/*.jar daily-bot.jar
ENTRYPOINT exec java $JAVA_ARG -jar /daily-bot.jar
