FROM adoptopenjdk/openjdk11:alpine-jre

ARG NAME
ARG VERSION
ARG JAR_FILE=target/${NAME}-${VERSION}.jar

# cp target/m3u-file-parser-${VERSION}.jar /app.jar
COPY ${JAR_FILE} /app.jar

EXPOSE 8080

# java -jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
