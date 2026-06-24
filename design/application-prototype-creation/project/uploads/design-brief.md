# openLog — Design Brief

## Overview

openLog is a desktop log analysis tool aimed at Android developers performing bug investigation. It ingests Android logcat output, provides rich filtering and navigation, and lets the user build a structured Markdown comment (a "bug report snippet") from selected log entries. The app runs natively on macOS, Windows, and Linux via Kotlin Multiplatform + Compose Multiplatform.

The architecture is extensible: the log parser is an interface, with Android logcat as the first implementation. Future formats (server logs, iOS, custom) can be added as plugins without changing core UI.

---

## Target User

Android developer who:
- Receives or captures logcat files/streams during bug investigation
- Needs to find crashes, recurring patterns, and event sequences in large logs
- Wants to document findings as a structured Markdown comment (e.g. for a Jira ticket, PR comment, or internal note)

---

## Key Concepts

### Log Entry
A single parsed line from a logcat file. Fields:
- Timestamp
- Log level: VERBOSE / DEBUG / INFO / WARN / ERROR / ASSERT
- PID / TID
- Tag
- Message body

### Sequence
A user-defined recurring boundary marker. The user selects a log entry and marks it as a sequence boundary. All log entries between one occurrence of that message and the next are grouped into a collapsible block.

**Priority** determines nesting:
- Higher priority (lower number) = outermost / parent group
- Lower priority = nested inside a higher-priority group
- Default: first defined sequence gets priority 1, next gets 2, etc.
- Priority is reorderable by the user; reordering restructures the nesting in real time

**Visual states:**
```
[10:00:01] onResume called  >         ← collapsed
[10:00:05] onResume called  >         ← collapsed
[10:00:09] onResume called  ↓         ← expanded
  [10:00:09] dispatchTouchEvent
  [10:00:09]   networkCall >          ← nested sequence, collapsed
  [10:00:10] onClick: button_login
[10:00:12] onResume called  >
```

### Annotation
A set of selected log lines that the user has attached a note to. Annotations are ordered by log timestamp. Together they form the content of the Markdown export.

---

## Screens & Panels

### 1. Main Window Layout

```
┌─────────────────────────────────────────────────────────────────┐
│ Toolbar: Open file | Filter toggle | Sequence manager | Export  │
├──────────────┬──────────────────────────────────────────────────┤
│              │                                                  │
│  Filter      │  Log Viewer                                      │
│  Panel       │                                                  │
│  (collaps-   │                                                  │
│  ible        │                                                  │
│  sidebar)    │                                                  │
│              │                                                  │
├──────────────┴──────────────────────────────────────────────────┤
│  Annotation / Export Panel (collapsible bottom panel)           │
└─────────────────────────────────────────────────────────────────┘
```

All three side panels (filter, annotation) are independently collapsible. The log viewer is always the primary surface.

---

### 2. Log Viewer

The central panel. Displays parsed log entries in chronological order.

**Each row shows:**
- Timestamp
- Level badge (color-coded: VERBOSE=gray, DEBUG=blue, INFO=green, WARN=yellow, ERROR=red, ASSERT=dark red)
- Tag
- PID/TID (optional, toggleable)
- Message

**Interactions:**
- Click a row = select it
- Right-click = context menu:
  - "Mark as sequence boundary…" → opens sequence definition dialog
  - "Add to annotation" → appends to the annotation panel
  - "Copy line"
  - "Copy as Markdown"
- Multi-select via Shift+click or Ctrl/Cmd+click
- Sequence blocks are rendered as collapsible tree nodes (see Sequence above)
- Filtered-out entries are hidden (not grayed — truly removed from view), with a visual indicator showing how many entries were hidden between visible ones

---

### 3. Filter Panel (left sidebar)

**Sections:**
- **Log level** — checkboxes for V / D / I / W / E / A
- **Tag** — searchable multi-select list of all tags present in the log
- **PID / TID** — searchable list
- **Time range** — start / end timestamp picker (relative or absolute)
- **Keyword / Regex** — text input with a toggle between plain text and regex mode; multiple rules can be stacked (AND / OR logic selectable)
- **Saved filters** — list of named filter presets the user can save and restore

Filters apply to the log viewer in real time. The sequence view respects active filters: sequence boundaries that are filtered out still act as boundaries (so the structure is preserved), but filtered-out entries inside a sequence block are hidden.

---

### 4. Sequence Manager

Accessible from the toolbar or via right-click. Shows all defined sequence boundaries in a reorderable list.

**Each sequence entry shows:**
- Preview of the matching log message (truncated)
- Match mode: exact text / contains / regex
- Priority number (1 = highest)
- Color swatch (each sequence gets a distinct color used in the log viewer)
- Toggle: enabled / disabled
- Delete button

**Reordering** (drag or up/down arrows) changes priority and immediately restructures the nested view.

---

### 5. Annotation & Export Panel (bottom panel)

This is the comment-building workspace.

**Left side — Annotation list:**
- Ordered list of annotated log entries (in timestamp order, regardless of selection order)
- Each entry shows the log line + an editable text note below it
- Entries can be removed, reordered, or grouped
- The user can add free-text blocks between entries (for context, section headers, etc.)
- "Add prefix" and "Add suffix" fields for text that wraps the entire comment

**Right side — Markdown preview (toggleable):**
- Live preview of the generated Markdown
- The preview updates as the user edits notes or reorders entries
- The preview itself is editable (two-way: edits in preview sync back to the structured list, or the user can break out of sync and freely edit the raw Markdown)
- "Copy to clipboard" and "Save as .md file" actions

**Generated Markdown structure (example):**
```markdown
## Bug Analysis — 2026-06-23

> Prefix text here (optional)

**[10:00:09] E/NetworkManager:** Connection timeout after 3 retries

_Note: This happens every time the user taps login. The timeout is 3s but backend SLA is 5s._

**[10:00:10] E/AuthService:** Token refresh failed — null response body

_Note: Root cause — timeout above causes null body here._

---

> Suffix / next steps text here (optional)
```

---

## Toolbar Actions

| Action | Description |
|--------|-------------|
| Open file | Open a `.txt` / `.log` logcat file |
| Drag & drop | Drop a file directly onto the log viewer |
| Filter toggle | Show / hide the filter sidebar |
| Sequence manager | Open the sequence management panel |
| Export | Open / focus the annotation & export panel |
| Theme toggle | Light / dark mode |
| Parser selector | Choose the active log format parser (extensible) |

---

## Extension / Plugin Architecture (for designer awareness)

The app has a "Parser" concept that is user-selectable. From a UI perspective:
- A small parser selector (dropdown or pill) appears in the toolbar
- When a new file is opened, the app auto-detects the format and selects the right parser
- Users can manually override
- Future parsers will add themselves to this list; no other UI changes needed

---

## Non-Goals (v1)

- Mobile (iOS / Android app) — desktop only
- Jira API integration — export is to Markdown only
- Real-time ADB streaming — file-based ingestion only (can be added later)
- Cloud sync or shared sessions

---

## Open Questions for Design

1. Should the sequence collapse/expand state be saved per session (so reopening a file restores the tree state)?
2. Should annotations be saved alongside the log file (e.g. a `.openlog` sidecar file), or only exported?
3. For the annotation panel: should the user be able to create multiple independent "comments" (for different bugs in the same log), or one comment per session?
4. Color scheme: the level badges need high contrast. Should the overall app theme default to dark (common for developer tools) or respect the OS setting?
