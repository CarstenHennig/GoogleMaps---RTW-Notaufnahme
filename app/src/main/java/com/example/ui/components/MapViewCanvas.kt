package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LatLngPoint
import com.example.model.RouteInfo
import kotlin.math.max
import kotlin.math.min

@Composable
fun MapViewCanvas(
  routeInfo: RouteInfo,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseRadius by infiniteTransition.animateFloat(
    initialValue = 14f,
    targetValue = 32f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "rtwPulse"
  )
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.8f,
    targetValue = 0.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "rtwAlpha"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(240.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(Color(0xFF131B24))
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val canvasWidth = size.width
      val canvasHeight = size.height
      val paddingPx = 48f

      // 1. Draw subtle tactical grid lines
      val gridSpacing = 40f
      var gx = 0f
      while (gx < canvasWidth) {
        drawLine(
          color = Color(0xFF1E293B).copy(alpha = 0.6f),
          start = Offset(gx, 0f),
          end = Offset(gx, canvasHeight),
          strokeWidth = 1f
        )
        gx += gridSpacing
      }
      var gy = 0f
      while (gy < canvasHeight) {
        drawLine(
          color = Color(0xFF1E293B).copy(alpha = 0.6f),
          start = Offset(0f, gy),
          end = Offset(canvasWidth, gy),
          strokeWidth = 1f
        )
        gy += gridSpacing
      }

      val allPoints = if (routeInfo.polylinePoints.isNotEmpty()) {
        routeInfo.polylinePoints
      } else {
        listOf(routeInfo.startPoint, routeInfo.destinationPoint)
      }

      var minLat = allPoints.minOf { it.latitude }
      var maxLat = allPoints.maxOf { it.latitude }
      var minLon = allPoints.minOf { it.longitude }
      var maxLon = allPoints.maxOf { it.longitude }

      // Guard against identical coordinates
      if (maxLat - minLat < 0.001) {
        minLat -= 0.002
        maxLat += 0.002
      }
      if (maxLon - minLon < 0.001) {
        minLon -= 0.002
        maxLon += 0.002
      }

      fun project(point: LatLngPoint): Offset {
        val nx = ((point.longitude - minLon) / (maxLon - minLon)).toFloat().coerceIn(0f, 1f)
        // Flip y because canvas y=0 is top, latitude increases towards north
        val ny = 1f - ((point.latitude - minLat) / (maxLat - minLat)).toFloat().coerceIn(0f, 1f)

        val px = paddingPx + nx * (canvasWidth - paddingPx * 2)
        val py = paddingPx + ny * (canvasHeight - paddingPx * 2)
        return Offset(px, py)
      }

      // 2. Draw driving route path
      val path = Path()
      allPoints.forEachIndexed { index, point ->
        val offset = project(point)
        if (index == 0) {
          path.moveTo(offset.x, offset.y)
        } else {
          path.lineTo(offset.x, offset.y)
        }
      }

      // Outer route casing / shadow
      drawPath(
        path = path,
        color = Color(0xFF0288D1).copy(alpha = 0.35f),
        style = Stroke(
          width = 16f,
          cap = StrokeCap.Round,
          join = StrokeJoin.Round
        )
      )

      // Main Route Line (Signal Blue with white core)
      drawPath(
        path = path,
        color = Color(0xFF00E5FF),
        style = Stroke(
          width = 7f,
          cap = StrokeCap.Round,
          join = StrokeJoin.Round
        )
      )
      drawPath(
        path = path,
        color = Color.White,
        style = Stroke(
          width = 2.5f,
          cap = StrokeCap.Round,
          join = StrokeJoin.Round
        )
      )

      // 3. Draw Start Marker (RTW Position)
      val startOffset = project(routeInfo.startPoint)

      // Pulse circle
      drawCircle(
        color = Color(0xFF00E5FF).copy(alpha = pulseAlpha),
        radius = pulseRadius,
        center = startOffset
      )
      // Solid outer ring
      drawCircle(
        color = Color(0xFF0D47A1),
        radius = 14f,
        center = startOffset
      )
      // Inner RTW dot
      drawCircle(
        color = Color(0xFF00E5FF),
        radius = 8f,
        center = startOffset
      )
      drawCircle(
        color = Color.White,
        radius = 3.5f,
        center = startOffset
      )

      // 4. Draw Destination Marker (Hospital Emergency Room)
      val destOffset = project(routeInfo.destinationPoint)

      // Outer Red Glow
      drawCircle(
        color = Color(0xFFD32F2F).copy(alpha = 0.3f),
        radius = 24f,
        center = destOffset
      )
      // Red Base Pin
      drawCircle(
        color = Color(0xFFD32F2F),
        radius = 16f,
        center = destOffset
      )
      // Medical Cross inside
      val crossSize = 7f
      val crossThickness = 3f
      // Horizontal cross bar
      drawLine(
        color = Color.White,
        start = Offset(destOffset.x - crossSize, destOffset.y),
        end = Offset(destOffset.x + crossSize, destOffset.y),
        strokeWidth = crossThickness,
        cap = StrokeCap.Round
      )
      // Vertical cross bar
      drawLine(
        color = Color.White,
        start = Offset(destOffset.x, destOffset.y - crossSize),
        end = Offset(destOffset.x, destOffset.y + crossSize),
        strokeWidth = crossThickness,
        cap = StrokeCap.Round
      )
    }

    // Overlay Badge for Quick Telemetry (Top Left: Paramedic Route Header)
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = Color(0xFF1E293B).copy(alpha = 0.92f),
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(10.dp)
    ) {
      Text(
        text = "🚑 RTW → 🏥 ${routeInfo.destinationName}",
        color = Color.White,
        fontSize = 11.sp,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        maxLines = 1
      )
    }

    // Overlay Badge (Bottom Right: ETA & Distance)
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = Color(0xFF0D47A1).copy(alpha = 0.95f),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(10.dp)
    ) {
      Text(
        text = "Fahrstrecke: ${routeInfo.distanceKmFormatted} • ETA ca. ${routeInfo.durationMinutesFormatted}",
        color = Color.White,
        fontSize = 11.sp,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
      )
    }
  }
}
