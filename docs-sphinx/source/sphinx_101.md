# Sphinx 101 — Complete Guide

## What Is Sphinx?

**Sphinx** is an open-source documentation generator originally created for the Python language reference documentation (it still powers the official Python docs). Today it is widely used across languages — including Java, C++, and JavaScript projects — whenever developers need to produce structured, professional, multi-page technical documentation from plain text source files.

Sphinx takes plain text files written in **reStructuredText (`.rst`)** or **Markdown (`.md`, via the MyST parser extension)** and generates:

- Static HTML websites (the most common output)
- PDF documents (via LaTeX)
- ePub e-books
- Man pages

The result is a maintainable, version-controlled documentation site that lives alongside the source code and can be automatically published through CI/CD.

---

## What Is Sphinx Useful For?

| Use Case | Why Sphinx |
|---|---|
| **Project portals** | Aggregates API docs, guides, reports, and tutorials in one navigable site |
| **API reference** | Can auto-import Python docstrings; for Java projects, embeds Javadoc/Doxygen links |
| **Tutorials & how-tos** | Numbered sections, admonition boxes, code blocks with syntax highlighting |
| **Multi-version docs** | Tools like `sphinx-multiversion` serve docs for every tagged release |
| **Search** | Built-in full-text client-side search (no server needed) |
| **Cross-references** | `:ref:`, `:doc:` directives create typed links that break at build time if the target disappears |
| **Theming** | Dozens of themes (`sphinx_rtd_theme`, `furo`, `pydata_sphinx_theme`) give professional results instantly |

### Sphinx vs. Alternatives

| Tool | Primary audience | Markup | Auto-API | Best for |
|---|---|---|---|---|
| **Sphinx** | Python / polyglot | RST / Markdown | Python (autodoc) | Comprehensive project portals |
| **Javadoc** | Java | Javadoc tags | Yes (Java) | API reference only |
| **Doxygen** | C/C++/Java | Doxygen tags | Yes | Cross-referenced code ref |
| **MkDocs** | Any | Markdown | No | Simple Markdown-first sites |
| **Docusaurus** | JS / React | Markdown/MDX | No | Product/SaaS documentation |

Sphinx shines when you need a **single portal** that combines hand-written narrative documentation, auto-generated API references, test reports, and external links — exactly the goal of this project.

---

## How Is Sphinx Used?

### 1. Install Sphinx

```bash
pip install sphinx sphinx-rtd-theme myst-parser
```

Or pin versions in a `requirements.txt`:

```
sphinx==7.4.7
sphinx-rtd-theme==2.0.0
myst-parser==3.0.1
```

### 2. Initialise a Project

```bash
sphinx-quickstart docs-sphinx
```

This interactive wizard creates `conf.py` (the configuration file), `index.rst` (the master table of contents), and a `Makefile`.

### 3. Source File Structure

```
docs-sphinx/
├── source/
│   ├── conf.py        ← Sphinx configuration
│   ├── index.rst      ← Master table of contents (toctree)
│   ├── overview.md    ← A page written in Markdown
│   ├── api.rst        ← A page written in reStructuredText
│   └── _static/       ← Custom CSS / images
├── Makefile           ← make html, make pdf, …
└── requirements.txt   ← Pinned Python dependencies
```

### 4. The `toctree` Directive

The `.. toctree::` directive in `index.rst` defines the site's navigation hierarchy:

```rst
.. toctree::
   :maxdepth: 2
   :caption: Project Overview

   overview
   architecture
   getting_started
```

Each entry maps to a `.rst` or `.md` file. Sphinx resolves them recursively.

### 5. Build the Documentation

```bash
cd docs-sphinx
make html          # output → _build/html/index.html
make latexpdf      # output → _build/latex/*.pdf
make clean         # remove previous build artefacts
```

### 6. Key reStructuredText Syntax

````rst
Section Heading
===============

Sub-heading
-----------

A paragraph with **bold**, *italic*, and ``inline code``.

.. code-block:: java

   public class Hello {
       public static void main(String[] args) {
           System.out.println("Hello from Sphinx!");
       }
   }

.. note::
   This is an admonition — a highlighted information box.

.. warning::
   This is a warning box.
````

### 7. Markdown with MyST

With the `myst-parser` extension enabled in `conf.py`, you can write pages in Markdown:

````markdown
# My Page

A paragraph with **bold** and *italic*.

```java
public class Hello { }
```

```{note}
This is a MyST admonition.
```

