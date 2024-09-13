FROM docker:20.10-dind

RUN apk update && apk add openjdk17
#RUN apk add supervisor

# RUN mkdir -p /tmp
COPY . /tmp
WORKDIR /tmp
RUN ./gradlew :app:buildFatJar

## Sed working dir to /usr/src/app
RUN mkdir -p /app
WORKDIR /app

## Copy the buildho
RUN cp /tmp/app/build/libs/gaiaRunner.jar ./gaiaRunner.jar

COPY ./docker/runners/ ./docker/runners/
COPY ./docker/tool-runner-controller/sandbox.sh ./docker/tool-runner-controller/
COPY ./docker/tool-runner-controller/bootstart.sh ./docker/tool-runner-controller/
RUN chmod +x ./docker/tool-runner-controller/bootstart.sh

## Set environment variables
ENV PORT=8020
ENV JDBC_CONNECTION_STRING="jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:6543/postgres?user=postgres.icmpfgtzjhdyjfioisip&password=3EeGTc\$3YdPYMR!"


## Network port
EXPOSE $PORT

# Starting the tool-runner-controller
CMD ["/app/docker/tool-runner-controller/bootstart.sh"]