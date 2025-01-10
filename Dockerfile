FROM eclipse-temurin:21-jdk

WORKDIR /home/app
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle.kts ./
COPY settings.gradle.kts ./
COPY src ./src

RUN chmod +x gradlew
RUN ./gradlew clean build --no-daemon

RUN mv build/libs/enigma-0.0.1-SNAPSHOT.jar /home/app/enigma.jar
CMD ["java", "-jar", "/home/app/enigma.jar"]