```{toctree}
:maxdepth: 2
overview
architecture
```
````

---

## How to Integrate Sphinx with GitHub Actions

The workflow below builds the Sphinx HTML and publishes it to the `gh-pages` branch using the `peaceiris/actions-gh-pages` action. It preserves other content already on `gh-pages` (e.g., Maven Site) via `keep_files: true`.

```yaml
# .github/workflows/sphinx-docs.yml
name: Build and Deploy Sphinx Docs

on:
  push:
    branches: [main]
  workflow_dispatch:       # allow manual trigger

jobs:
  sphinx:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Set up Python 3.12
        uses: actions/setup-python@v5
        with:
          python-version: '3.12'

      - name: Cache pip packages
        uses: actions/cache@v4
        with:
          path: ~/.cache/pip
          key: ${{ runner.os }}-pip-${{ hashFiles('docs-sphinx/requirements.txt') }}
          restore-keys: ${{ runner.os }}-pip-

      - name: Install Sphinx & extensions
        run: pip install -r docs-sphinx/requirements.txt

      - name: Build Sphinx HTML
        run: |
          cd docs-sphinx
          make html

      - name: Deploy to GitHub Pages (sphinx/ subfolder)
        uses: peaceiris/actions-gh-pages@v4
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_dir: ./docs-sphinx/_build/html
          destination_dir: sphinx     # published at /sphinx/ URL path
          keep_files: true            # preserve Maven Site & Doxygen content
```

### Required Repository Settings

1. **Settings → Actions → General → Workflow permissions** → *Read and write permissions*
2. **Settings → Pages** → Source: `gh-pages` branch, folder: `/`

The Sphinx portal will then be live at:
```
https://<owner>.github.io/<repo>/sphinx/
```

---

## How This Project Uses Sphinx

### Documentation Architecture

The Sphinx portal for SpringBootLibrary is a **meta-documentation hub** — it does not duplicate content that already lives in other tools; instead it links out to the generated artefacts:

```
GitHub Pages (gh-pages branch)
├── index.html              ← Landing page redirecting to Sphinx or Maven site
├── sphinx/                 ← Sphinx portal (this site)
│   ├── index.html
│   ├── overview.html
│   ├── testing.html
│   ├── reports.html        ← Links to JaCoCo, Surefire, PMD reports
│   ├── javadoc_link.html   ← Links to Javadoc in site/apidocs/
│   └── sphinx_101.html     ← This page
├── site/                   ← Maven Site (Javadoc, JaCoCo, PMD, Checkstyle …)
│   ├── apidocs/
│   ├── jacoco/
│   ├── surefire-report.html
│   └── …
└── doxygen/                ← Doxygen cross-referenced HTML
    └── html/
```

### Integration Points

| External Artefact | Where Sphinx links to it |
|---|---|
| Javadoc | `javadoc_link.md` → `../site/apidocs/index.html` |
| JaCoCo | `reports.md` → `../site/jacoco/index.html` |
| Surefire | `reports.md` → `../site/surefire-report.html` |
| PMD / Checkstyle | `reports.md` → `../site/pmd.html` |
| Performance report | `reports.md` → `../site/reports/perf-report.html` |
| Doxygen | `javadoc_link.md` → `../doxygen/html/index.html` |

### Adding a New Documentation Page

1. Create `docs-sphinx/source/my_page.md`
2. Add `my_page` to the appropriate `.. toctree::` in `index.rst`
3. Push to `main` — the GitHub Actions workflow rebuilds and redeploys automatically

### Adding the `conf.py` Extensions for This Project

```python
extensions = [
    'sphinx.ext.autodoc',    # auto-import Python docstrings (optional here)
    'sphinx.ext.viewcode',   # adds [source] links
    'sphinx.ext.napoleon',   # Google/NumPy style docstrings
    'myst_parser',           # Markdown .md files
    'sphinx_rtd_theme',      # Read the Docs theme
]
```

---

## Useful Sphinx Resources

| Resource | URL |
|---|---|
| Official Sphinx docs | https://www.sphinx-doc.org |
| MyST Markdown parser | https://myst-parser.readthedocs.io |
| Read the Docs theme | https://sphinx-rtd-theme.readthedocs.io |
| Furo theme (modern) | https://pradyunsg.me/furo |
| sphinx-multiversion | https://holzhaus.github.io/sphinx-multiversion |
| peaceiris/actions-gh-pages | https://github.com/peaceiris/actions-gh-pages |
