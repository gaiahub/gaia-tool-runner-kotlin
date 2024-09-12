#!/bin/sh

echo "port is $PORT"
echo "jdbc is $JDBC_CONNECTION_STRING"

echo "Starting the Gaia Tool Runner"

# Schedule the task to run in 1 minute
echo java -jar /app/gaiaRunner.jar --port $PORT --jdbc "$JDBC_CONNECTION_STRING"
# java -jar /app/gaiaRunner.jar --port $PORT --jdbc "$JDBC_CONNECTION_STRING"

# Start the docker daemon
echo "Starting the Docker Daemon"
nohup dockerd > /dev/null 2>&1 &

sleep 10

# Start the Gaia Tool Runner
echo "Starting the Gaia Tool Runner"
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar /app/gaiaRunner.jar --port $PORT --jdbc "$JDBC_CONNECTION_STRING"
