# Agent workflow

Use Codex with this repository as the working directory. Start each task by reading [README.md](README.md), this file, and the relevant Java implementation and tests. Implement the solution exclusively in the [Java module](java/) using JDK 17 and Maven as configured in [java/pom.xml](java/pom.xml).

## Development and verification

1. Run `mvn -f java/pom.xml test` from the repository root to establish a baseline for the Java calculator.
2. Make changes in focused increments. Prefer hexagonal architecture so interest rules can be tested independently of HTTP and database adapters.
3. Rerun the Java tests after refactoring. Verify both endpoints and the persisted balance updates, preferably with database integration tests using Testcontainers.
4. Provide an OpenAPI contract and document how to start the application and exercise both endpoints in Swagger.
5. Keep commits atomic and develop in a personal fork. Do not create branches or pull requests in the upstream ikigai-digital repository.
6. Use Spring Boot as base development framework. Utilize JPA, JQL etc. instead of direct JDBC calls.
7. Use gvenzl/oracle-xe:21-slim-faststart as main testcontainer


## AI-assisted development record

Codex used the assignment README and repository layout to draft this workflow. Continue using the workflow for implementation, testing, and refactoring. For the submission, record the tools and models actually used, how to reproduce the setup, any additional prompts or configuration, and which parts were AI-assisted and why.

## Implemenatation tracking
Store the history of all prompts used in PROMPT_HISTORY.md


