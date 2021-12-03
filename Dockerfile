FROM openjdk:11
VOLUME /tmp
EXPOSE 8088
ARG JAR_FILE=target/search-busan-shop-api-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/app.jar"]
