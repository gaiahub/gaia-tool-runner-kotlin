FROM node:18-slim

WORKDIR /usr/src/app

# Add the package.json file with the allowed dependencies to the container
COPY ./package.json ./
RUN npm install

ENV PORT=8041
EXPOSE 8041

COPY run.sh .


CMD ["/bin/sh","./run.sh"]
