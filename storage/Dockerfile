FROM registry.opensource.zalan.do/stups/openjdk:latest

MAINTAINER Zalando SE

COPY build/libs/storage.jar /
COPY scm-source.json /

EXPOSE 8080

CMD java -Xmx512m $(appdynamics-agent) -jar /storage.jar
