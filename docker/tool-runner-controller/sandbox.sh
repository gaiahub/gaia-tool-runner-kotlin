#!/bin/sh

# Display usage information
show_help() {
  echo "Usage: sandbox.sh [OPTIONS]"
  echo "Options:"
  echo "  -c, --create                  Create a new sandbox environment"
  echo "  -l, --list                    List available sandboxes images"
  # echo "  -lc, --list-containers  List running sandboxes"
  echo "  -r, --run                     Run a tool in the sandbox environment"
  echo "  -r-g, --run-generic           Run a tool in the generic sandbox environment"
  echo "  -d, --delete                  Delete the sandbox environment"
  echo "  -h, --help                    Display this help message"
}

# Build a new Docker image from a Dockerfile
build_docker_image() {
  dockerfile_path=$1
  image_name=$2
  base_path=$3

  if [ -z "$dockerfile_path" ] || [ -z "$image_name" ] || [ -z "$base_path" ]; then
    echo "Error: Dockerfile path and image name must be provided."
    echo "Usage: sandbox.sh --create <Dockerfile path> <image name> <base path>"
    exit 1
  fi

  echo "Building Docker image from $dockerfile_path with name $image_name..."
  echo docker build -f "$dockerfile_path" -t "sandbox-$image_name" "$base_path"
  docker build -f "$dockerfile_path" -t "sandbox-$image_name" "$base_path"
  exit 0
}

# Run a generic sandbox
run_sandbox_generic() {
  image_name=$1
  program_path=$2
  container_name=$3

  if [ -z "$image_name" ] || [ -z "$program_path" ] || [ -z "$container_name" ]; then
    echo "Error: Image name and index file path must be provided."
    echo "Usage: sandbox.sh --run-generic <image name> <index file path> <sandbox name> <start_command>"
    exit 1
  fi

  absolute_path=$(dirname "$program_path")

  echo docker create --name "$container_name" "$image_name"
  docker create --name "$container_name" "$image_name"

  echo docker cp "$program_path" "$container_name:/usr/src/app/"
  docker cp "$program_path" "$container_name:/usr/src/app/"

  echo docker start -a "$container_name"
  result=$(docker start -a "$container_name")
  echo "PROGRAM-OUTPUT-BEGIN"
  echo "$result"
  echo "PROGRAM-OUTPUT-END"
  docker rm "$container_name"
  exit 0
}

# Run a new sandbox
run_sandbox() {
  image_name=$1
  program_path=$2
  container_name=$3

  if [ -z "$image_name" ] || [ -z "$program_path" ] || [ -z "$container_name" ]; then
    echo "Error: Image name and index file path must be provided."
    echo "Usage: sandbox.sh --run <image name> <index file path> <sandbox name>"
    exit 1
  fi

  absolute_path=$(dirname "$program_path")
  result=$(docker run --rm --name "$container_name" -v "$absolute_path:/usr/src/app" "$image_name" node /usr/src/app/index.js)

  echo "$result"
  exit 0
}

# delete a new sandbox
delete_sandbox() {
  image_name=$1

  if [ -z "$image_name" ] ; then
    echo "Error: Image name"
    echo "Usage: sandbox.sh --delete <sandbox name>"
    exit 1
  fi

  #echo "Creating a new Docker container from image $image_name..."
  docker rm "$image_name"

  exit 0
}

list_sandboxes_images() {
  # Define the command and column names
  command="docker image ls | grep sandbox"
  column_names="IMAGE NAME TAG IMAGE ID CREATED SIZE"

  # Execute the command and capture the output
  output=$(eval $command)

  # Print the column headers
  printf "%-20s %-10s %-15s %-15s %-10s\n" $column_names
  echo "--------------------------------------------------------------------------------"

  # Process the output
  echo "$output" | while IFS= read -r line; do
    # Use awk to split the line into columns
    set -- $line
    columns="$1 $2 $3 $4 $5 $6 $7 $8 $9 $10"

    # Print the image data in columns
    printf "%-20s %-10s %-15s %-15s %-10s\n" $columns
  done
}

# Check if no arguments were provided
if [ $# -eq 0 ]; then
  show_help
  exit 1
fi

# Parse command line arguments
while [ $# -gt 0 ]; do
  case $1 in
    -c|--create)
      build_docker_image $2 $3 $4
      shift 4 # past argument
      ;;
    -l|--list)
      list_sandboxes_images
      shift # past argument
      ;;
    -r|--run)
      run_sandbox $2 $3 $4
      shift 4 # past argument
      ;;
    -rg|--run-generic)
      run_sandbox_generic $2 $3 $4 $5
      shift 5 # past argument
      ;;
    -d|--delete)
      delete_sandbox $2
      shift 2 # past argument
      ;;
    -h|--help)
      show_help
      exit 0
      ;;
    *)
      echo "Unknown option: $1"
      show_help
      exit 1
      ;;
  esac
done
