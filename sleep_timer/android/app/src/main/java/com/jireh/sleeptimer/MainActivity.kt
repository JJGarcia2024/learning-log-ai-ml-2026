package com.jireh.sleeptimer

import android.app.PictureInPictureParams
import android.app.TimePickerDialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jireh.sleeptimer.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {

    companion object { const val EXTRA_AUTO_PIP = "auto_pip" }

    private val inPipState = mutableStateOf(false)
    private var wantAutoPip = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Make sure the daily 9 PM alarm is armed.
        SleepAlarmScheduler.schedule(this)

        wantAutoPip = intent.getBooleanExtra(EXTRA_AUTO_PIP, false)

        setContent {
            val context = LocalContext.current
            val prefs = remember { SleepPrefs(context) }

            var themeMode by remember { mutableStateOf(prefs.themeMode) }
            val systemDark = isSystemDark()
            val darkTheme = when (themeMode) {
                "light" -> false
                "dark"  -> true
                else    -> systemDark
            }

            SleepTimerTheme(darkTheme = darkTheme) {
                SleepTimerScreen(
                    prefs = prefs,
                    inPip = inPipState.value,
                    themeMode = themeMode,
                    onThemeChange = { themeMode = it; prefs.themeMode = it },
                    onEnterPip = { enterPip() },
                )
            }
        }

        if (wantAutoPip) enterPip()
    }

    private fun aspect() = Rational(2, 1)

    private fun pipParams() = PictureInPictureParams.Builder()
        .setAspectRatio(aspect())
        .build()

    fun enterPip() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            runCatching { enterPictureInPictureMode(pipParams()) }
        }
    }

    // Auto-float when the user navigates away during the sleep window.
    // Prefer the overlay (coexists with other apps' PiP); fall back to PiP.
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        val prefs = SleepPrefs(this)
        val snap = TimerEngine.snapshot(
            prefs.sleepHour, prefs.sleepMinute, prefs.wakeHour, prefs.wakeMinute
        )
        if (!snap.active) return
        if (Settings.canDrawOverlays(this)) {
            startForegroundService(Intent(this, FloatingTimerService::class.java))
        } else {
            enterPip()
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPip: Boolean, newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPip, newConfig)
        inPipState.value = isInPip
    }
}

