FROM openjdk:20
COPY "./target/tfgCache-1.0-SNAPSHOT.jar" "app.jar"
ENTRYPOINT ["java","-jar","app.jar"]