package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KANPUR_LANDMARKS
import com.example.data.model.KanpurLocation
import com.example.data.model.TripStatus
import kotlin.math.sqrt

@Composable
fun KanpurMapView(
    pickupLocation: KanpurLocation,
    dropLocation: KanpurLocation,
    onPickupChanged: (KanpurLocation) -> Unit,
    onDropChanged: (KanpurLocation) -> Unit,
    activeTripStatus: TripStatus?,
    liveVehicleProgress: Float,
    isNightMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var isSatelliteView by remember { mutableStateOf(false) }

    // Map bounds in Kanpur latitude/longitude
    val minLat = 26.38
    val maxLat = 26.54
    val minLng = 80.20
    val maxLng = 80.45

    val textMeasurer = rememberTextMeasurer()

    // Smooth animated progress for moving driver pin
    val animatedProgress by animateFloatAsState(
        targetValue = liveVehicleProgress,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "vehicleProgress"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("kanpur_map_view")
    ) {
        // Fullscreen Interactive Canvas Map
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        panOffset += dragAmount
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        // Check if tap hit any landmark node
                        val w = size.width.toFloat()
                        val h = size.height.toFloat()

                        KANPUR_LANDMARKS.forEach { landmark ->
                            val x = ((landmark.longitude - minLng) / (maxLng - minLng) * w * zoomScale).toFloat() + panOffset.x
                            val y = ((maxLat - landmark.latitude) / (maxLat - minLat) * h * zoomScale).toFloat() + panOffset.y
                            val dist = sqrt((tapOffset.x - x) * (tapOffset.x - x) + (tapOffset.y - y) * (tapOffset.y - y))
                            if (dist < 40f * zoomScale) {
                                onDropChanged(landmark)
                            }
                        }
                    }
                }
        ) {
            val width = size.width
            val height = size.height

            // Colors based on Satellite or Night or Vector view
            val bgColor = when {
                isSatelliteView -> Color(0xFF0F172A)
                isNightMode -> Color(0xFF1E293B)
                else -> Color(0xFFF1F5F9)
            }
            val roadColor = if (isSatelliteView || isNightMode) Color(0xFF334155) else Color(0xFFFFFFFF)
            val majorRoadColor = if (isSatelliteView || isNightMode) Color(0xFF475569) else Color(0xFFE2E8F0)
            val riverColor = Color(0xFF0284C7)
            val gridColor = if (isNightMode) Color(0xFF334155).copy(alpha = 0.3f) else Color(0xFFCBD5E1).copy(alpha = 0.4f)

            // Draw Background Canvas
            drawRect(color = bgColor, size = size)

            // Helper to translate lat/lng to canvas x/y
            fun toCanvasOffset(lat: Double, lng: Double): Offset {
                val nx = (lng - minLng) / (maxLng - minLng)
                val ny = (maxLat - lat) / (maxLat - minLat)
                val x = (nx * width * zoomScale).toFloat() + panOffset.x
                val y = (ny * height * zoomScale).toFloat() + panOffset.y
                return Offset(x, y)
            }

            // Draw Grid lines
            val step = 80f * zoomScale
            var gx = panOffset.x % step
            while (gx < width) {
                drawLine(gridColor, Offset(gx, 0f), Offset(gx, height), strokeWidth = 1f)
                gx += step
            }
            var gy = panOffset.y % step
            while (gy < height) {
                drawLine(gridColor, Offset(0f, gy), Offset(width, gy), strokeWidth = 1f)
                gy += step
            }

            // Draw River Ganges Curve (Kanpur North boundary)
            val riverPath = Path().apply {
                val p1 = toCanvasOffset(26.5300, 80.2100)
                val p2 = toCanvasOffset(26.5100, 80.3000)
                val p3 = toCanvasOffset(26.4700, 80.3800)
                val p4 = toCanvasOffset(26.4300, 80.4400)
                moveTo(p1.x, p1.y)
                cubicTo(p2.x, p2.y, p3.x, p3.y, p4.x, p4.y)
            }
            drawPath(
                path = riverPath,
                color = riverColor.copy(alpha = 0.7f),
                style = Stroke(width = 32f * zoomScale, cap = StrokeCap.Round)
            )

            // Label Ganges River
            val riverMid = toCanvasOffset(26.4900, 80.3400)
            drawText(
                textMeasurer = textMeasurer,
                text = "Ganges River (Ganga Barrage)",
                style = TextStyle(color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold),
                topLeft = Offset(riverMid.x - 80f, riverMid.y - 30f)
            )

            // Draw Major Kanpur Roads (GT Road, Mall Road, Bypass)
            val gtRoad = Path().apply {
                val r1 = toCanvasOffset(26.5200, 80.2200)
                val r2 = toCanvasOffset(26.4800, 80.3100)
                val r3 = toCanvasOffset(26.4500, 80.3600)
                val r4 = toCanvasOffset(26.4000, 80.4200)
                moveTo(r1.x, r1.y)
                lineTo(r2.x, r2.y)
                lineTo(r3.x, r3.y)
                lineTo(r4.x, r4.y)
            }
            drawPath(gtRoad, color = majorRoadColor, style = Stroke(width = 16f * zoomScale, cap = StrokeCap.Round))
            drawPath(gtRoad, color = roadColor, style = Stroke(width = 10f * zoomScale, cap = StrokeCap.Round))

            val mallRoad = Path().apply {
                val m1 = toCanvasOffset(26.4750, 80.3100)
                val m2 = toCanvasOffset(26.4680, 80.3510)
                val m3 = toCanvasOffset(26.4530, 80.3520)
                moveTo(m1.x, m1.y)
                lineTo(m2.x, m2.y)
                lineTo(m3.x, m3.y)
            }
            drawPath(mallRoad, color = Color(0xFFF59E0B).copy(alpha = 0.6f), style = Stroke(width = 12f * zoomScale, cap = StrokeCap.Round))

            // Draw Landmark Nodes
            KANPUR_LANDMARKS.forEach { landmark ->
                val pos = toCanvasOffset(landmark.latitude, landmark.longitude)
                drawCircle(
                    color = if (isNightMode) Color(0xFF38BDF8) else Color(0xFF0284C7),
                    radius = 8f * zoomScale,
                    center = pos
                )
                drawCircle(
                    color = Color.White,
                    radius = 4f * zoomScale,
                    center = pos
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = landmark.name,
                    style = TextStyle(
                        color = if (isNightMode || isSatelliteView) Color(0xFFE2E8F0) else Color(0xFF334155),
                        fontSize = (10 * zoomScale).sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    topLeft = Offset(pos.x + 12f, pos.y - 10f)
                )
            }

            // Draw Pickup (Green) and Drop (Red) Offsets
            val pickupPos = toCanvasOffset(pickupLocation.latitude, pickupLocation.longitude)
            val dropPos = toCanvasOffset(dropLocation.latitude, dropLocation.longitude)

            // Draw Dashed Route Polyline
            val routePath = Path().apply {
                moveTo(pickupPos.x, pickupPos.y)
                val midX = (pickupPos.x + dropPos.x) / 2 + 30f
                val midY = (pickupPos.y + dropPos.y) / 2 - 30f
                quadraticTo(midX, midY, dropPos.x, dropPos.y)
            }

            drawPath(
                path = routePath,
                color = Color(0xFFFF6D00),
                style = Stroke(
                    width = 6f * zoomScale,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 12f), 0f),
                    cap = StrokeCap.Round
                )
            )

            // Draw Green Pickup Marker Pin
            drawCircle(Color(0xFF22C55E).copy(alpha = 0.3f), radius = 24f * zoomScale, center = pickupPos)
            drawCircle(Color(0xFF22C55E), radius = 12f * zoomScale, center = pickupPos)
            drawCircle(Color.White, radius = 6f * zoomScale, center = pickupPos)
            drawRoundRect(
                color = Color(0xFF15803D),
                topLeft = Offset(pickupPos.x - 45f, pickupPos.y - 50f),
                size = Size(90f, 32f),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawText(
                textMeasurer = textMeasurer,
                text = "PICKUP",
                style = TextStyle(color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold),
                topLeft = Offset(pickupPos.x - 28f, pickupPos.y - 44f)
            )

            // Draw Red Drop Marker Pin
            drawCircle(Color(0xFFEF4444).copy(alpha = 0.3f), radius = 24f * zoomScale, center = dropPos)
            drawCircle(Color(0xFFEF4444), radius = 12f * zoomScale, center = dropPos)
            drawCircle(Color.White, radius = 6f * zoomScale, center = dropPos)
            drawRoundRect(
                color = Color(0xFFB91C1C),
                topLeft = Offset(dropPos.x - 40f, dropPos.y - 50f),
                size = Size(80f, 32f),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawText(
                textMeasurer = textMeasurer,
                text = "DROP",
                style = TextStyle(color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold),
                topLeft = Offset(dropPos.x - 22f, dropPos.y - 44f)
            )

            // Draw Live Moving Vehicle Marker if trip IN_PROGRESS
            if (activeTripStatus == TripStatus.IN_PROGRESS) {
                val currentVehicleX = pickupPos.x + (dropPos.x - pickupPos.x) * animatedProgress
                val currentVehicleY = pickupPos.y + (dropPos.y - pickupPos.y) * animatedProgress
                val vehiclePos = Offset(currentVehicleX, currentVehicleY)

                drawCircle(Color(0xFFFF6D00).copy(alpha = 0.4f), radius = 30f * zoomScale, center = vehiclePos)
                drawCircle(Color(0xFF0F172A), radius = 16f * zoomScale, center = vehiclePos)
                drawCircle(Color(0xFFFF6D00), radius = 10f * zoomScale, center = vehiclePos)

                drawText(
                    textMeasurer = textMeasurer,
                    text = "🚖 YatraX Active",
                    style = TextStyle(color = Color(0xFFFF6D00), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold),
                    topLeft = Offset(vehiclePos.x - 40f, vehiclePos.y + 20f)
                )
            }
        }

        // Floating Map Controls (Zoom, Center, Layers)
        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 110.dp, end = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(4.dp)) {
                IconButton(
                    onClick = { zoomScale = (zoomScale + 0.2f).coerceAtMost(2.5f) },
                    modifier = Modifier.testTag("zoom_in_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Zoom In")
                }

                IconButton(
                    onClick = { zoomScale = (zoomScale - 0.2f).coerceAtLeast(0.6f) },
                    modifier = Modifier.testTag("zoom_out_button")
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
                }

                IconButton(
                    onClick = { panOffset = Offset.Zero; zoomScale = 1.0f },
                    modifier = Modifier.testTag("recenter_button")
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Recenter Kanpur", tint = MaterialTheme.colorScheme.primary)
                }

                IconButton(
                    onClick = { isSatelliteView = !isSatelliteView },
                    modifier = Modifier.testTag("map_layer_toggle")
                ) {
                    Icon(
                        Icons.Default.Layers,
                        contentDescription = "Map Style Layer",
                        tint = if (isSatelliteView) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Bottom Map Location Badge
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 110.dp, start = 16.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Navigation,
                    contentDescription = null,
                    tint = Color(0xFFFF6D00),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Kanpur Region • Live Map",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
