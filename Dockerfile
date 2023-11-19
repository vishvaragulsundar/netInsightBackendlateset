FROM alpine:3.18.0

ENV JAVA_HOME="/usr/lib/jvm/default-jvm/"
RUN apk add --no-cache openjdk11
ENV PATH=$PATH:${JAVA_HOME}/bin

ARG JAR_FILE=build/libs/netinsight-1.jar
COPY ${JAR_FILE} app.jar
ENV APP_HOME=/usr/app
WORKDIR $APP_HOME
ENTRYPOINT ["java","-jar","/app.jar","--spring.config.location=classpath:/application.properties,optional:file:/usr/app/overrideApplication.properties"]
