FROM alpine:3.20.2

WORKDIR /usr/src/app

ENV PORT=8041
EXPOSE 8041

COPY run.sh .


ENTRYPOINT ["/bin/sh","./run.sh"]