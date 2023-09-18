FROM openjdk:20
COPY "./target/tfgCache-1.0-SNAPSHOT.jar" "app.jar"
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]