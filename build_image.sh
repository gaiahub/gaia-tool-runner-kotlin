#!/bin/sh
docker build --progress=plain -t "gaia-tool-runner"  -f ./docker/tool-runner-controller/Dockerfile .