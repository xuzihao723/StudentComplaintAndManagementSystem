# Claude Style UI Redesign Design

## Feature Summary

Redesign the full Student Complaint and Feedback Management System UI using the existing `DESIGN.md` as the visual source of truth. The work covers the authenticated app shell, role dashboards, case tables, case detail drawer, settings, profile/password, and the public authentication/anonymous submission surface.

## Primary User Action

Each role should immediately understand its next action: students submit or track a case, officers review and assign, department staff process assigned work, and admins configure the system and inspect risk.

## Design Direction

Color strategy is restrained: warm cream canvas, coral primary actions, dark product surfaces, and semantic colors used only for state. The scene is a student or campus staff member working on a laptop in a bright office or dorm room, needing calm institutional clarity rather than decorative drama. Anchor references are `DESIGN.md` Claude warm-canvas system, Linear task clarity, and Stripe Dashboard settings/table discipline.

Visual probes are skipped because the user declined visual companion use and explicitly approved a direction fully based on `DESIGN.md`.

## Scope

Production-ready UI pass across the whole existing frontend surface. The implementation should remain interactive and functional using the current Vue 3, Element Plus, and lucide stack.

## Layout Strategy

The app shell keeps a left navigation, but treats it as a dark product surface. Main content uses a warm editorial canvas with wide workspaces. Dashboards use a statistics band plus asymmetric panels for recent activity and actions. Table pages are full-width with a cream filter toolbar. Forms and settings use centered or two-column work surfaces with generous rhythm and clear labels.

## Key States

- Default: warm editorial surface, predictable product controls.
- Loading: skeleton panels that match the table or card shape.
- Empty: role-aware action prompts, not generic "no data" text.
- Error: inline form and page-level alerts using the error token.
- Success: toast feedback and immediate visual state update.
- Focus: visible coral focus ring.
- Active/hover: subtle press and background changes only.
- Mobile: sidebar stacks above content, tables scroll horizontally, forms collapse to one column.

## Interaction Model

Navigation changes task pages without duplicating dashboard content. Tables open a dossier-style drawer. Primary actions use coral buttons, secondary actions use cream hairline buttons, and destructive or warning actions use semantic color only when needed. Filters are always visible on case list pages.

## Content Requirements

The interface keeps existing English labels but clarifies empty states and operational copy. Empty case pages should tell users what to do next. Notifications should show email status and failure reason when present. Settings should distinguish Departments, Categories & SLA, and Email Settings.

## Recommended References

- `reference/product.md`
- `reference/spatial-design.md`
- `reference/typography.md`
- `reference/color-and-contrast.md`
- `reference/interaction-design.md`
- `reference/responsive-design.md`
- `reference/ux-writing.md`

## Open Questions

No open direction questions remain. Browser visual companion was declined; verification will rely on tests, build, and local rendered inspection where available.
