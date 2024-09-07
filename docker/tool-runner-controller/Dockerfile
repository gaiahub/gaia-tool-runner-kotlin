FROM docker:20.10-dind

RUN apk update && apk add openjdk17
#RUN apk add supervisor

## Sed working dir to /usr/src/app
RUN mkdir -p /app
WORKDIR /app
#
## Copy the buildho
COPY ./app/build/libs/gaiaRunner.jar .
COPY ./docker/runners/ ./docker/runners/
COPY ./docker/tool-runner-controller/sandbox.sh ./docker/tool-runner-controller/
COPY ./docker/tool-runner-controller/bootstart.sh ./docker/tool-runner-controller/
RUN chmod +x ./docker/tool-runner-controller/bootstart.sh
#COPY ./docker/tool-runner-controller/supervisord.conf /etc/supervisord.conf


## Network port
EXPOSE 8020
#
# CMD ["sh", "/app/docker/tool-runner-controller/bootstart.sh"]