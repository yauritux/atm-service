# ATM-Service

## Prerequisites

- Docker

## Running the App

1. Build docker image (execute the command from the root project directory).
```shell
docker build --no-cache -t atm-service:latest .
```
2. Run the image.
```shell
docker container run -it --rm atm-service:latest
```