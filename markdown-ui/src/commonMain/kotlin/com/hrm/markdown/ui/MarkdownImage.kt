package com.hrm.markdown.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * 图片渲染所需的数据模型。
 *
 * @param url 图片 URL
 * @param altText 替代文本（无障碍）
 * @param title 图片标题（tooltip）
 * @param width 指定宽度（像素），null 表示自适应
 * @param height 指定高度（像素），null 表示自适应
 * @param attributes 额外属性（class, id, loading, align 等）
 */
@Immutable
data class MarkdownImageData(
    val url: String,
    val altText: String,
    val title: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val attributes: Map<String, String> = emptyMap(),
) {
    /** 获取对齐方式 */
    val align: String?
        get() = attributes["align"]

    /** 获取 loading 模式（lazy/eager） */
    val loading: String?
        get() = attributes["loading"]

    /** 获取 CSS class 列表 */
    val cssClasses: List<String>
        get() = attributes["class"]?.split(" ")?.filter { it.isNotEmpty() } ?: emptyList()

    /** 获取 CSS ID */
    val cssId: String?
        get() = attributes["id"]
}

/**
 * 图片渲染器的类型别名。
 * 外部可以通过提供自定义实现来替换默认的图片加载逻辑。
 */
typealias MarkdownImageRenderer = @Composable (data: MarkdownImageData, modifier: Modifier) -> Unit

/**
 * 用于在组件树中传递自定义图片渲染器的 CompositionLocal。
 *
 * 默认值为 null，表示使用内置的默认渲染器（纯文本占位显示）。
 * 外部调用者可以通过 [Markdown] 的 `imageContent` 参数提供自定义实现，
 * 例如使用 Coil、Kamel、Ktor 等图片加载库。
 *
 * 使用示例：
 * ```kotlin
 * Markdown(
 *     markdown = "![alt](https://example.com/image.png =200x300)",
 *     imageContent = { data, modifier ->
 *         // 使用 Coil 或其他图片加载库
 *         AsyncImage(
 *             model = data.url,
 *             contentDescription = data.altText,
 *             modifier = modifier,
 *         )
 *     },
 * )
 * ```
 */
internal val LocalImageRenderer = compositionLocalOf<MarkdownImageRenderer?> { null }

/**
 * 默认的图片渲染组件。
 *
 * 默认只提供文本降级展示，避免内置依赖具体图片加载库。
 * 外部使用者可通过 [Markdown] 的 `imageContent` 参数接入 Coil、Kamel 等实现。
 */
@Composable
internal fun DefaultMarkdownImage(
    data: MarkdownImageData,
    modifier: Modifier = Modifier,
) {
    val theme = LocalMarkdownTheme.current
    MarkdownImageFrame(
        data = data,
        style = theme.image,
        modifier = modifier,
    ) {
        BasicText(
            text = data.altText.ifEmpty { data.title ?: data.url },
            style = theme.image.captionTextStyle.copy(textAlign = theme.image.captionTextAlign),
        )
    }
}

@Composable
internal fun MarkdownImageFrame(
    data: MarkdownImageData,
    style: com.hrm.markdown.ui.theme.ImageStyle,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(style.cornerRadius)

    Box(
        modifier = modifier
            .applyImageSize(data.width, data.height)
            .clip(shape)
            .background(style.background)
            .border(style.borderWidth, style.borderColor, shape)
            .padding(vertical = 4.dp)
            .padding(style.contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/**
 * 根据图片数据的宽高约束应用 Modifier。
 */
internal fun Modifier.applyImageSize(width: Int?, height: Int?): Modifier {
    var mod = this
    if (width != null && height != null) {
        mod = mod.size(width.dp, height.dp)
    } else if (width != null) {
        mod = mod.widthIn(max = width.dp)
    } else if (height != null) {
        mod = mod.heightIn(max = height.dp)
    }
    return mod
}
