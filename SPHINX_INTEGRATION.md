# Integrating Sphinx into SpringBootLibrary

This document is a step-by-step integration guide. Everything described here is already
implemented in the files shipped alongside this guide. You only need to follow the
**Setup** section once to activate it in your fork.

---

## 1. What Was Added to the Project

| New / Changed file | Purpose |
|---|---|
| `docs-sphinx/source/conf.py` | Sphinx configuration (theme, extensions, project metadata) |
| `docs-sphinx/source/index.rst` | Master table of contents |
| `docs-sphinx/source/overview.md` | Project overview page |
| `docs-sphinx/source/architecture.md` | Architecture documentation |
| `docs-sphinx/source/getting_started.md` | Installation and run guide |
| `docs-sphinx/source/testing.md` | Testing strategy (unit / integration / performance / coverage) |
| `docs-sphinx/source/reports.md` | Links to all generated reports (JaCoCo, Surefire, PMD, Doxygen, Javadoc) |
| `docs-sphinx/source/api_rest.md` | REST API endpoint reference |
| `docs-sphinx/source/javadoc_link.md` | Links to Javadoc and Doxygen, explains both tools |
| `docs-sphinx/source/cicd.md` | GitHub Actions workflows explained in full |
| `docs-sphinx/source/docker.md` | Docker and Docker Compose guide |
| `docs-sphinx/source/sphinx_101.md` | **Complete Sphinx 101** — what it is, how to use it, GitHub Actions integration |
| `docs-sphinx/requirements.txt` | Pinned Python dependencies for reproducible builds |
| `docs-sphinx/Makefile` | Standard Sphinx Makefile (`make html`, `make clean`, etc.) |
| `.github/workflows/sphinx-docs.yml` | **New** GitHub Actions workflow: build Sphinx → deploy to `gh-pages/sphinx/` |
| `.github/workflows/maven-site-integration.yml` | **Updated** — now also deploys `docs/` to `gh-pages/` root |
| `docs/index.html` | **New** landing page at the root of GitHub Pages — links to all documentation sections |

---

## 2. One-Time Setup in Your Fork

### Step 1 — Workflow Permissions

Go to **Settings → Actions → General → Workflow permissions**
and select **Read and write permissions**. This allows the `GITHUB_TOKEN` to push to the
`gh-pages` branch.

### Step 2 — Enable GitHub Pages

Go to **Settings → Pages**.
Set **Source** to `Deploy from a branch`, branch `gh-pages`, folder `/`.

After the first successful workflow run your portal will be live at:

```
https://<your-github-username>.github.io/SpringBootLibrary/
```

### Step 3 — Push the New Files

Copy all new/changed files from this package into your repository and push to `main`:

```bash
git add docs-sphinx/ docs/index.html .github/workflows/
git commit -m "feat: add Sphinx documentation portal with GitHub Pages deployment"
git push origin main
```

Both workflows will trigger:

- `sphinx-docs.yml` builds Sphinx and deploys to `gh-pages/sphinx/`
- `maven-site-integration.yml` builds Maven site + Doxygen and deploys to `gh-pages/` root

---

## 3. Documentation URL Map

Once deployed, your GitHub Pages site looks like this:

```
https://<owner>.github.io/SpringBootLibrary/
├── index.html                    ← Landing page (card grid)
├── sphinx/                       ← Sphinx portal
│   ├── index.html
│   ├── overview.html
│   ├── architecture.html
│   ├── getting_started.html
│   ├── testing.html
│   ├── reports.html              ← Links to all quality reports
│   ├── api_rest.html
│   ├── javadoc_link.html         ← Links to Javadoc & Doxygen
│   ├── cicd.html
│   ├── docker.html
│   └── sphinx_101.html           ← Sphinx 101 guide
├── site/                         ← Maven Site
│   ├── apidocs/                  ← Javadoc
│   ├── jacoco/                   ← JaCoCo coverage
│   ├── surefire-report.html      ← Unit test results
│   ├── pmd.html
│   ├── cpd.html
│   ├── checkstyle.html
│   ├── jdepend-report.html
│   └── reports/perf-report.html  ← Performance test report
└── doxygen/html/                 ← Doxygen cross-reference
```

---

## 4. How Reports Are Integrated into Sphinx

Sphinx does **not** re-generate Javadoc, JaCoCo, or PMD. Instead the `reports.md` and
`javadoc_link.md` pages contain **relative hyperlinks** that work correctly once all
artefacts are co-located on the same GitHub Pages origin:

```markdown
[JaCoCo Coverage Report](../site/jacoco/index.html)
[Javadoc](../site/apidocs/index.html)
[Doxygen](../doxygen/html/index.html)
```

This is intentional: each tool generates the best version of its own output, and Sphinx
acts as the navigation hub that ties them together.

---

## 5. Running Sphinx Locally

```bash
# Install dependencies (Python 3.9+ required)
pip install -r docs-sphinx/requirements.txt

# Build HTML
cd docs-sphinx
make html

# Open the result
open _build/html/index.html      # macOS
xdg-open _build/html/index.html  # Linux
```

---

## 6. Adding New Documentation Pages

1. Create `docs-sphinx/source/my_new_page.md` (or `.rst`)
2. Add `my_new_page` to the relevant `.. toctree::` block in `index.rst`
3. Push → GitHub Actions rebuilds and redeploys automatically

---

## 7. Choosing a Different Theme

Change the `html_theme` variable in `docs-sphinx/source/conf.py` and update `requirements.txt`:

| Theme | pip package | Style |
|---|---|---|
| Read the Docs | `sphinx-rtd-theme` | Classic, widely recognised |
| Furo | `furo` | Modern, clean, dark-mode aware |
| PyData | `pydata-sphinx-theme` | Multi-version, sidebar nav |
| Book | `sphinx-book-theme` | Academic / book-like |

```python
# conf.py — example switch to Furo
html_theme = 'furo'
```

```
# requirements.txt
furo==2024.5.6
```
