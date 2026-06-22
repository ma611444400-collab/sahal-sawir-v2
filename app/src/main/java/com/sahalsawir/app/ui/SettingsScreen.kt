package com.sahalsawir.app.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahalsawir.app.model.CreditManager
import com.sahalsawir.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var isPremiumActive by remember { mutableStateOf(CreditManager.isPremium(context)) }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, OutlineBorder, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Developer Sandbox Tools", color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Isticmaal kuwan si aad u tijaabiso xaddiga maalinlaha ah iyo lacag bixinta.", color = TextSecondary, fontSize = 12.sp)
                    HorizontalDivider(color = OutlineBorder)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Dhibcaha haray maanta", color = TextPrimary, fontSize = 14.sp)
                        Text("${CreditManager.getRemainingCredits(context)} / 3", color = AccentTeal, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Button(
                        onClick = {
                            val prefs = context.getSharedPreferences("sahal_sawir_prefs", android.content.Context.MODE_PRIVATE)
                            prefs.edit().putInt("credits_remaining", 3).putLong("first_use_timestamp", 0L).apply()
                            Toast.makeText(context, "Credits Reset to 3/3!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceContainer),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = PrimarySlate)
                        Spacer(Modifier.width(6.dp))
                        Text("Reset Daily Credits to 3", color = PrimarySlate)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Simulate PRO Subscription", color = TextPrimary, fontSize = 14.sp)
                        Switch(
                            checked = isPremiumActive,
                            onCheckedChange = { status ->
                                CreditManager.setPremium(context, status)
                                isPremiumActive = status
                                Toast.makeText(context, if (status) "PRO unlocked!" else "PRO disabled", Toast.LENGTH_SHORT).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PremiumGold,
                                checkedTrackColor = PremiumGold.copy(alpha = 0.5f),
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = OutlineBorder
                            )
                        )
                    }
                }
            }
        }
    }
}
