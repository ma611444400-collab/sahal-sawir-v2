
package com.sahalsawir.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahalsawir.app.model.CreditManager
import com.sahalsawir.app.ui.EnhancerScreen
import com.sahalsawir.app.ui.PricingScreen
import com.sahalsawir.app.ui.SettingsScreen
import com.sahalsawir.app.ui.theme.*

enum class AppScreen { Workspace, Pricing, Settings }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppHost()
            }
        }
    }
}

@Composable
fun MainAppHost() {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(AppScreen.Workspace) }
    var showPaywallDialog by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = DarkBackground) {
        when (currentScreen) {
            AppScreen.Workspace -> EnhancerScreen(
                onNavigateToPricing = { currentScreen = AppScreen.Pricing },
                onNavigateToSettings = { currentScreen = AppScreen.Settings },
                onShowPaywall = { showPaywallDialog = true }
            )
            AppScreen.Pricing -> PricingScreen(onBack = { currentScreen = AppScreen.Workspace })
            AppScreen.Settings -> SettingsScreen(onBack = { currentScreen = AppScreen.Workspace })
        }

        if (showPaywallDialog) {
            AlertDialog(
                onDismissRequest = { showPaywallDialog = false },
                modifier = Modifier.clip(RoundedCornerShape(24.dp)).border(1.dp, PremiumGold, RoundedCornerShape(24.dp)),
                icon = {
                    Box(
                        modifier = Modifier.size(60.dp).clip(RoundedCornerShape(30.dp)).background(PremiumGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = PremiumGold, modifier = Modifier.size(32.dp))
                    }
                },
                title = {
                    Text("Xaddiga Maalinlaha ah waa la Gaaray", color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Maanta waxaad si guul leh u hagaajisay 3 sawir! Xaddigaagu wuxuu dib u bilaaban doonaa 24 saacadood gudahood.", color = TextPrimary, fontSize = 14.sp, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(16.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("Helitaanka PRO ee Bishii", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Hagaajin aan xadidnayn", color = TextSecondary, fontSize = 11.sp)
                                }
                                Text("$1/bishii", color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showPaywallDialog = false; currentScreen = AppScreen.Pricing },
                        colors = ButtonDefaults.buttonColors(containerColor = PremiumGold),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Kici & Furo PRO", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPaywallDialog = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("Malaha hadhow", color = TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                },
                containerColor = DarkSurface
            )
        }
    }
}
