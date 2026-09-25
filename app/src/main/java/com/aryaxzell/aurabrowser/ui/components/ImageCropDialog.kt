package com.aryaxzell.aurabrowser.ui.components

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlin.math.max
import kotlin.math.min

@Composable
fun ImageCropDialog(
    sourceBitmap: Bitmap,
    onConfirm: (Bitmap) -> Unit,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val imageBitmap = remember(sourceBitmap) { sourceBitmap.asImageBitmap() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Toolbar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.White)
                    }
                    Text(
                        text = "Crop Icon (1:1)",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = {
                        val cropped = performCrop(sourceBitmap, scale, offset)
                        onConfirm(cropped)
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Confirm", tint = Color.White)
                    }
                }

                // Crop canvas area — square viewport in the center
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(1f, 5f)
                                offset += pan
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cropSize = min(size.width, size.height) * 0.85f
                        val cropRect = Rect(
                            offset = Offset(
                                (size.width - cropSize) / 2f,
                                (size.height - cropSize) / 2f
                            ),
                            size = Size(cropSize, cropSize)
                        )

                        // Draw the image, transformed by scale/offset
                        val imgW = imageBitmap.width.toFloat()
                        val imgH = imageBitmap.height.toFloat()
                        val baseScale = max(cropSize / imgW, cropSize / imgH)
                        val drawScale = baseScale * scale
                        val drawW = imgW * drawScale
                        val drawH = imgH * drawScale
                        val drawLeft = cropRect.center.x - drawW / 2f + offset.x
                        val drawTop = cropRect.center.y - drawH / 2f + offset.y

                        clipRect(
                            left = cropRect.left,
                            top = cropRect.top,
                            right = cropRect.right,
                            bottom = cropRect.bottom
                        ) {
                            drawImage(
                                image = imageBitmap,
                                dstOffset = androidx.compose.ui.unit.IntOffset(drawLeft.toInt(), drawTop.toInt()),
                                dstSize = androidx.compose.ui.unit.IntSize(drawW.toInt(), drawH.toInt())
                            )
                        }

                        // Dim area outside crop square
                        drawRect(color = Color.Black.copy(alpha = 0.55f), size = Size(size.width, cropRect.top))
                        drawRect(
                            color = Color.Black.copy(alpha = 0.55f),
                            topLeft = Offset(0f, cropRect.bottom),
                            size = Size(size.width, size.height - cropRect.bottom)
                        )
                        drawRect(
                            color = Color.Black.copy(alpha = 0.55f),
                            topLeft = Offset(0f, cropRect.top),
                            size = Size(cropRect.left, cropSize)
                        )
                        drawRect(
                            color = Color.Black.copy(alpha = 0.55f),
                            topLeft = Offset(cropRect.right, cropRect.top),
                            size = Size(size.width - cropRect.right, cropSize)
                        )

                        // Crop square border
                        drawRect(
                            color = Color.White,
                            topLeft = cropRect.topLeft,
                            size = cropRect.size,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                        )
                    }
                }

                Text(
                    text = "Pinch to zoom • Drag to reposition",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun performCrop(source: Bitmap, scale: Float, offset: Offset): Bitmap {
    val outputSize = 256 // final icon size
    val output = Bitmap.createBitmap(outputSize, outputSize, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(output)

    val imgW = source.width.toFloat()
    val imgH = source.height.toFloat()
    val baseScale = max(outputSize / imgW, outputSize / imgH)
    val drawScale = baseScale * scale

    val matrix = Matrix()
    matrix.postScale(drawScale, drawScale)
    val scaledW = imgW * drawScale
    val scaledH = imgH * drawScale
    val cropRatio = (outputSize / (min(source.width, source.height) * baseScale * 0.85f))
    val translateX = (outputSize - scaledW) / 2f + offset.x * cropRatio
    val translateY = (outputSize - scaledH) / 2f + offset.y * cropRatio
    matrix.postTranslate(translateX, translateY)

    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG or android.graphics.Paint.FILTER_BITMAP_FLAG)
    canvas.drawBitmap(source, matrix, paint)
    return output
}
