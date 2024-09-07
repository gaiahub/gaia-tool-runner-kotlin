# Gaia Tool Runner

This is a simple tool to run the Gaia tool on a given input file. 

## Build the Jar
    
```bash
./gradlew buildFatJar
```

## Build the image

```bash
docker build --progress=plain -t gaiahub/gaia-tool-runner:latest -f ./docker/tool-runner-controller/Dockerfile .
 
```

## Run the image

```bash
docker run -it --rm -v $(pwd)/data:/data gaiahub/gaia-tool-runner:latest
```

## Usage


