FROM maven:3.8.5-openjdk-17 AS build
MAINTAINER yauritux@gmail.com

COPY . /usr/local/atm-service

WORKDIR /usr/local/atm-service/

RUN mvn -Dmaven.test.skip=true clean package

FROM openjdk:17-jdk

WORKDIR /app

COPY --from=build "/usr/local/atm-service/core/target/core-1.0.jar" /app/core-1.0.jar
COPY --from=build "/usr/local/atm-service/cli-application/target/classes/link" /app/link

CMD ["java", "-cp", ".:./core-1.0.jar", "link.yauritux.AppRunner"]