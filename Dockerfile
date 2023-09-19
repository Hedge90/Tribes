FROM amazoncorretto:17
MAINTAINER davidkasza
COPY . /app
WORKDIR /app
RUN ./gradlew build -x test
ENTRYPOINT ["java","-jar","./build/libs/springwebapp-0.0.1-SNAPSHOT.jar"]