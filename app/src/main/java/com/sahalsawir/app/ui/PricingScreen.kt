package com.sahalsawir.app.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import com.sahalsawir.app.model.CreditManager
import com.sahalsawir.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PricingScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showCheckoutModal by remember { mutableStateOf(false) }
    var isCheckingOut by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf("EVC") }
    var evcPhone by remember { mutableStateOf("0611444400") }
    var evcPin by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    val priceDisplay = CreditManager.getMonthlyPriceDisplay(context)

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Qorshayaasha & Lacag-bixinta", fontWeight = FontWeight.Bold, color = TextPrimary) },
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
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Awood geli Akawnka PRO", color = PremiumGold, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(6.dp))
                Text("Hagaaji sawiradaada adoo isticmaalaya AI Istuudiyaha.", color = TextSecondary, fontSize = 14.sp, textAlign = TextAlign.Center)
            }

            Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth().border(1.dp, OutlineBorder, RoundedCornerShape(24.dp))) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Heerka Bilaashka Ah", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Ku habboon tijaabinta", color = TextSecondary, fontSize = 12.sp)
                        }
                        Text("$0 /bil", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    HorizontalDivider(color = OutlineBorder)
                    listOf("3 hagaajin maalin kasta", "Ilaa 2x ballaarinta", "Safka caadiga ah", "Tirtiridda buuqa ee caadiga ah").forEach {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.CheckCircle, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            Text(it, color = TextSecondary, fontSize = 13.sp)
                        }
                    }
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth().border(2.dp, PremiumGold, RoundedCornerShape(24.dp))) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.WorkspacePremium, null, tint = PremiumGold, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Heerka PRO", color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Text("Xidhmada Sare ee Bilicda", color = TextSecondary, fontSize = 12.sp)
                        }
                        Text(priceDisplay, color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    HorizontalDivider(color = OutlineBorder)
                    listOf("Hagaajin aan xadidnayn", "Ilaa 8x ballaarinta", "Mashiin degdeg ah", "Ma jiraan calaamad biyo ah", "Helitaanka hore ee moodellada").forEach {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Star, null, tint = PremiumGold, modifier = Modifier.size(16.dp))
                            Text(it, color = TextPrimary, fontSize = 13.sp)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    if (CreditManager.isPremium(context)) {
                        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray), modifier = Modifier.fillMaxWidth(), enabled = false) {
                            Text("Waa laguu kiciyay!", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { showCheckoutModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PremiumGold),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        ) {
                            Text("Kor u qaad (EVC Plus / Stripe)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }if (showCheckoutModal) {
        AnimatedVisibility(visible = showCheckoutModal, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth(0.92f).border(1.dp, PremiumGold, RoundedCornerShape(24.dp))
                ) {
                    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Text("Xaqiijinta Lacag Bixinta", color = PremiumGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            TextButton(onClick = { showCheckoutModal = false }) {
                                Text("Jooji", color = TextSecondary)
                            }
                        }
                        HorizontalDivider(color = OutlineBorder)

                        Row(modifier = Modifier.fillMaxWidth().background(DarkSurfaceContainer, RoundedCornerShape(12.dp)).padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(if (paymentMethod == "EVC") AccentTeal else Color.Transparent).clickable { paymentMethod = "EVC" },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("EVC Plus", color = if (paymentMethod == "EVC") Color.Black else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Box(
                                modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(if (paymentMethod == "STRIPE") AccentTeal else Color.Transparent).clickable { paymentMethod = "STRIPE" },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Kaarka Bankiga", color = if (paymentMethod == "STRIPE") Color.Black else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        if (paymentMethod == "EVC") {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Card(colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("Ku bixi lacagta lambarka EVC Plus:", color = TextSecondary, fontSize = 11.sp)
                                        Text(evcPhone, color = PremiumGold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                        Text("Dial: *712*${evcPhone}*1# si aad u wareejiso ${priceDisplay}", color = AccentTeal, fontSize = 11.sp)
                                    }
                                }
                                OutlinedTextField(value = evcPhone, onValueChange = { evcPhone = it }, label = { Text("Lambarkaaga EVC Plus") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                                OutlinedTextField(value = evcPin, onValueChange = { evcPin = it }, label = { Text("PIN-ka (Ikhtiyaari)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Waxaad u dalbanaysaa PRO bishii ${priceDisplay}.", color = TextSecondary, fontSize = 13.sp)
                                OutlinedTextField(value = cardNumber, onValueChange = { cardNumber = it }, label = { Text("Lambarka Kaarka") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    OutlinedTextField(value = expirationDate, onValueChange = { expirationDate = it }, label = { Text("MM/YY") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                                    OutlinedTextField(value = cvv, onValueChange = { cvv = it }, label = { Text("CVV") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                                }
                            }
                        }

                        Button(
                            onClick = {
                                isCheckingOut = true
                                scope.launch {
                                    delay(1500)
                                    CreditManager.setPremium(context, true)
                                    isCheckingOut = false
                                    showCheckoutModal = false
                                    Toast.makeText(context, "Ku soo dhowaw PRO!", Toast.LENGTH_LONG).show()
                                    onBack()
                                }
                            },
                            enabled = !isCheckingOut,
                            colors = ButtonDefaults.buttonColors(containerColor = PremiumGold),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        ) {
                            Text(if (isCheckingOut) "Xaqiijinaya..." else "Bixi ${priceDisplay} & Kici PRO", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
