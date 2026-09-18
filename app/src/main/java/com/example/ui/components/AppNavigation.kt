package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfileEntity
import com.example.ui.NavigationModule
import com.example.ui.theme.*

@Composable
fun LifeOsTopBar(
    profile: UserProfileEntity,
    onSearchClick: () -> Unit,
    onAiClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // App Brand & Life Score
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onProfileClick() }
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(AccentIndigo, AccentCyan))
                    )
                    .border(1.5.dp, GlassBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "OS",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "LIFE OS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = AccentEmerald.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${profile.lifeScore}% SCORE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentEmerald,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "Lv. ${profile.level} • ${profile.xp} XP • ${profile.streakDays}d Streak",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Action Buttons: Search, AI OS, Theme/Settings
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag("search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                onClick = onAiClick,
                shape = RoundedCornerShape(20.dp),
                color = AccentIndigo.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentIndigo.copy(alpha = 0.4f)),
                modifier = Modifier.testTag("ai_assistant_top_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Assistant",
                        tint = AccentIndigo,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "AI OS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AccentIndigo
                    )
                }
            }
        }
    }
}

@Composable
fun ModuleNavigationTabs(
    selectedModule: NavigationModule,
    onSelectModule: (NavigationModule) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NavigationModule.entries.forEach { module ->
            val isSelected = module == selectedModule
            val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)

            Surface(
                onClick = { onSelectModule(module) },
                shape = RoundedCornerShape(12.dp),
                color = bgColor,
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                modifier = Modifier.testTag("nav_tab_${module.name.lowercase()}")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = getModuleIcon(module),
                        contentDescription = module.label,
                        tint = textColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = module.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor
                    )
                    if (module.badge != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            color = AccentCyan.copy(alpha = 0.2f),
                            shape = CircleShape
                        ) {
                            Text(
                                text = module.badge,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getModuleIcon(module: NavigationModule) = when (module) {
    NavigationModule.DASHBOARD -> Icons.Default.Dashboard
    NavigationModule.PLANNER -> Icons.Default.ViewTimeline
    NavigationModule.TASKS -> Icons.Default.TaskAlt
    NavigationModule.PROJECTS -> Icons.Default.FolderSpecial
    NavigationModule.ROADMAPS -> Icons.Default.Schema
    NavigationModule.HABITS -> Icons.Default.Repeat
    NavigationModule.STUDY -> Icons.Default.School
    NavigationModule.FOCUS -> Icons.Default.Timer
    NavigationModule.AI_ASSISTANT -> Icons.Default.AutoAwesome
    NavigationModule.VAULT -> Icons.Default.Inventory2
    NavigationModule.JOURNAL -> Icons.Default.EditNote
    NavigationModule.HEALTH_FINANCE -> Icons.Default.QueryStats
    NavigationModule.ANALYTICS -> Icons.Default.Insights
}