@Composable
private fun isSystemDark(): Boolean {
    val config = LocalContext.current.resources.configuration
    return (config.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
        Configuration.UI_MODE_NIGHT_YES
}

/* ───────────────────────── Screen ───────────────────────── */

@Composable
fun SleepTimerScreen(
    prefs: SleepPrefs,
    inPip: Boolean,
    themeMode: String,
    onThemeChange: (String) -> Unit,
    onEnterPip: () -> Unit,
) {
    val context = LocalContext.current

    var sleepH by remember { mutableStateOf(prefs.sleepHour) }
    var sleepM by remember { mutableStateOf(prefs.sleepMinute) }
    var wakeH  by remember { mutableStateOf(prefs.wakeHour) }
    var wakeM  by remember { mutableStateOf(prefs.wakeMinute) }

    var wallpaperUri by remember { mutableStateOf(prefs.wallpaperUri) }
    var wallpaperOpacity by remember { mutableStateOf(prefs.wallpaperOpacity) }

    var showSettings by remember { mutableStateOf(false) }

    // Tick every second.
    var snapshot by remember {
        mutableStateOf(TimerEngine.snapshot(sleepH, sleepM, wakeH, wakeM))
    }
    var nowLabel by remember { mutableStateOf(currentClock()) }
    LaunchedEffect(sleepH, sleepM, wakeH, wakeM) {
        while (true) {
            snapshot = TimerEngine.snapshot(sleepH, sleepM, wakeH, wakeM)
            nowLabel = currentClock()
            delay(1000)
        }
    }

    // Wallpaper bitmap.
    val wallpaperBitmap by produceState<ImageBitmap?>(null, wallpaperUri) {
        value = wallpaperUri?.let { uriStr ->
            runCatching {
                val uri = Uri.parse(uriStr)
                val src = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(src).asImageBitmap()
            }.getOrNull()
        }
    }

    val wallpaperPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            wallpaperUri = uri.toString()
            prefs.wallpaperUri = wallpaperUri
        }
    }

    // Start the floating overlay (coexists with other PiP). Asks for the
    // "Display over other apps" permission the first time if it's missing.
    val startOverlay = {
        if (Settings.canDrawOverlays(context)) {
            context.startForegroundService(
                Intent(context, FloatingTimerService::class.java)
            )
        } else {
            context.startActivity(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // Animated gradient background.
        AnimatedBackground()

        // Wallpaper overlay.
        wallpaperBitmap?.let { bmp ->
            androidx.compose.foundation.Image(
                bitmap = bmp,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().alpha(wallpaperOpacity)
            )
        }

        if (inPip) {
            PipContent(snapshot, sleepH, sleepM, wakeH, wakeM)
        } else {
            FullContent(
                snapshot = snapshot,
                nowLabel = nowLabel,
                sleepH = sleepH, sleepM = sleepM, wakeH = wakeH, wakeM = wakeM,
                onEnterPip = onEnterPip,
                onFloatOverlay = startOverlay,
                onOpenSettings = { showSettings = true },
            )
        }

        if (showSettings) {
            SettingsSheet(
                themeMode = themeMode,
                onThemeChange = onThemeChange,
                wallpaperOpacity = wallpaperOpacity,
                onOpacityChange = { wallpaperOpacity = it; prefs.wallpaperOpacity = it },
                onPickWallpaper = { wallpaperPicker.launch(arrayOf("image/*")) },
                onClearWallpaper = {
                    wallpaperUri = null; prefs.wallpaperUri = null
                },
                sleepH = sleepH, sleepM = sleepM, wakeH = wakeH, wakeM = wakeM,
                onSleepTime = { h, m ->
                    sleepH = h; sleepM = m; prefs.sleepHour = h; prefs.sleepMinute = m
                    SleepAlarmScheduler.schedule(context)
                },
                onWakeTime = { h, m ->
                    wakeH = h; wakeM = m; prefs.wakeHour = h; prefs.wakeMinute = m
                },
                onDismiss = { showSettings = false },
            )
        }
    }
}

/* ───────────────────────── Full UI ───────────────────────── */

@Composable
private fun FullContent(
    snapshot: TimerEngine.Snapshot,
    nowLabel: String,
    sleepH: Int, sleepM: Int, wakeH: Int, wakeM: Int,
    onEnterPip: () -> Unit,
    onFloatOverlay: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val secs = if (snapshot.active) snapshot.remaining else snapshot.untilStart
    val (h, m, s) = TimerEngine.hms(secs)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        // Header
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Bedtime, null, tint = Gold)
                Spacer(Modifier.width(8.dp))
                Text("Sleep Timer",
                    color = Gold, fontWeight = FontWeight.Bold, fontSize = 20.sp,
                    fontFamily = GoogleSans)
            }
            Row {
                GlassIconButton(Icons.Filled.PictureInPicture, onEnterPip)
                Spacer(Modifier.width(10.dp))
                GlassIconButton(Icons.Filled.Settings, onOpenSettings)
            }
        }

        // Status badge
        StatusBadge(snapshot.active, sleepH, sleepM)

        // Ring + countdown
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(300.dp)) {
            ProgressRing(progress = if (snapshot.active) snapshot.progress else 0f)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (snapshot.active) "TIME REMAINING" else "STARTS IN",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f),
                    fontSize = 12.sp, letterSpacing = 2.sp, fontFamily = GoogleSans
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "%02d:%02d:%02d".format(h, m, s),
                    fontSize = 52.sp, fontWeight = FontWeight.Bold,
                    fontFamily = GoogleSans,
                    style = androidx.compose.ui.text.TextStyle(
                        brush = Brush.linearGradient(listOf(BlueLight, Color.White, Gold))
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(nowLabel,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f),
                    fontSize = 14.sp, fontFamily = GoogleSans)
                Spacer(Modifier.height(6.dp))
                Text(
                    if (snapshot.active) "Wake up at ${fmt12(wakeH, wakeM)}"
                    else "Starts at ${fmt12(sleepH, sleepM)}",
                    color = Gold, fontSize = 12.sp, fontFamily = GoogleSans
                )
            }
        }

        // H / M / S cards
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SegCard("HOURS", h, Modifier.weight(1f))
            SegCard("MINUTES", m, Modifier.weight(1f))
            SegCard("SECONDS", s, Modifier.weight(1f))
        }

        // Linear progress
        LinearGold(progress = if (snapshot.active) snapshot.progress else 0f)

        // Buttons
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Overlay float — coexists with another app's Picture-in-Picture.
            FilledTonalButton(
                onClick = onFloatOverlay,
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = BlueMid)
            ) {
                Icon(Icons.Filled.OpenInNew, null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Float Window", color = Color.White, fontFamily = GoogleSans)
            }
            Button(
                onClick = onOpenSettings,
                colors = ButtonDefaults.buttonColors(containerColor = Gold)
            ) {
                Icon(Icons.Filled.Tune, null, tint = BlueDeep)
                Spacer(Modifier.width(8.dp))
                Text("Customize", color = BlueDeep, fontFamily = GoogleSans,
                    fontWeight = FontWeight.Medium)
            }
        }

        // Native PiP (single-window) as a secondary option.
        TextButton(onClick = onEnterPip) {
            Icon(Icons.Filled.PictureInPicture, null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f))
            Spacer(Modifier.width(6.dp))
            Text("Use system Picture-in-Picture",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f),
                fontSize = 12.sp, fontFamily = GoogleSans)
        }
    }
}

