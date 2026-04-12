package com.hrm.markdown.ui

import androidx.compose.runtime.Immutable
import com.hrm.markdown.parser.flavour.ExtendedFlavour
import com.hrm.markdown.parser.flavour.MarkdownFlavour

@Immutable
data class MarkdownUiConfig(
    val flavour: MarkdownFlavour = ExtendedFlavour,
    val customEmojiMap: Map<String, String> = emptyMap(),
    val enableAsciiEmoticons: Boolean = false,
    val enableLinting: Boolean = false,
) {
    companion object {
        val Default = MarkdownUiConfig()
    }
}
