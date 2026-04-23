# http-client-timeout

## Running the experiment

1. Run the server:

```shell
./gradlew :app-server:quarkusDev
```

2. In a separate terminal session run the client:

```shell
./gradlew :app-client:run
```

## Moving parts

* **app-server**\
Quarkus application, HTTP server.\
Runs on port 8000.\
Endpoint `/slow` serves 100 lines of code in chunked mode. It makes a pause after every line.

* **app-client**\
Plain Java application to run the experiment.

* **lib-apache-http-client-impl**\
Client library that uses Apache HTTP Client under the hood

* **lib-jdk-http-client-impl**\
Client library that uses JDK HTTP Client under the hood

## The Experiment

There is one client (**request timeout**) and two server (**initial delay** and **running delay**) parameters you can adjust:

* `app-server/src/main/resources/application.properties`
  * **example.server.app.delay.initial** – delay before the server sends any bytes in response. Emulates querying a database
  * **example.server.app.delay.running** – delay after every line. Emulates slow chunked response.
* `example.client.app.ClientApp.REQUEST_TIMEOUT` – configured request timeout for an HTTP client
  
1. First, set **request timeout** so that it is larger than **initial delay** plus 100 times **running delay**. This corresponds to a happy case.
2. Next, set **request timeout** slightly larger than **initial delay**, but much less than in the first case. What would you expect?
3. Last, set **request timeout** smaller than **initial delay**. A request should fail