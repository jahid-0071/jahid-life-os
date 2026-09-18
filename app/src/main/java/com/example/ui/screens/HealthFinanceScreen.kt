package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HealthFinanceEntity
import com.example.ui.LifeOsViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.MetricRing
import com.example.ui.theme.*

@Composable
fun HealthFinanceScreen(
    viewModel: LifeOsViewModel,
    modifier: Modifier = Modifier
) {
    val records by viewModel.recentHealthFinance.collectAsState()
    val today = records.firstOrNull() ?: HealthFinanceEntity()

    var showTransactionDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
    ) {
        item {
            Column {
                Text(
                    text = "BIOMETRICS & FINANCIAL CONTROL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = AccentEmerald
                )
                Text(
                    text = "Physical Vitality & Wealth Engine",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Health Section
        item {
            GlassCard(
                borderColor = AccentEmerald.copy(alpha = 0.4f),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NEURO-PHYSIOLOGY & HEALTH",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentEmerald
                    )
                    Text(
                        text = "Today's Telemetry",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Sleep
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Bedtime, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${today.sleepHours}h", fontSize = 16.sp, fontWeight = FontWeight.Black)
                        Text("Sleep", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    // Steps
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${today.steps}", fontSize = 16.sp, fontWeight = FontWeight.Black)
                        Text("Steps / 10k", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    // Workout
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${today.workoutMinutes}m", fontSize = 16.sp, fontWeight = FontWeight.Black)
                        Text("Training", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(10.dp))

                // Water Hydration Counter with 1-Tap Add
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Hydration", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("${today.waterGlasses} / 8 Glasses (Optimal)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Button(
                        onClick = { viewModel.addWaterGlass() },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("add_water_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+1 Glass", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }

        // Finance Section
        item {
            GlassCard(
                borderColor = AccentIndigo.copy(alpha = 0.4f),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FINANCIAL COMMAND & CASHFLOW",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentIndigo
                    )
                    TextButton(
                        onClick = { showTransactionDialog = true },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("+ Record", fontSize = 11.sp, color = AccentIndigo, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Monthly Income", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$${String.format("%,.0f", today.income)}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = AccentEmerald)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Monthly Expenses", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$${String.format("%,.0f", today.expenses)}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = PriorityP1)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Net Savings", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$${String.format("%,.0f", today.savings)}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = AccentCyan)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                val savingsRatio = if (today.income > 0) (today.savings / today.income).toFloat().coerceIn(0f, 1f) else 0f

                LinearProgressIndicator(
                    progress = { savingsRatio },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = AccentCyan,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${(savingsRatio * 100).toInt()}% Savings Rate (Target: >40%)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showTransactionDialog) {
        var inc by remember { mutableStateOf("") }
        var exp by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showTransactionDialog = false },
            title = { Text("Log Transaction", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = inc, onValueChange = { inc = it }, label = { Text("Income Added ($)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = exp, onValueChange = { exp = it }, label = { Text("Expense Logged ($)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val incomeVal = inc.toDoubleOrNull() ?: 0.0
                        val expenseVal = exp.toDoubleOrNull() ?: 0.0
                        viewModel.logTransaction(incomeVal, expenseVal)
                        showTransactionDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
                ) {
                    Text("Save Transaction")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTransactionDialog = false }) { Text("Cancel") }
            }
        )
    }
}
