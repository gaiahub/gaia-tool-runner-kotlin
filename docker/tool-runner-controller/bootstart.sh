#!/bin/sh

echo "$@"

PORT=$1
JDBC_CONNECTION_STRING=$2

echo "port is $PORT"
echo "jdbc is $JDBC_CONNECTION_STRING"

echo "Creating Sandboxes..."
./docker/tool-runner-controller/sandbox.sh -c ./docker/runners/nodejs/Dockerfile node ./docker/runners/nodejs/.


echo "Starting the Gaia Tool Runner"

# Schedule the task to run in 1 minute
echo java -jar /app/gaiaRunner.jar --port $PORT --jdbc "$JDBC_CONNECTION_STRING"
java -jar /app/gaiaRunner.jar --port $PORT --jdbc "$JDBC_CONNECTION_STRING"