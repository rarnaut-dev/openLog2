package com.openlog.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openlog.model.AnnBlock
import com.openlog.model.LogTab
import com.openlog.utils.buildMd

@Composable
fun AnnotationPanel(
    tab: LogTab,
    onToggleMd: () -> Unit,
    onCopy: () -> Unit,
    onSave: () -> Unit,
    onUpdatePrefix: (String) -> Unit,
    onUpdateSuffix: (String) -> Unit,
    onUpdateBlock: (String, String) -> Unit,
    onRemoveBlock: (String) -> Unit,
    onMoveBlock: (String, Int) -> Unit,
    onAddNoteAfter: (String?) -> Unit,
    width: Float,
) {
    val tc = tc()
    val mono = monoFont()
    val ann = tab.annotations
    val blockCount = ann.blocks.size

    Column(Modifier.width(width.dp).fillMaxHeight().background(tc.p).border(BorderStroke(1.dp, tc.br))) {
        // Header
        Row(
            Modifier.fillMaxWidth().height(36.dp).background(tc.p2).border(BorderStroke(1.dp, tc.br)).padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            AppText("Annotation · $blockCount", color = tc.ts, fontSize = 12.sp, modifier = Modifier.weight(1f))
            PillBtn("MD",   active = tab.showAnnMd, onClick = onToggleMd)
            PillBtn("Copy", active = true, onClick = onCopy)
            PillBtn("Save", active = true, onClick = onSave)
        }

        val scroll = rememberScrollState()
        Column(Modifier.fillMaxSize().verticalScroll(scroll)) {
            // Prefix
            AnnSection(tc) {
                AppText("PREFIX", color = tc.td, fontSize = 10.sp, fontFamily = UI)
                Spacer(Modifier.height(3.dp))
                InlineField(ann.prefix, onUpdatePrefix, "Heading, context…", Modifier.fillMaxWidth(), fontSize = 12.sp)
            }

            if (ann.blocks.isEmpty()) {
                // Add note button + empty state
                Box(
                    Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)
                        .border(1.dp, tc.br, RoundedCornerShape(4.dp))
                        .clickable { onAddNoteAfter(null) }.padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center,
                ) { AppText("+ Add text block", color = tc.td, fontSize = 11.sp) }
                Column(
                    Modifier.fillMaxWidth().padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AppText("◆", color = tc.td.copy(.33f), fontSize = 22.sp)
                    AppText("Right-click a log line\nto annotate it", color = tc.td, fontSize = 11.sp, maxLines = 2)
                }
            }

            ann.blocks.forEachIndexed { idx, block ->
                val isFirst = idx == 0
                val isLast  = idx == ann.blocks.lastIndex

                when (block) {
                    is AnnBlock.Note -> NoteBlock(
                        block = block, tc = tc, isFirst = isFirst, isLast = isLast,
                        onUpdate = { onUpdateBlock(block.id, it) },
                        onRemove = { onRemoveBlock(block.id) },
                        onMoveUp = { onMoveBlock(block.id, -1) },
                        onMoveDown = { onMoveBlock(block.id, 1) },
                        onAddBelow = { onAddNoteAfter(block.id) },
                    )
                    is AnnBlock.LogRef -> LogRefBlock(
                        block = block, tab = tab, mono = mono, tc = tc,
                        isFirst = isFirst, isLast = isLast,
                        onUpdateCaption = { onUpdateBlock(block.id, it) },
                        onRemove = { onRemoveBlock(block.id) },
                        onMoveUp = { onMoveBlock(block.id, -1) },
                        onMoveDown = { onMoveBlock(block.id, 1) },
                        onAddBelow = { onAddNoteAfter(block.id) },
                    )
                }
            }

            if (ann.blocks.isNotEmpty()) {
                // Global + text block button
                Box(
                    Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)
                        .border(1.dp, tc.br, RoundedCornerShape(4.dp))
                        .clickable { onAddNoteAfter(ann.blocks.last().id) }.padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center,
                ) { AppText("+ Add text block", color = tc.td, fontSize = 11.sp) }

                // Suffix
                AnnSection(tc) {
                    AppText("NEXT STEPS", color = tc.td, fontSize = 10.sp, fontFamily = UI)
                    Spacer(Modifier.height(3.dp))
                    InlineField(ann.suffix, onUpdateSuffix, "Add follow-up notes…", Modifier.fillMaxWidth(), fontSize = 11.sp)
                }
            }

            // MD preview
            if (tab.showAnnMd && ann.blocks.isNotEmpty()) {
                Column(Modifier.fillMaxWidth().padding(12.dp).border(BorderStroke(1.dp, tc.br))) {
                    Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)) {
                        AppText("MARKDOWN PREVIEW", color = tc.td, fontSize = 10.sp)
                    }
                    Box(Modifier.fillMaxWidth().background(tc.bg).padding(10.dp)) {
                        AppText(buildMd(tab), color = tc.tx, fontSize = 10.sp, fontFamily = mono,
                            maxLines = Int.MAX_VALUE, overflow = TextOverflow.Clip)
                    }
                }
            }
        }
    }
}

