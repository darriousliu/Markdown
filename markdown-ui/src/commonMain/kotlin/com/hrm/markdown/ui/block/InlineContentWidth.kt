package com.hrm.markdown.ui.block

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit

@Composable
internal fun WithInlineContentWidth(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable (TextUnit?) -> Unit,
) {
    val density = LocalDensity.current
    var widthPx by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { size ->
                if (size.width > 0 && size.width != widthPx) {
                    widthPx = size.width
                }
            },
        contentAlignment = contentAlignment
    ) {
        val maxInlineContentWidth = if (widthPx > 0) {
            with(density) { widthPx.toSp() }
        } else {
            null
        }
        content(maxInlineContentWidth)
    }
}
