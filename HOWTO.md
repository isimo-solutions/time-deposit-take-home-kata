# Run the API and open Swagger UI

You need JDK 17, Maven, and Docker with Docker Compose and Linux containers enabled. Docker must be running to start the application because it uses Oracle XE. The app serves Swagger UI with the [OpenAPI contract](java/openapi.yaml).

## Start the application

From the repository root, run the app with the Maven `local` profile.

```sh
mvn -f java/pom.xml -Plocal spring-boot:run
```

Maven runs Docker Compose and waits for Oracle XE to become healthy before starting Spring Boot with its `local` profile. If the Compose Oracle container is already running, Maven reuses it. The first run pulls `gvenzl/oracle-xe:21-slim-faststart`.

Spring Boot creates the tables on startup. Oracle listens at `127.0.0.1:1521` with service name `XEPDB1`, local user `time_deposit`, and local default password `time_deposit_dev`.

## Send requests from the browser

1. Wait for Spring Boot to report that the application has started. Open [Swagger UI](http://localhost:8080/index.html).
2. Expand **GET /time-deposits**, select **Try it out**, then **Execute**. Expect HTTP `200` and a JSON array. A fresh database returns `[]`.
3. Expand **POST /time-deposits/balance-updates**, select **Try it out**, then **Execute**. Expect HTTP `204` with an empty response body. Each call applies one monthly interest calculation to the stored deposits.
4. Run GET again to see the updated balances if the database contains eligible deposits.

Press **Ctrl+C** to stop the application. If Windows asks whether to terminate the batch job, enter **Y**. Oracle stays running for the next launch. To stop it, run this from the repository root.

```sh
docker compose -f java/compose.yaml down
```

Compose keeps the database volume so the data is available on the next startup.