// ── Note block ─────────────────────────────────────────────────────────
@Composable
private fun NoteBlock(
    block: AnnBlock.Note,
    tc: ThemeColors,
    isFirst: Boolean, isLast: Boolean,
    onUpdate: (String) -> Unit,
    onRemove: () -> Unit,
    onMoveUp: () -> Unit, onMoveDown: () -> Unit,
    onAddBelow: () -> Unit,
) {
    Column(
        Modifier.fillMaxWidth()
            .border(BorderStroke(2.dp, tc.ac.copy(.35f)))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        BlockControls(tc, "T", tc.ac, isFirst, isLast, onMoveUp, onMoveDown, onRemove, onAddBelow)
        Spacer(Modifier.height(5.dp))
        BasicTextField(
            value = block.text,
            onValueChange = onUpdate,
            textStyle = TextStyle(color = tc.tx, fontSize = 12.sp, fontFamily = FontFamily.Default, lineHeight = 18.sp),
            cursorBrush = SolidColor(tc.ac),
            modifier = Modifier.fillMaxWidth()
                .background(tc.bg, RoundedCornerShape(3.dp))
                .border(1.dp, tc.br, RoundedCornerShape(3.dp))
                .padding(8.dp).defaultMinSize(minHeight = 60.dp),
            decorationBox = { inner ->
                if (block.text.isEmpty()) AppText("Write your note here…", color = tc.td, fontSize = 12.sp)
                inner()
            },
        )
    }
}

// ── LogRef block ───────────────────────────────────────────────────────
@Composable
private fun LogRefBlock(
    block: AnnBlock.LogRef,
    tab: LogTab,
    mono: androidx.compose.ui.text.font.FontFamily,
    tc: ThemeColors,
    isFirst: Boolean, isLast: Boolean,
    onUpdateCaption: (String) -> Unit,
    onRemove: () -> Unit,
    onMoveUp: () -> Unit, onMoveDown: () -> Unit,
    onAddBelow: () -> Unit,
) {
    val rows = block.logIds.mapNotNull { tab.rmap[it] }
    val borderColor = rows.firstOrNull()?.level?.defaultColor ?: tc.ac

    Column(
        Modifier.fillMaxWidth()
            .border(BorderStroke(2.dp, borderColor))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        // Caption / note text — shown FIRST
        BlockControls(tc, "◆", borderColor, isFirst, isLast, onMoveUp, onMoveDown, onRemove, onAddBelow)
        Spacer(Modifier.height(5.dp))
        BasicTextField(
            value = block.caption,
            onValueChange = onUpdateCaption,
            textStyle = TextStyle(color = tc.tx, fontSize = 12.sp, fontFamily = FontFamily.Default, lineHeight = 18.sp),
            cursorBrush = SolidColor(tc.ac),
            modifier = Modifier.fillMaxWidth()
                .background(tc.bg, RoundedCornerShape(3.dp))
                .border(1.dp, tc.br, RoundedCornerShape(3.dp))
                .padding(8.dp).defaultMinSize(minHeight = 52.dp),
            decorationBox = { inner ->
                if (block.caption.isEmpty()) AppText("Add a note or analysis…", color = tc.td, fontSize = 12.sp)
                inner()
            },
        )
        Spacer(Modifier.height(6.dp))

        // Referenced log lines shown BELOW the text
        Column(
            Modifier.fillMaxWidth()
                .background(tc.bg.copy(.7f), RoundedCornerShape(3.dp))
                .border(1.dp, tc.br.copy(.6f), RoundedCornerShape(3.dp))
                .padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            rows.forEach { r ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppText(r.ts, color = tc.td, fontSize = 9.sp, fontFamily = mono, modifier = Modifier.width(75.dp))
                    LevelBadge(r.level)
                    AppText(r.tag, color = tc.ts, fontSize = 9.sp, fontFamily = mono,
                        modifier = Modifier.width(80.dp), overflow = TextOverflow.Ellipsis)
                    AppText(r.msg, color = tc.ts, fontSize = 9.sp, fontFamily = mono,
                        modifier = Modifier.weight(1f), overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

// ── Block controls (move / delete / add note) ──────────────────────────
@Composable
private fun BlockControls(
    tc: ThemeColors,
    typeLabel: String, typeColor: Color,
    isFirst: Boolean, isLast: Boolean,
    onMoveUp: () -> Unit, onMoveDown: () -> Unit,
    onRemove: () -> Unit,
    onAddBelow: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            Modifier.size(16.dp).background(typeColor.copy(.2f), RoundedCornerShape(3.dp))
                .border(1.dp, typeColor.copy(.4f), RoundedCornerShape(3.dp)),
            contentAlignment = Alignment.Center,
        ) { AppText(typeLabel, color = typeColor, fontSize = 9.sp) }

        Spacer(Modifier.weight(1f))

        if (!isFirst) AppText("↑", color = tc.td, fontSize = 12.sp, modifier = Modifier.clickable(onClick = onMoveUp))
        if (!isLast)  AppText("↓", color = tc.td, fontSize = 12.sp, modifier = Modifier.clickable(onClick = onMoveDown))
        AppText("+ note", color = tc.td, fontSize = 10.sp, modifier = Modifier.clickable(onClick = onAddBelow))
        AppText("×", color = tc.td, fontSize = 14.sp, modifier = Modifier.clickable(onClick = onRemove))
    }
}

@Composable
private fun AnnSection(tc: ThemeColors, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxWidth().border(BorderStroke(1.dp, tc.br.copy(.33f))).padding(horizontal = 12.dp, vertical = 8.dp),
        content = content,
    )
}
