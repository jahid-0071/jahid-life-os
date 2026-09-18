package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PriorityLevel
import com.example.data.model.SmartTaskType
import com.example.ui.theme.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
    content: @Composable ColumnScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable { onClick() }
    } else Modifier

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .then(clickableModifier)
            .padding(16.dp),
        content = content
    )
}

@Composable
fun MetricRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    strokeWidth: Dp = 6.dp,
    primaryColor: Color = AccentIndigo,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    centerContent: @Composable () -> Unit = {}
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(700),
        label = "ring_progress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            // Track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke
            )
            // Progress
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(primaryColor, AccentCyan, primaryColor)
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = stroke
            )
        }
        centerContent()
    }
}

@Composable
fun PriorityBadge(priority: Int, modifier: Modifier = Modifier) {
    val (color, text) = when (priority) {
        1 -> PriorityP1 to "P1 Urgent"
        2 -> PriorityP2 to "P2 High"
        3 -> PriorityP3 to "P3 Medium"
        4 -> PriorityP4 to "P4 Normal"
        else -> PriorityP5 to "P5 Low"
    }

    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.18f),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun TaskTypeBadge(typeString: String, modifier: Modifier = Modifier) {
    val type = try {
        SmartTaskType.valueOf(typeString)
    } catch (e: Exception) {
        SmartTaskType.DEEP_WORK
    }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            val icon = getTaskTypeIcon(type)
            Icon(
                imageVector = icon,
                contentDescription = type.label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = type.label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

fun getTaskTypeIcon(type: SmartTaskType): ImageVector {
    return when (type) {
        SmartTaskType.QUICK -> Icons.Filled.Bolt
        SmartTaskType.DEEP_WORK -> Icons.Filled.Psychology
        SmartTaskType.STUDY -> Icons.Filled.MenuBook
        SmartTaskType.CODING -> Icons.Filled.Terminal
        SmartTaskType.READING -> Icons.Filled.AutoStories
        SmartTaskType.PROJECT -> Icons.Filled.AccountTree
        SmartTaskType.HABIT -> Icons.Filled.CheckCircle
        SmartTaskType.FITNESS -> Icons.Filled.FitnessCenter
        SmartTaskType.RESEARCH -> Icons.Filled.Science
        SmartTaskType.EXAM -> Icons.Filled.Quiz
        SmartTaskType.MEETING -> Icons.Filled.Groups
        SmartTaskType.PERSONAL -> Icons.Filled.Person
    }
}

fun getHabitIcon(iconKey: String): ImageVector {
    return when (iconKey.lowercase()) {
        "water" -> Icons.Filled.WaterDrop
        "code", "terminal" -> Icons.Filled.Terminal
        "book", "read" -> Icons.Filled.MenuBook
        "gym", "fitness" -> Icons.Filled.FitnessCenter
        "meditate", "zen" -> Icons.Filled.SelfImprovement
        "sleep" -> Icons.Filled.Bedtime
        "walk" -> Icons.Filled.DirectionsWalk
        "study" -> Icons.Filled.School
        else -> Icons.Filled.CheckCircle
    }
}
