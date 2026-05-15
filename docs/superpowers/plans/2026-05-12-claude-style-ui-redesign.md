# Claude Style UI Redesign Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Apply the approved `DESIGN.md` visual system across the existing Vue app while keeping all complaint workflow functionality intact.

**Architecture:** Keep the current single-file Vue app and global stylesheet to avoid a broad component migration. Add a small presentation helper for empty-state copy, then use CSS tokens and targeted template classes to create a unified warm editorial product surface.

**Tech Stack:** Vue 3, Vite, Element Plus, lucide-vue-next, Vitest.

---

### Task 1: Empty State Presentation Helper

**Files:**
- Create: `frontend/src/uiPresentation.js`
- Create: `frontend/src/uiPresentation.test.js`
- Modify: `frontend/src/App.vue`

- [ ] Write tests for role-aware empty states.
- [ ] Run `npm.cmd test` and confirm the new tests fail because the helper does not exist.
- [ ] Implement `emptyStateForView(view, role)`.
- [ ] Render empty states inside `CaseTable`.
- [ ] Run `npm.cmd test` and confirm the tests pass.

### Task 2: Template Hooks

**Files:**
- Modify: `frontend/src/App.vue`

- [ ] Add shell/page classes that distinguish auth, dashboard, table, settings, profile, and drawer surfaces.
- [ ] Add loading skeleton areas for tables and panels.
- [ ] Add failure reason display in notifications.
- [ ] Keep existing API methods and role flows unchanged.

### Task 3: DESIGN.md CSS System

**Files:**
- Modify: `frontend/src/styles.css`

- [ ] Replace old cool green/blue variables with `DESIGN.md` cream, coral, dark, hairline, and semantic tokens.
- [ ] Theme Element Plus buttons, inputs, tabs, tables, alerts, tags, drawer, upload, and popovers.
- [ ] Rework sidebar as a dark product surface.
- [ ] Rework dashboard statistics, table pages, settings, profile, and auth surfaces.
- [ ] Add focus-visible, hover, active, disabled, loading, empty, and reduced-motion states.
- [ ] Add mobile/tablet responsive rules.

### Task 4: Verification

**Files:**
- Frontend test/build outputs.

- [ ] Run `npm.cmd test`.
- [ ] Run `npm.cmd run build`.
- [ ] Restart the frontend dev server if needed.
- [ ] Inspect the local URL enough to catch obvious blank screens or proxy failures.
