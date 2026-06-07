# Diagram Workflow

This project uses two diagram workflows:

- Mermaid diagrams for formal OOAD diagrams: sequence diagrams, class diagrams, ER diagrams, and activity-like flowcharts.
- Fireworks tech graph diagrams for visual system architecture and role-based navigation diagrams.

## Mermaid

Store Mermaid source files in:

```text
docs/diagrams/mermaid-src/
```

Render SVG output files into:

```text
docs/diagrams/mermaid-output/
```

Use `.mmd` for Mermaid source files and SVG output in the PDF report.

Current render command:

```powershell
npm run diagram:mermaid
```
