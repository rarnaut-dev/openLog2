package com.openlog.utils

import androidx.compose.ui.graphics.Color
import com.openlog.model.*

fun passesFilter(entry: LogEntry, filter: Filter): Boolean {
    if (entry.level !in filter.levels) return false
    if (entry.tag in filter.excludeTags) return false
    if (filter.excludeKw.isNotBlank()) {
        val hay = "${entry.tag} ${entry.msg}"
        val excluded = if (filter.excludeKwRegex)
            runCatching { Regex(filter.excludeKw, RegexOption.IGNORE_CASE).containsMatchIn(hay) }.getOrElse { false }
        else hay.contains(filter.excludeKw, ignoreCase = true)
        if (excluded) return false
    }
    // PID / TID filter — comma-separated list, entry passes if its pid OR tid matches any
    if (filter.pidTidFilter.isNotBlank()) {
        val tokens = filter.pidTidFilter.split(',', ' ').map { it.trim() }.filter { it.isNotEmpty() }
        val pidStr = entry.pid.toString(); val tidStr = entry.tid.toString()
        if (tokens.none { it == pidStr || it == tidStr }) return false
    }
    return when (filter.mode) {
        FilterMode.TAGS -> {
            // Tag passes if: no active-tag / prefix filter  OR  exact match  OR  prefix match
            val hasTagFilter = filter.activeTags.isNotEmpty() || filter.pkgPrefix.isNotBlank()
            val tagPass = if (hasTagFilter) {
                entry.tag in filter.activeTags ||
                (filter.pkgPrefix.isNotBlank() &&
                    (entry.tag == filter.pkgPrefix || entry.tag.startsWith(filter.pkgPrefix + ".")))
            } else true
            if (!tagPass) return false
            // Secondary message keyword filter within the tag result set
            if (filter.kwInTag.isNotBlank()) {
                val msgPass = if (filter.kwInTagRegex)
                    runCatching { Regex(filter.kwInTag, RegexOption.IGNORE_CASE).containsMatchIn(entry.msg) }.getOrElse { false }
                else entry.msg.contains(filter.kwInTag, ignoreCase = true)
                if (!msgPass) return false
            }
            true
        }
        FilterMode.KEYWORD -> {
            if (filter.kwText.isBlank()) true
            else {
                val hay = "${entry.tag} ${entry.msg}"
                if (filter.kwRegex) runCatching { Regex(filter.kwText, RegexOption.IGNORE_CASE).containsMatchIn(hay) }.getOrElse { false }
                else hay.contains(filter.kwText, ignoreCase = true)
            }
        }
    }
}

fun computeItems(tab: LogTab, sequences: List<SequenceDef>, applyFilter: Boolean): List<LogItem> {
    // Filter first, then detect sequences in filtered data only
    val data = if (applyFilter) tab.logData.filter { passesFilter(it, tab.filter) } else tab.logData

    val seqGroups = if (tab.filter.seqOn && sequences.any { it.enabled })
        computeSeqGroups(data, sequences)
    else emptyList()

    if (seqGroups.isEmpty()) return data.map { LogItem.Row(it, 0) }

    val defMap = sequences.associateBy { it.id }
    val skipIds = buildSet<Int> {
        seqGroups.forEach { g ->
            addAll(g.plain)
            g.nested.forEach { ng -> add(ng.rid); addAll(ng.ch) }
        }
    }

    val secondaryColor = sequences.filter { it.enabled }.sortedBy { it.priority }.getOrNull(1)?.color
        ?: Color(0xFFf0883e)

    val items = mutableListOf<LogItem>()
    for (entry in data) {
        val sg = seqGroups.find { it.rid == entry.id }
        if (sg != null) {
            val totalCh = sg.plain.size + sg.nested.sumOf { ng -> 1 + ng.ch.size }
            val exp = sg.gid in tab.expanded
            val outerColor = defMap[sg.defId]?.color ?: Color(0xFF8957e5)
            items += LogItem.SeqHeader(entry, sg.gid, 0, exp, totalCh, outerColor)
            if (exp) {
                sg.plain.forEach { id -> tab.rmap[id]?.let { items += LogItem.Row(it, 1) } }
                for (ng in sg.nested) {
                    val nr = tab.rmap[ng.rid] ?: continue
                    val nexp = ng.gid in tab.expanded
                    items += LogItem.SeqHeader(nr, ng.gid, 1, nexp, ng.ch.size, secondaryColor)
                    if (nexp) {
                        ng.ch.forEach { id -> tab.rmap[id]?.let { items += LogItem.Row(it, 2) } }
                    }
                }
            }
            continue
        }
        if (entry.id in skipIds) continue
        items += LogItem.Row(entry, 0)
    }
    return items
}

fun buildMd(tab: LogTab): String = buildString {
    if (tab.annotations.prefix.isNotBlank()) { appendLine(tab.annotations.prefix); appendLine() }
    for (block in tab.annotations.blocks) {
        when (block) {
            is AnnBlock.Note -> {
                if (block.text.isNotBlank()) { appendLine(block.text); appendLine() }
            }
            is AnnBlock.LogRef -> {
                if (block.caption.isNotBlank()) { appendLine(block.caption); appendLine() }
                val rows = block.logIds.mapNotNull { tab.rmap[it] }
                if (rows.size == 1) {
                    val r = rows[0]
                    appendLine("> `[${r.ts}] ${r.level.key}/${r.tag}: ${r.msg}`")
                } else {
                    rows.forEach { r -> appendLine("> `[${r.ts}] ${r.level.key}/${r.tag}: ${r.msg}`") }
                }
                appendLine()
            }
        }
    }
    if (tab.annotations.suffix.isNotBlank()) {
        appendLine("---"); appendLine(); append(tab.annotations.suffix)
    }
}
