# CI/CD with GitHub Actions

The project ships with two GitHub Actions workflows:

| Workflow file | Trigger | What it does |
|---|---|---|
| `maven-site-integration.yml` | Push to any branch + cron | Build, test, Maven site, deploy to Pages |
| `sphinx-docs.yml` | Push to `main` | Build Sphinx docs + merge into GitHub Pages |

---

## Workflow 1: `maven-site-integration.yml`

This is the main CI/CD workflow. It:

1. Starts a MySQL 8 Docker service
2. Waits for the database to be healthy
3. Runs the DB initialisation script
4. Executes integration tests (`-Pintegration`)
5. Generates the full Maven site (Javadoc, JaCoCo, PMD, Checkstyle, Surefire)
6. Copies all Maven site output + Doxygen into the `docs/site/` and `docs/doxygen/` sub-folders
7. Deploys the `docs/` directory to **GitHub Pages**

```yaml
name: Maven Site & Integration Tests

on:
  push:
    branches: ['**']
  schedule:
    - cron: '0 18-23/2 * * *'
    - cron: '0 0-6/2 * * *'

jobs:
  build-and-test:
    runs-on: ubuntu-latest

    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: root
          MYSQL_DATABASE: libraryapidb
        ports:
          - 3306:3306
        options: >-
          --health-cmd="mysqladmin ping -h 127.0.0.1 -uroot -proot"
          --health-interval=10s
          --health-timeout=5s
          --health-retries=5

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Cache Maven
        uses: actions/cache@v4
        with:
          path: ~/.m2/repository
          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}

      - name: Wait for MySQL
        run: |
          until mysqladmin ping -h 127.0.0.1 -uroot -proot --silent; do sleep 2; done

      - name: Init database
        run: mysql -h 127.0.0.1 -uroot -proot < src/main/resources/dbsetup.sql

      - name: Integration tests
        run: mvn -Pintegration integration-test

      - name: Maven site
        run: mvn site

      - name: Publish docs to GitHub Pages
        uses: peaceiris/actions-gh-pages@v4
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_dir: ./docs
          destination_dir: .    # serve from root of gh-pages branch
          keep_files: true       # keep Sphinx output from the other workflow
```

---

## Workflow 2: `sphinx-docs.yml`

This workflow builds the Sphinx documentation and deploys it to the `gh-pages` branch **alongside** the Maven site output, without overwriting it.

```yaml
name: Build and Deploy Sphinx Docs

on:
  push:
    branches: [main]
  workflow_dispatch:

jobs:
  sphinx:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Set up Python
        uses: actions/setup-python@v5
        with:
          python-version: '3.12'

      - name: Install Sphinx dependencies
        run: |
          pip install -r docs-sphinx/requirements.txt

      - name: Build Sphinx HTML
        run: |
          cd docs-sphinx
          make html

      - name: Deploy Sphinx to GitHub Pages (sphinx/ subfolder)
        uses: peaceiris/actions-gh-pages@v4
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_dir: ./docs-sphinx/_build/html
          destination_dir: sphinx   # → gh-pages/sphinx/
          keep_files: true
```

---

## GitHub Pages Setup

To enable GitHub Pages for this repository:

1. Go to **Settings → Pages**
2. Set **Source** to `gh-pages` branch, root `/`
3. Save — the site will be live at `https://<owner>.github.io/SpringBootLibrary/`

> **Permissions:** Go to **Settings → Actions → General → Workflow permissions** and set **Read and write permissions** so that the `GITHUB_TOKEN` can push to the `gh-pages` branch.

---

## Triggering Workflows via API

```bash
curl -X POST \
  https://api.github.com/repos/<owner>/SpringBootLibrary/actions/workflows/sphinx-docs.yml/dispatches \
  -H "Accept: application/vnd.github+json" \
  -H "Authorization: Bearer $PAT_TOKEN" \
  -H "X-GitHub-Api-Version: 2022-11-28" \
  -d '{"ref":"main"}'
```