/* ───────────────────────── PiP UI ───────────────────────── */

@Composable
private fun PipContent(
    snapshot: TimerEngine.Snapshot,
    sleepH: Int, sleepM: Int, wakeH: Int, wakeM: Int,
) {
    val secs = if (snapshot.active) snapshot.remaining else snapshot.untilStart
    val (h, m, s) = TimerEngine.hms(secs)
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                if (snapshot.active) "SLEEP TIMER" else "STARTS IN",
                color = BlueLight, fontSize = 11.sp, letterSpacing = 2.sp,
                fontFamily = GoogleSans
            )
            Text(
                "%02d:%02d:%02d".format(h, m, s),
                fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.White,
                fontFamily = GoogleSans
            )
            Text(
                if (snapshot.active) "Wake at ${fmt12(wakeH, wakeM)}"
                else "Starts ${fmt12(sleepH, sleepM)}",
                color = Gold, fontSize = 11.sp, fontFamily = GoogleSans
            )
        }
    }
}

/* ───────────────────────── Components ───────────────────────── */

@Composable
private fun GlassIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = .6f))
            .border(1.dp, Gold.copy(alpha = .3f), RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun StatusBadge(active: Boolean, sleepH: Int, sleepM: Int) {
    Row(
        Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = .6f))
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = .12f), CircleShape)
            .padding(horizontal = 18.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val infinite = rememberInfiniteTransition(label = "dot")
        val a by infinite.animateFloat(
            1f, .4f,
            infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "dotAlpha"
        )
        Box(
            Modifier.size(8.dp).clip(CircleShape)
                .background((if (active) Color(0xFF4CAF50) else Gold).copy(alpha = a))
        )
        Spacer(Modifier.width(8.dp))
        Text(
            if (active) "Sleep timer active" else "Waiting • Starts ${fmt12(sleepH, sleepM)}",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f),
            fontSize = 13.sp, fontFamily = GoogleSans
        )
    }
}

@Composable
private fun ProgressRing(progress: Float) {
    val animated by animateFloatAsState(progress, tween(800), label = "ring")
    val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = .12f)
    Canvas(Modifier.size(300.dp)) {
        val stroke = 14f
        val diameter = size.minDimension - stroke
        val topLeft = Offset(
            (size.width - diameter) / 2f, (size.height - diameter) / 2f
        )
        val arcSize = Size(diameter, diameter)
        // track
        drawArc(trackColor, -90f, 360f, false, topLeft, arcSize,
            style = Stroke(stroke))
        // progress
        drawArc(
            Brush.sweepGradient(listOf(BlueVivid, BlueLight, Gold, BlueVivid)),
            -90f, 360f * animated, false, topLeft, arcSize,
            style = Stroke(stroke, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun SegCard(label: String, value: Int, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = .55f))
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = .12f),
                RoundedCornerShape(20.dp))
            .padding(vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("%02d".format(value), color = Gold, fontSize = 30.sp,
            fontWeight = FontWeight.Bold, fontFamily = GoogleSans)
        Text(label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .55f),
            fontSize = 11.sp, letterSpacing = 1.5.sp, fontFamily = GoogleSans)
    }
}

@Composable
private fun LinearGold(progress: Float) {
    val animated by animateFloatAsState(progress, tween(800), label = "bar")
    Box(
        Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = .5f))
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(animated.coerceIn(0f, 1f))
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(BlueVivid, Gold)))
        )
    }
}

@Composable
private fun AnimatedBackground() {
    val infinite = rememberInfiniteTransition(label = "bg")
    val t by infinite.animateFloat(
        0f, 1f, infiniteRepeatable(tween(12000, easing = LinearEasing), RepeatMode.Reverse),
        label = "bgT"
    )
    val dark = MaterialTheme.colorScheme.background
    Canvas(Modifier.fillMaxSize()) {
        drawRect(dark)
        drawCircle(
            Brush.radialGradient(
                listOf(Color(0xFF0D2870).copy(alpha = .8f), Color.Transparent),
                center = Offset(size.width * (0.2f + 0.1f * t), size.height * 0.3f),
                radius = size.minDimension * 0.6f
            ),
            radius = size.minDimension * 0.6f,
            center = Offset(size.width * (0.2f + 0.1f * t), size.height * 0.3f)
        )
        drawCircle(
            Brush.radialGradient(
                listOf(BlueMid.copy(alpha = .5f), Color.Transparent),
                center = Offset(size.width * (0.8f - 0.1f * t), size.height * 0.75f),
                radius = size.minDimension * 0.55f
            ),
            radius = size.minDimension * 0.55f,
            center = Offset(size.width * (0.8f - 0.1f * t), size.height * 0.75f)
        )
        drawCircle(
            Brush.radialGradient(
                listOf(Gold.copy(alpha = .12f), Color.Transparent),
                center = Offset(size.width * 0.5f, size.height * (0.15f + 0.05f * t)),
                radius = size.minDimension * 0.4f
            ),
            radius = size.minDimension * 0.4f,
            center = Offset(size.width * 0.5f, size.height * (0.15f + 0.05f * t))
        )
    }
}

