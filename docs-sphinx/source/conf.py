# Configuration file for the Sphinx documentation builder.
# SpringBootLibrary - docs-sphinx/source/conf.py

import os
import sys

# -- Project information -----------------------------------------------------
project = 'SpringBootLibrary'
copyright = '2025, dipina'
author = 'dipina'
release = '0.0.1-SNAPSHOT'

# -- General configuration ---------------------------------------------------
extensions = [
    'sphinx.ext.autodoc',
    'sphinx.ext.viewcode',
    'sphinx.ext.napoleon',
    'myst_parser',          # Markdown support
    'sphinx_rtd_theme',
]

templates_path = ['_templates']
exclude_patterns = []

# Support both .rst and .md source files
source_suffix = {
    '.rst': 'restructuredtext',
    '.md': 'markdown',
}

# -- Options for HTML output -------------------------------------------------
html_theme = 'sphinx_rtd_theme'
html_static_path = ['_static']
html_title = 'SpringBootLibrary Documentation'
html_logo = None
html_favicon = None

html_theme_options = {
    'navigation_depth': 4,
    'collapse_navigation': False,
    'sticky_navigation': True,
    'includehidden': True,
    'titles_only': False,
    'display_version': True,
    'prev_next_buttons_location': 'bottom',
}

# -- MyST options (Markdown parser) ------------------------------------------
myst_enable_extensions = [
    "colon_fence",
    "deflist",
    "tasklist",
]
