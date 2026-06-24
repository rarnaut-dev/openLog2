# openLog — Screen Flows

## Flow 1: Open and explore a logcat file

1. User launches the app → sees empty state with "Open file" button and drag-drop zone
2. User opens or drops a `.log` file
3. App parses the file → log viewer populates with all entries
4. Parser auto-detected → shown in toolbar parser indicator
5. Level badge counts shown in filter panel (e.g. "ERROR: 12 / WARN: 34")

---

## Flow 2: Filter logs

1. User opens filter panel (sidebar toggle)
2. Unchecks log levels (e.g. hides VERBOSE, DEBUG)
3. Types a tag name in the Tag filter → matching tags highlighted, others hidden
4. Adds a keyword rule → log viewer updates in real time
5. User saves the filter as a preset named "Login flow errors"
6. User can restore this preset later from the saved filters list

---

## Flow 3: Define a sequence

1. User scrolls to a recurring log message (e.g. `ActivityLifecycle: onResume`)
2. Right-clicks → "Mark as sequence boundary…"
3. Dialog opens:
   - Match mode: exact / contains / regex (pre-filled with the selected line's message)
   - Color picker
   - Priority: defaults to next available (e.g. 1 if first sequence)
   - Preview: shows how many occurrences found in the current log
4. User confirms → log viewer restructures: entries between each pair of occurrences collapse into tree nodes
5. User opens Sequence Manager to see all sequences, reorder priorities, or edit

---

## Flow 4: Navigate collapsed sequences

1. Log viewer shows sequence nodes as collapsed rows with `>` indicator
2. User clicks `>` on a node → expands to show entries inside
3. If a nested sequence exists inside, it also shows as a collapsed sub-node
4. User clicks the expanded node's indicator (↓) → collapses it again
5. "Expand all" / "Collapse all" available in the toolbar or right-click context

---

## Flow 5: Build an annotation / comment

1. User selects one or more log lines (Shift+click for range, Cmd/Ctrl+click for individual)
2. Right-clicks → "Add to annotation"
3. Bottom panel opens (or gains focus) showing the selected lines in timestamp order
4. User types a note below each line
5. User adds a prefix ("## Bug Analysis — Login crash") and a suffix ("Next steps: check network timeout config")
6. User toggles the Markdown preview → sees the rendered output on the right
7. User edits the raw Markdown directly if needed (syncs back to structured list, or user breaks sync for free edit)
8. User clicks "Copy to clipboard" or "Save as .md"

---

## Flow 6: Manage and reorder sequences

1. User opens Sequence Manager from toolbar
2. Sees list: Priority 1 = `onResume`, Priority 2 = `networkCall`, Priority 3 = `onClick`
3. Drags `networkCall` above `onResume` → priorities swap
4. Log viewer immediately restructures: `networkCall` is now the outer group, `onResume` is nested inside
5. User disables `onClick` sequence via toggle → those boundaries are ignored in the viewer
6. User deletes a sequence → its grouping is removed, entries shown flat again

---

## Edge Cases to Design For

- **Very large files** (100k+ lines): log viewer must virtualize rendering — only visible rows rendered
- **No matches for a filter**: show empty state with "No entries match current filters" and a "Clear filters" button
- **Sequence boundary not found**: if a defined sequence message doesn't appear in the current log, show it as inactive in Sequence Manager
- **Overlapping sequences at same priority**: if two different sequence boundaries with the same priority interleave, show a warning in Sequence Manager
- **Annotation with no notes**: export still works, notes are just empty — the log line itself carries meaning
- **Multi-comment mode**: if a user wants to document two separate bugs from the same log, they need at least two annotation workspaces (open question — could be tabs in the annotation panel)
