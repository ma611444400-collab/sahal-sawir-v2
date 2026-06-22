package com.sahalsawir.app.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.sahalsawir.app.model.CreditManager
import com.sahalsawir.app.model.ImageProcessor
import com.sahalsawir.app.model.EnhancementRecord
import com.sahalsawir.app.model.EnhancementRepository
import com.sahalsawir.app.model.ThumbnailHelper
import com.sahalsawir.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.InputStream

class SplitShape(private val percentage: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val rect = Rect(0f, 0f, size.width * percentage, size.height)
        return Outline.Rectangle(rect)
    }
}

data class PresetSample(
    val id: String,
    val name: String,
    val description: String,
    val rawResId: Int,
    val mockUrl: String,
    val info: String
)

data class FilterOption(
    val id: String,
    val name: String,
    val colorAccent: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancerScreen(
    onNavigateToPricing: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onShowPaywall: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { EnhancementRepository.getInstance(context) }
    val historyRecords by repository.allRecords.collectAsState(initial = emptyList())

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPreset by remember { mutableStateOf<PresetSample?>(null) }
    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var enhancedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var upscaleFactor by remember { mutableStateOf(4.0f) }
    var denoiseStrength by remember { mutableStateOf(0.5f) }
    var colorCorrection by remember { mutableStateOf(0.5f) }
    var selectedFilter by remember { mutableStateOf("asal") }
    var isProcessing by remember { mutableStateOf(false) }
    var progressCount by remember { mutableStateOf(0f) }
    var sliderPercentage by remember { mutableStateOf(0.5f) }
    var remainingCredits by remember { mutableStateOf(3) }

    val filterOptions = remember {
        listOf(
            FilterOption("asal", "Caadi (Normal)", androidx.compose.ui.graphics.Color.Gray),
            FilterOption("istuudiyo", "Istuudiyo Glow", PremiumGold),
            FilterOption("kulaal", "Retro Kulaal", androidx.compose.ui.graphics.Color(0xFFF59E0B)),
            FilterOption("qabow", "Ocean Qabow", AccentTeal),
            FilterOption("madow_caddaan", "Madow & Caddaan", androidx.compose.ui.graphics.Color.White)
        )
    }

    val presetSamples = remember {
        listOf(
            PresetSample("misty", "Misty Peak", "Landscape recovery", android.R.drawable.ic_menu_gallery,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuD9F1X6mXbKmWoUNd4JVoWKQlvEJaQQrbnckNNCgHASZIvFL5qpOqDwx8i0LlCbj1NBnJ5PouzvhJ0lfISB9vljod9Gt7z2Zr2dM2yxxnprpDPg_QayyuEI8JRRusAxU4L2z1fXQxgq2eWJj1tyjY2k5LC68Oa-zpvaiCtOiUQdhrehH5ysMuEPhmvCGgvRwKaiJsxU1qG671FkTRFBK6kvPtgj9XBo_-hVMl5ThgRW7icMVAeqOZ3UpWG4FSvSYMjnj7L3nn0TTj8",
                "RAW • 12MP • High Grain"),
            PresetSample("metallic", "Cyber Sphere", "Render sharpening", android.R.drawable.ic_menu_camera,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuBs0aOvp-AE0aU9a2YKFUeBzFNRa2673pQuOQwmlCRHVDU7QT3XOqSq_HTmNWy1S7jeFRwzr-p-O_pla6Tj82QqmF6O3OkEfHubKGsHJcWzi8T_0Zds78qha3Mpn87V-xVZZZlqfZX_qhGyaisbJ4lGaZTx6tv-wLPPrLZjvFLa9jdYoLvYWvE_NRK8LS6M26AEj2AUZjRafD0UnDLe2TUtGVwRdOWs7SJ3hyQCN6apoveaTbK_5H154XlEchKOk7SeVtZKOjh2sbQ",
                "Studio • 4K • Low Light"),
            PresetSample("silk", "Silken Wave", "Texture booster", android.R.drawable.ic_menu_slideshow,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuAnn4cjlOX7wDYpckGrHLT_Do_t8uKqWo1GU8yfRxIj6PWSqPiPDAx-fHJZQFxCWOSXxBWr_QvMeBxEKq_uDrSX1G0D8VqjnT9G4Gald68MXkDjnSxhc5hmDZjuzbBxxTPk76gpOUCGtYfiQuGvwwk_Xvycyi86i9gyOUinNHXu05TG7RR4lSTevDMFA7W4UIsR1bwlV43yW4LeP5-dQP0LOTTMlhpCOfRcXAfjPPaA6oboB5FQX_eYNBlsP8uTRyQjJyemz5zugvo",
                "Macro • ISO 400 • Soft Focus")
        )
    }

    LaunchedEffect(Unit) {
        remainingCredits = CreditManager.getRemainingCredits(context)
        selectedPreset = presetSamples.first()
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            selectedPreset = null
            enhancedBitmap = null
            scope.launch {
                try {
                    val stream: InputStream? = context.contentResolver.openInputStream(uri)
                    originalBitmap = BitmapFactory.decodeStream(stream)
                    stream?.close()
                } catch (e: Exception) {
                    Toast.makeText(context, "Khalad: Sawirka waa la soo rari waayay", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
Scaffold(
        modifier = Modifier.fillMaxSize().background(DarkBackground),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AccentTeal, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Sahal Sawir", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp, fontFamily = FontFamily.SansSerif)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface),
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = TextSecondary)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Hagaaji tayada sawiradaada.", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Soo geli sawirro tayadoodu hooseyso. Tiknoolajiyadeena AI ayaa ku soo celin doonta heerka istuudiyaha.", color = TextSecondary, fontSize = 14.sp, lineHeight = 20.sp)

            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(DarkSurface).border(1.dp, OutlineBorder, RoundedCornerShape(24.dp)).clickable(enabled = !isProcessing) { galleryLauncher.launch("image/*") }.padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(AccentTeal.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = AccentTeal, modifier = Modifier.size(28.dp))
                    }
                    Text("Ka soo geli sawir qalabkaaga", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Text("Wuxuu taageeraa JPEG, PNG, iyo RAW ilaa 50MB", color = TextSecondary, fontSize = 12.sp, textAlign = TextAlign.Center)
                    Button(onClick = { galleryLauncher.launch("image/*") }, colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceContainer), shape = RoundedCornerShape(10.dp)) {
                        Text("Eeg Faylasha", color = PrimarySlate)
                    }
                }
            }

            Text("Ama ka dooro Moodal diyaarsan", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(presetSamples) { sample ->
                    val isSelected = selectedPreset?.id == sample.id && selectedUri == null
                    Card(
                        onClick = { if (!isProcessing) { selectedPreset = sample; selectedUri = null; enhancedBitmap = null; originalBitmap = null } },
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) DarkSurfaceContainer else DarkSurface),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.width(150.dp).border(if (isSelected) 2.dp else 1.dp, if (isSelected) AccentTeal else OutlineBorder, RoundedCornerShape(18.dp))
                    ) {
                        Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                            Box(modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(14.dp))) {
                                Image(painter = rememberAsyncImagePainter(model = sample.mockUrl), contentDescription = sample.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(sample.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(sample.description, color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val remaining = CreditManager.getRemainingCredits(context)
                    if (remaining <= 0) { onShowPaywall(); return@Button }
                    isProcessing = true; progressCount = 0f
                    scope.launch {
                        while (progressCount < 1.0f) { delay(200); progressCount += 0.12f }
                        try {
                            val sourceBmp = originalBitmap ?: run {
                                val w = 400; val h = 300
                                val mockBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                                val canvas = android.graphics.Canvas(mockBmp)
                                val p = android.graphics.Paint()
                                p.color = android.graphics.Color.DKGRAY
                                canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), p)
                                mockBmp
                            }
                            val result = ImageProcessor.enhanceBitmap(sourceBmp, upscaleFactor, denoiseStrength, colorCorrection, selectedFilter)
                            originalBitmap = sourceBmp; enhancedBitmap = result
                            val thumbBase64 = ThumbnailHelper.createThumbnailBase64(result)
                            repository.saveRecord(EnhancementRecord(
                                sourceName = if (selectedUri != null) "Sawirkaaga" else (selectedPreset?.name ?: "Sawir"),
                                upscaleFactor = "${upscaleFactor.toInt()}x",
                                denoisePercent = (denoiseStrength * 100).toInt(),
                                colorPercent = (colorCorrection * 100).toInt(),
                                appliedFilter = when(selectedFilter) { "madow_caddaan" -> "Madow & Caddaan"; "kulaal" -> "Retro Kulaal"; "qabow" -> "Ocean Qabow"; "istuudiyo" -> "Istuudiyo Glow"; else -> "Caadi" },
                                thumbnailBase64 = thumbBase64
                            ))
                            CreditManager.consumeCredit(context)
                            remainingCredits = CreditManager.getRemainingCredits(context)
                            Toast.makeText(context, "Si guul leh ayaa loo hagaajiyay!", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Hagaajintu way guuldaraysatay: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally { isProcessing = false }
                    }
                },
                enabled = !isProcessing,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentTeal),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = androidx.compose.ui.graphics.Color(0xFF0F172A))
                Spacer(Modifier.width(8.dp))
                Text(if (isProcessing) "Hagaajinaya..." else "Hagaaji Sawirka", color = androidx.compose.ui.graphics.Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }

    AnimatedVisibility(visible = isProcessing, enter = fadeIn(), exit = fadeOut()) {
        Box(modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.85f)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(32.dp)) {
                CircularProgressIndicator(color = AccentTeal, strokeWidth = 4.dp, modifier = Modifier.size(64.dp))
                Text("Hagaajinta Pixels-ka...", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                LinearProgressIndicator(progress = { progressCount.coerceIn(0f, 1f) }, color = AccentTeal, trackColor = OutlineBorder, modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)))
            }
        }
    }
}

fun rotateBitmap90(src: Bitmap): Bitmap {
    val matrix = android.graphics.Matrix().apply { postRotate(90f) }
    return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
}

fun flipBitmapHorizontal(src: Bitmap): Bitmap {
    val matrix = android.graphics.Matrix().apply { postScale(-1f, 1f) }
    return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
}

fun saveBitmapToGallery(context: Context, bitmap: Bitmap, displayName: String): Boolean {
    val resolver = context.contentResolver
    val imagesUri = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        android.provider.MediaStore.Images.Media.getContentUri(android.provider.MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else { android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI }
    val contentValues = android.content.ContentValues().apply {
        put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, "${displayName}_${System.currentTimeMillis()}.jpg")
        put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SahalSawir")
            put(android.provider.MediaStore.Images.Media.IS_PENDING, 1)
        }
    }
    val uri = resolver.insert(imagesUri, contentValues) ?: return false
    return try {
        val outputStream = resolver.openOutputStream(uri)
        if (outputStream != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
            outputStream.close()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(android.provider.MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
            true
        } else { resolver.delete(uri, null, null); false }
    } catch (e: Exception) { resolver.delete(uri, null, null); false }
}
