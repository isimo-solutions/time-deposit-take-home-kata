# AI Tools used: GPT-6.2 Sol (initalliy Light, next prompts High, VS Code with Java Extensions, Codex extension for VS Code)

All steps in the development of this solution have been implemented using AI Tools (including tests and documentation, the history of prompts is stored in PROMPT_HISTORY.md)

# Prompt history

## Agent setup

> Set up initial agent workflow file based on the information from README.md

> Modify the instructions to concentrate on Java only, implementation in other languages will not be required

## Java API

> - Create a RESTful API endpoint to update the balances of all time deposits in the database.
> - Create a RESTful API endpoint to retrieve all time deposits.
>   - The GET endpoint should return a list of all time deposits with the following schema:
>     - `id`
>     - `planType`
>     - `balance`
>     - `days`
>     - `withdrawals`

Codex implemented the Spring Boot endpoints, JDBC persistence, OpenAPI contract, and integration tests. The tests exercise the database and HTTP mappings together to check that updates are persisted and withdrawals are grouped with their deposits.

## Java package separation

> Split platform dependent java classes (Springboot, etc. ) and platform independent java classes (Business logic and data classes) into separate Java packages

Codex moved the Spring Boot and JDBC classes into the infrastructure package. The framework-free models and calculator retain their existing package so callers can keep their imports.

## Oracle XE integration tests

> Change the tests to utilize Oracle testcontainer instead of h2 db

Codex configured the API integration tests to start Oracle XE with Testcontainers and adapted the database schema, fixtures, and JDBC reads for Oracle compatibility.

## JPA persistence

> Change direct JDBC calls to JPA and JQL

Codex mapped the existing tables as JPA entities and used JPQL for queries and test cleanup. This keeps the calculator and response data independent of the persistence framework while preserving transactional balance updates.

## Interest calculation

> Implement logic for
>
> 1. **Interest Calculation**:
>    - Implement logic to calculate monthly interest based on the plan type:
>      - **Basic Plan**: 1% interest
>      - **Student Plan**: 3% interest (no interest after 1 year)
>      - **Premium Plan**: 5% interest (interest starts after 45 days)
>    - No interest is applied for the first 30 days for any existing plans.

Codex made the existing rates and eligibility rules explicit and added calculator assertions for the day cutoffs and cent rounding.

## Command-line and Swagger walkthrough

> Describe how to start the application from the command line and access OpenAPI Swagger using browser, add this description in the HOWTO.md

Codex wrote a PowerShell walkthrough for starting the application and using the OpenAPI contract in Swagger UI to exercise both endpoints.

## Bundled Swagger UI

> Rather include this index.html directly in the application sources to make it always available

Codex added the Swagger page to the application resources and packaged the OpenAPI contract and Swagger UI assets with the app. The walkthrough now uses the normal startup command.

## Monthly interest assumptions

> It's unclear what is the definition of month in the current specs when speaking about monthly interest - document in the comments what assumptions has been made about meaning of the monthy here

Codex documented the fixed monthly rate, elapsed-day eligibility, and caller-controlled posting schedule in the Java source.

## Oracle XE for local development

> Use Oracle image also when running the application from the command line instead of using H2 database, mention in HOWTO.md, that Docker is required to start application

Codex configured Docker Compose and Spring Boot to use Oracle XE locally. The application and integration tests share the schema script, and the startup guide documents the Docker prerequisite and commands.

## One-command local startup

> make sure that the Oracle docker testcontainer starts when the application is started locally by the developer, create a specific spring profile for this type of launch and document it in HOWTO.md

> include starting the docker database in maven call using docker maven plugins to have the development environment started with one command line, if  db container is already started starting it should be skipped

Codex added a Maven profile that runs Docker Compose before launching Spring Boot with its local profile. Compose preserves the running database between launches.
