#!/bin/sh

PORT=$1
JDBC_CONNECTION_STRING=$2

# Build the artifact
echo "Building the docker image..."
./gradlew :app:buildFatJar

sleep 2
# Build the docker image
echo "Copying the artifact to the docker image..."
docker build --progress=plain -t gaiahub/gaia-tool-runner:latest -f ./docker/tool-runner-controller/Dockerfile .

sleep 2
# Run the docker image
echo "Running the docker image..."
docker run  --name "tool-runner" -d -p $PORT:$PORT -p 5432:5432 --privileged --rm gaiahub/gaia-tool-runner:latest
sleep 5
echo "Starting Gaia Tool Runner in the container..."
#echo docker exec  tool-runner /app/docker/tool-runner-controller/bootstart.sh $PORT $JDBC_CONNECTION_STRING
docker exec tool-runner /app/docker/tool-runner-controller/bootstart.sh $PORT $JDBC_CONNECTION_STRING
