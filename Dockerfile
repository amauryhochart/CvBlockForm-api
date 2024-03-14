FROM openjdk:21
VOLUME /tmp
COPY target/*.jar app.jar
COPY src/main/resources /resources
#SPRING_PROFILE=local for dev use
ENV SPRING_PROFILE=prod
ENTRYPOINT ["java","-jar","/app.jar"]