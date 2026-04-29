# Testing Strategy

The project applies a three-tier testing strategy, each with its own Maven profile and tooling.

## Unit Tests

**Framework:** JUnit 5 + Mockito  
**Location:** `src/test/java/com/example/library/` (excluding `integration/` and `performance/`)  
**Command:**

```bash
mvn test
```

Unit tests mock all external dependencies (database, other services) and run in-process using the H2 in-memory database. They are fast and should run on every commit.

Key test classes:
- `BookServiceTest` — validates book availability, add/remove logic
- `UserServiceTest` — validates user registration and lookup
- `BorrowingServiceTest` — validates borrow/return business rules

## Integration Tests

**Framework:** Spring Boot Test (`@SpringBootTest`) + real MySQL  
**Location:** `src/test/java/com/example/library/integration/`  
**Maven profile:** `integration`  
**Command:**

```bash
mvn -Pintegration integration-test
```

Integration tests start the full Spring context against a live database. In CI they rely on the MySQL Docker service defined in the GitHub Actions workflow.

## Performance Tests

**Framework:** JUnitPerf  
**Location:** `src/test/java/com/example/library/performance/`  
**Maven profile:** `performance`  
**Command:**

```bash
mvn -Pperformance integration-test
```

Performance tests annotate methods with `@JUnitPerfTest` to define throughput and latency thresholds. The generated HTML report is saved to `target/reports/perf-report.html`.

## Code Coverage

**Tool:** JaCoCo  
**Minimum line coverage enforced:** 25 %  

```bash
mvn clean test jacoco:report   # generates target/site/jacoco/index.html
mvn clean verify               # enforces the coverage threshold
```

The JaCoCo report is included in the Maven Site and embedded in the Sphinx portal.

## Static Analysis

| Tool | Purpose | Report |
|---|---|---|
| **Checkstyle** | Code style conformance | `target/site/checkstyle.html` |
| **PMD** | Static bug-pattern detection | `target/site/pmd.html` |
| **CPD** (via PMD) | Copy-paste detection | `target/site/cpd.html` |
| **JDepend** | Package coupling metrics | `target/site/jdepend-report.html` |

All static-analysis reports are produced during `mvn site`.
