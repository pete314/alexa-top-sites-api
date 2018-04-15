Build status: [![CircleCI](https://circleci.com/gh/pete314/alexa-top-sites-api/tree/master.svg?style=svg)](https://circleci.com/gh/pete314/alexa-top-sites-api/tree/master)

# Introduction
This project is built to showcase implementation examples of communly used REST functionality in Java runtime with Undertow JAX-RS server, Redis and Docker.
The project itself enables the paginated lookup for the Alexa Top 1 Million sites, with additional filtering by TLD.

# Requirements
Java 1.8+
+ [OpenJDK](http://openjdk.java.net/)

Maven 3+
+ [Maven](https://maven.apache.org/)

Redis 3.0+ 

+ [redis download](https://redis.io/download)
+ [redis docker](https://hub.docker.com/_/redis/)

Docker (optional)
+ [docker download](https://www.docker.com/get-docker) 

# Features
+ List Alexa top sites - ```GET /v0.1/alexa-top```
+ List Alexa top sites by TLD - ``` GET /v0.1/alexa-top?tld=com"```
+ Paginated response with [HATEOAS](https://en.wikipedia.org/wiki/HATEOAS) - ``` GET /v0.1/alexa-top?page=2&size=50```
+ Background data updating, with external content verification
+ Configuration via env vars, to help cluster deployment eg: kubernetes
+ General error handling with json only response
+ CORS support
+ Dynamic cache controll headers, to enable reverse proxy and browser caching
+ HTTP2 support
+ TLS and plain text server deployment
+ Docker container deployment

# Usage
Before you begin please check the correctness of the system environment variables, examples can be found in ```config/.env.example```.

In order to build the project Java JDK 1.8+, Maven and Redis is required. These can be either exist as a regular installation or available within container(s). 
In order to improve the build speed, the Alexa Top 1million sites file can be downloaded once, and the path can be passed via the ```TOP_SITES_PATH``` environment variable.

The default deployment contains a self signed cert, which can be replaced by specifying the required Java Key Store path with credential via the envirnoment variables ``` KEY_STORE_PASSWORD, KEY_STORE_PATH```

## Deploy OS
1. Navigate into the repository root
2. Build the project ``` mvn clean install ```
3. Run the project ``` java -jar -server target/java -jar target/alexa-top-api-1.0.jar```

## Deploy Docker
1. Navigate into the repository root
2. Build the project ``` mvn clean install ```
3. Build docker container ``` docker build --no-cache -t alexa-top-api:latest .```
4. Run docker container ``` docker run -it -p 8888:8888 -p 4443:4443 -e REDIS_MASTER_HOST=$DOCKER_REDIS_HOST  alexa-top-api:latest ```

Note that the env variable ```DOCKER_REDIS_HOST``` represnets a host only, where the service is listening on external calls. 

# HTTP Benchmarks 

HTTP benchmark run with [wrk](https://github.com/wg/wrk)

|request | http method | throughput (/sec) | latency(avg)| virtualization |
|---|---|---|---|---|
|/v0.1/alexa-top| GET | 16200 | 6.98 ms| - |
|/v0.1/alexa-top| GET | 14300 | 8.68 ms| Docker |
|/v0.1/alexa-top?page=2&tld=com| GET | 24100 | 4.78 ms| - |
|/v0.1/alexa-top?page=2&tld=com| GET | 10700 | 10.37 ms| Docker |


```bash
# Run benchmark without paramters
wrk -t8 -c 100 -d5s "http://127.0.0.1:8888/v0.1/alexa-top"

# Run benchmark with paramters
wrk -t8 -c 100 -d5s "http://127.0.0.1:8888/v0.1/alexa-top?page=2&tld=com"

```
# License
The project alexa-top-sites-api is licensed under the MIT license.

