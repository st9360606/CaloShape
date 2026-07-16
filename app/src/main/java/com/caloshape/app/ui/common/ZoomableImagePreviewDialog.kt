package com.caloshape.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.caloshape.app.R
import com.caloshape.app.ui.common.haptic.rememberClickWithHaptic

private const val MinimumImageScale = 1f
private const val MaximumImageScale = 5f

@Composable
fun ZoomableImagePreviewDialog(
    imageModel: Any?,
    contentDescription: String,
    onDismiss: () -> Unit
) {
    if (imageModel == null) return

    var scale by remember(imageModel) { mutableFloatStateOf(MinimumImageScale) }
    var translation by remember(imageModel) { mutableStateOf(Offset.Zero) }
    var viewportSize by remember(imageModel) { mutableStateOf(IntSize.Zero) }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        val updatedScale = (scale * zoomChange)
            .coerceIn(MinimumImageScale, MaximumImageScale)
        scale = updatedScale
        translation = constrainImageTranslation(
            candidate = translation + panChange,
            viewportSize = viewportSize,
            scale = updatedScale
        )
    }
    val dismissWithHaptic = rememberClickWithHaptic(onClick = onDismiss)

    Dialog(
        onDismissRequest = dismissWithHaptic,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .clipToBounds()
                .testTag("zoomable_image_preview")
        ) {
            AsyncImage(
                model = imageModel,
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .onSizeChanged { viewportSize = it }
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = translation.x
                        translationY = translation.y
                    }
                    .transformable(state = transformState),
                contentScale = ContentScale.Fit
            )

            IconButton(
                onClick = dismissWithHaptic,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.62f))
                    .testTag("zoomable_image_preview_close")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.common_close),
                    tint = Color.White
                )
            }
        }
    }
}

private fun constrainImageTranslation(
    candidate: Offset,
    viewportSize: IntSize,
    scale: Float
): Offset {
    if (scale <= MinimumImageScale || viewportSize == IntSize.Zero) return Offset.Zero

    val maxTranslationX = viewportSize.width * (scale - MinimumImageScale) / 2f
    val maxTranslationY = viewportSize.height * (scale - MinimumImageScale) / 2f
    return Offset(
        x = candidate.x.coerceIn(-maxTranslationX, maxTranslationX),
        y = candidate.y.coerceIn(-maxTranslationY, maxTranslationY)
    )
}
