# http-client-timeout

## Running the experiment

1. Run the server:

```shell
./gradlew :app-server:quarkusDev
```

## Moving parts

### app-server

Quarkus application, HTTP server.

It runs on port 8000 by default. Endpoint `/slow` serves 100 lines of code in chunked mode. It makes a pause after every line.