/* ───────────────────────── Settings ───────────────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsSheet(
    themeMode: String,
    onThemeChange: (String) -> Unit,
    wallpaperOpacity: Float,
    onOpacityChange: (Float) -> Unit,
    onPickWallpaper: () -> Unit,
    onClearWallpaper: () -> Unit,
    sleepH: Int, sleepM: Int, wakeH: Int, wakeM: Int,
    onSleepTime: (Int, Int) -> Unit,
    onWakeTime: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    ModalBottomSheet(onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Text("Settings", color = Gold, fontWeight = FontWeight.Bold,
                fontSize = 20.sp, fontFamily = GoogleSans)

            // Theme
            Column {
                SettingLabel("Theme")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("system" to "System", "dark" to "Dark", "light" to "Light")
                        .forEach { (key, label) ->
                            FilterChip(
                                selected = themeMode == key,
                                onClick = { onThemeChange(key) },
                                label = { Text(label, fontFamily = GoogleSans) }
                            )
                        }
                }
            }

            // Wallpaper
            Column {
                SettingLabel("Wallpaper")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onPickWallpaper, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Image, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Choose image", fontFamily = GoogleSans)
                    }
                    OutlinedButton(onClick = onClearWallpaper) {
                        Text("Clear", fontFamily = GoogleSans)
                    }
                }
            }

            // Opacity
            Column {
                SettingLabel("Wallpaper opacity • ${(wallpaperOpacity * 100).toInt()}%")
                Slider(
                    value = wallpaperOpacity,
                    onValueChange = onOpacityChange,
                    colors = SliderDefaults.colors(
                        thumbColor = Gold, activeTrackColor = Gold
                    )
                )
            }

            // Sleep / wake times
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TimeField("Sleep at", sleepH, sleepM, Modifier.weight(1f)) {
                    TimePickerDialog(context, { _, hh, mm -> onSleepTime(hh, mm) },
                        sleepH, sleepM, false).show()
                }
                TimeField("Wake at", wakeH, wakeM, Modifier.weight(1f)) {
                    TimePickerDialog(context, { _, hh, mm -> onWakeTime(hh, mm) },
                        wakeH, wakeM, false).show()
                }
            }

            // Exact alarm permission shortcut
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                TextButton(onClick = {
                    context.startActivity(
                        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    )
                }) {
                    Text("Grant exact-alarm permission", color = BlueLight,
                        fontFamily = GoogleSans)
                }
            }

            // Overlay permission shortcut (needed for the coexisting float window)
            TextButton(onClick = {
                context.startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    )
                )
            }) {
                Text("Grant \"Display over other apps\" (for floating window)",
                    color = BlueLight, fontFamily = GoogleSans)
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BlueMid)
            ) { Text("Done", color = Color.White, fontFamily = GoogleSans) }
        }
    }
}

@Composable
private fun SettingLabel(text: String) {
    Text(text.uppercase(Locale.getDefault()),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f),
        fontSize = 12.sp, letterSpacing = 1.sp, fontFamily = GoogleSans,
        modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun TimeField(
    label: String, h: Int, m: Int, modifier: Modifier = Modifier, onClick: () -> Unit
) {
    Column(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = .6f))
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = .15f),
                RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f),
            fontSize = 11.sp, fontFamily = GoogleSans)
        Spacer(Modifier.height(4.dp))
        Text(fmt12(h, m), color = Gold, fontWeight = FontWeight.Bold,
            fontSize = 18.sp, fontFamily = GoogleSans)
    }
}

/* ───────────────────────── Helpers ───────────────────────── */

private fun fmt12(h: Int, m: Int): String {
    val ampm = if (h >= 12) "PM" else "AM"
    val hh = h % 12
    return "%d:%02d %s".format(if (hh == 0) 12 else hh, m, ampm)
}

private fun currentClock(): String {
    val c = Calendar.getInstance()
    val h = c.get(Calendar.HOUR_OF_DAY)
    val m = c.get(Calendar.MINUTE)
    val s = c.get(Calendar.SECOND)
    val ampm = if (h >= 12) "PM" else "AM"
    val hh = h % 12
    return "%d:%02d:%02d %s".format(if (hh == 0) 12 else hh, m, s, ampm)
}
