package com.twinklingtreasure.timer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Nightlight
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twinklingtreasure.timer.data.AppSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: AppSettings,
    onBack: () -> Unit,
    onPickSystemRingtone: () -> Unit,
    onPickCustomAudio: () -> Unit,
    onTestAlarm: () -> Unit,
    onClearAlarm: () -> Unit,
    onSetVibrate: (Boolean) -> Unit,
    onSetForceDark: (Boolean) -> Unit,
    onPickWallpaper: () -> Unit,
    onRemoveWallpaper: () -> Unit,
    onSetWallpaperOpacity: (Float) -> Unit,
    onSetPhaseMinutes: (phaseIndex: Int, minutes: Int) -> Unit,
    onSetAutoStartEnabled: (Boolean) -> Unit,
    onSetAutoStartTime: (hour: Int, minute: Int) -> Unit,
) {
    var showTimePicker by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            // ── Timer Durations ───────────────────────────────────
            SettingsSection(title = "Timer Durations") {
                Text(
                    "Changes take effect when each phase next starts.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
                val phases = listOf(
                    Triple("📚", "Upskilling",  settings.upskillingMinutes),
                    Triple("😌", "Eye Rest 1",  settings.eyeRest1Minutes),
                    Triple("⚡", "Work",         settings.workMinutes),
                    Triple("😌", "Eye Rest 2",  settings.eyeRest2Minutes),
                )
                phases.forEachIndexed { index, (emoji, name, minutes) ->
                    if (index > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    PhaseDurationRow(
                        emoji      = emoji,
                        name       = name,
                        minutes    = minutes,
                        onDecrement = { onSetPhaseMinutes(index, (minutes - 5).coerceAtLeast(5)) },
                        onIncrement = { onSetPhaseMinutes(index, (minutes + 5).coerceAtMost(180)) },
                    )
                }
            }

            // ── Daily Auto-Start ──────────────────────────────────
            SettingsSection(title = "Daily Auto-Start") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        Icons.Rounded.Schedule,
                        contentDescription = null,
                        tint     = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Start automatically each day",
                        style    = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    Switch(checked = settings.autoStartEnabled, onCheckedChange = onSetAutoStartEnabled)
                }

                if (settings.autoStartEnabled) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "At this time the cycle begins on its own as the floating pill — no Picture-in-Picture, the app stays closed.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(
                        onClick  = { showTimePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Rounded.Schedule, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Start time:  ${formatHourMinute(settings.autoStartHour, settings.autoStartMinute)}")
                    }
                }
            }

            // ── Alarm Sound ───────────────────────────────────────
            SettingsSection(title = "Alarm Sound") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        Icons.Rounded.Notifications,
                        contentDescription = null,
                        tint     = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text     = if (settings.alarmSoundUri.isEmpty()) "No alarm sound"
                                   else settings.alarmSoundName,
                        style    = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    if (settings.alarmSoundUri.isNotEmpty()) {
                        IconButton(onClick = onTestAlarm, modifier = Modifier.size(36.dp)) {
                            Icon(
                                Icons.Rounded.PlayArrow,
                                contentDescription = "Test alarm",
                                tint = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(onClick = onPickSystemRingtone, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Rounded.MusicNote, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Choose system ringtone")
                }
                OutlinedButton(onClick = onPickCustomAudio, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Rounded.LibraryMusic, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Choose custom MP3 from storage")
                }
                if (settings.alarmSoundUri.isNotEmpty()) {
                    TextButton(onClick = onClearAlarm, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Rounded.VolumeOff, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Remove alarm sound", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            // ── Vibration ─────────────────────────────────────────
            SettingsSection(title = "Vibration") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        Icons.Rounded.Vibration,
                        contentDescription = null,
                        tint     = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Vibrate when timer ends",
                        style    = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    Switch(checked = settings.vibrateEnabled, onCheckedChange = onSetVibrate)
                }
            }

            // ── Appearance ────────────────────────────────────────
            SettingsSection(title = "Appearance") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        Icons.Rounded.Nightlight,
                        contentDescription = null,
                        tint     = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Always dark mode",
                        style    = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    Switch(checked = settings.forceDarkMode, onCheckedChange = onSetForceDark)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Text(
                    "Background Wallpaper",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.Wallpaper,
                        contentDescription = null,
                        tint     = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text  = if (settings.wallpaperUri.isEmpty()) "No wallpaper set"
                                else "Wallpaper active",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(onClick = onPickWallpaper, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Rounded.PhotoLibrary, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Choose from gallery")
                }

                if (settings.wallpaperUri.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Wallpaper visibility",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Dark",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Slider(
                            value         = settings.wallpaperOpacity,
                            onValueChange = onSetWallpaperOpacity,
                            valueRange    = 0.05f..1f,
                            modifier      = Modifier.weight(1f).padding(horizontal = 8.dp),
                        )
                        Text(
                            "Bright",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    TextButton(onClick = onRemoveWallpaper, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Rounded.Delete, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Remove wallpaper", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        if (showTimePicker) {
            TimePickerDialog(
                initialHour   = settings.autoStartHour,
                initialMinute = settings.autoStartMinute,
                onConfirm     = { h, m -> onSetAutoStartTime(h, m); showTimePicker = false },
                onDismiss     = { showTimePicker = false },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = rememberTimePickerState(
        initialHour   = initialHour,
        initialMinute = initialMinute,
        is24Hour      = false,
    )
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape          = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            color          = MaterialTheme.colorScheme.surface,
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Auto-start time",
                    style    = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                TimePicker(state = state)
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { onConfirm(state.hour, state.minute) }) { Text("Set") }
                }
            }
        }
    }
}

/** Formats a 24-hour time as a friendly 12-hour string, e.g. 6:10 → "6:10 AM". */
private fun formatHourMinute(hour: Int, minute: Int): String {
    val period = if (hour < 12) "AM" else "PM"
    val h12 = when {
        hour == 0  -> 12
        hour > 12  -> hour - 12
        else       -> hour
    }
    return "%d:%02d %s".format(h12, minute, period)
}

@Composable
private fun PhaseDurationRow(
    emoji: String,
    name: String,
    minutes: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = emoji, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.width(10.dp))
        Text(
            text     = name,
            style    = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        FilledIconButton(
            onClick  = onDecrement,
            enabled  = minutes > 5,
            modifier = Modifier.size(36.dp),
            colors   = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor   = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        ) {
            Icon(Icons.Rounded.Remove, contentDescription = "Decrease", modifier = Modifier.size(18.dp))
        }
        Text(
            text      = "$minutes min",
            style     = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color     = MaterialTheme.colorScheme.secondary,
            modifier  = Modifier.width(62.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        FilledIconButton(
            onClick  = onIncrement,
            enabled  = minutes < 180,
            modifier = Modifier.size(36.dp),
            colors   = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor   = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Increase", modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text     = title.uppercase(),
            style    = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.5.sp,
                fontWeight    = FontWeight.Bold,
            ),
            color    = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp),
        )
        Surface(
            shape    = MaterialTheme.shapes.medium,
            color    = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp), content = content)
        }
        Spacer(Modifier.height(16.dp))
    }
}
