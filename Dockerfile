FROM openjdk:11-jdk

CMD ["./mvnw", "clean","package"]

ARG JAR_FILE_PATH=target/*.jar

COPY ${JAR_FILE_PATH} JJMToyProject.jar

ENTRYPOINT ["java","-jar","JJMToyProject.jar"]