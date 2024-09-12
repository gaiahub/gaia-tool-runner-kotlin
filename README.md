# Gaia Tool Runner

This is a simple tool to run the Gaia tool on a given input file. 

## Building the image
    
```bash
./build_image.sh
```


## Running the image

```bash
PORT=8020
JDBC_CONNECTION_STRING=jdbc:postgresql://postgres:5432/postgres

docker run gaia-tool-runner $PORT $JDBC_CONNECTION_STRING
```

## Usage

#### Create the Sandboxes

```bash
curl -X GET  http://127.0.0.1:8020/v1/sandboxes/create
```


#### List all the available Gaia Tools
    
```bash
curl -X GET  http://127.0.0.1:8020/v1/tools
```

#### Run a Gaia Tool
    
```bash
curl -X GET  http://127.0.0.1:8020/v1/runTool/8a5c3f4e-e304-41b1-be20-20b8157a00c2
```

#### Run a Gaia Tool with a specific input file
    
```bash
curl -X POST http://127.0.0.1:8020/v1/runCode/node -F "file=@/Users/demo_user/workspace/program.js"
```