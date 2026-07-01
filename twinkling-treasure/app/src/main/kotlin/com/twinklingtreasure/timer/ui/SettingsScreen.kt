package com.twinklingtreasure.timer.ui

import android.speech.tts.Voice
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Nightlight
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twinklingtreasure.timer.data.AppSettings
import com.twinklingtreasure.timer.util.VoiceNames
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: AppSettings,
    availableVoices: List<Voice>,
    onBack: () -> Unit,
    onSetVibrate: (Boolean) -> Unit,
    onSetForceDark: (Boolean) -> Unit,
    onPickWallpaper: () -> Unit,
    onRemoveWallpaper: () -> Unit,
    onSetWallpaperOpacity: (Float) -> Unit,
    onSetPhaseMinutes: (phaseIndex: Int, minutes: Int) -> Unit,
    onSetPhaseReminderText: (phaseIndex: Int, text: String) -> Unit,
    onSetTtsVoice: (voiceName: String) -> Unit,
    onSetTtsLanguage: (languageTag: String) -> Unit,
    onSetTtsPace: (pace: Float) -> Unit,
    onPreviewText: (text: String) -> Unit,
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

            // ── Phase Reminders ───────────────────────────────────
            SettingsSection(title = "Phase Reminders") {
                Text(
                    "Spoken aloud when each phase ends.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
                val reminderPhases = listOf(
                    Triple("📚", "Upskilling",  settings.upskillingReminder),
                    Triple("😌", "Eye Rest 1",  settings.eyeRest1Reminder),
                    Triple("⚡", "Work",         settings.workReminder),
                    Triple("😌", "Eye Rest 2",  settings.eyeRest2Reminder),
                )
                reminderPhases.forEachIndexed { index, (emoji, name, text) ->
                    if (index > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    PhaseReminderRow(
                        emoji     = emoji,
                        name      = name,
                        text      = text,
                        onSave    = { newText -> onSetPhaseReminderText(index, newText) },
                        onPreview = { draftText -> onPreviewText(draftText) },
                    )
                }
            }

            // ── Reminder Voice ────────────────────────────────────
            SettingsSection(title = "Reminder Voice") {
                if (availableVoices.isEmpty()) {
                    Text(
                        "Loading voices…",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    var showLanguageDialog by remember { mutableStateOf(false) }
                    val currentLocale = if (settings.ttsLanguageTag.isNotEmpty())
                        Locale.forLanguageTag(settings.ttsLanguageTag) else Locale.getDefault()
                    val availableLocales = remember(availableVoices) {
                        availableVoices.map { it.locale }.distinctBy { it.toLanguageTag() }
                            .sortedBy { it.getDisplayName(Locale.getDefault()) }
                    }
                    // Filter the carousel to the selected language; fall back to primary-language
                    // match, then to all voices, so the carousel is never left empty.
                    val filteredVoices = remember(availableVoices, currentLocale) {
                        val exact = availableVoices.filter {
                            it.locale.toLanguageTag() == currentLocale.toLanguageTag()
                        }
                        exact.ifEmpty {
                            availableVoices.filter { it.locale.language == currentLocale.language }
                                .ifEmpty { availableVoices }
                        }
                    }

                    // Keyed on language so the pager fully resets (no stale out-of-bounds page)
                    // when switching to a language with fewer voices.
                    key(currentLocale.toLanguageTag()) {
                        VoiceCarousel(
                            voices            = filteredVoices,
                            selectedVoiceName = settings.ttsVoiceName,
                            onSelectVoice     = onSetTtsVoice,
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            Icons.Rounded.Language,
                            contentDescription = null,
                            tint     = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Language",
                            style    = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedButton(onClick = { showLanguageDialog = true }) {
                            Text(currentLocale.getDisplayName(Locale.getDefault()))
                        }
                    }

                    if (showLanguageDialog) {
                        LanguagePickerDialog(
                            locales        = availableLocales,
                            selectedLocale = currentLocale,
                            onSelect       = { tag -> onSetTtsLanguage(tag); showLanguageDialog = false },
                            onDismiss      = { showLanguageDialog = false },
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            Icons.Rounded.Speed,
                            contentDescription = null,
                            tint     = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Pace",
                            style    = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            "%.1fx".format(settings.ttsPace),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Slow",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Slider(
                            value         = settings.ttsPace,
                            onValueChange = onSetTtsPace,
                            valueRange    = 0.5f..2.0f,
                            modifier      = Modifier.weight(1f).padding(horizontal = 8.dp),
                        )
                        Text(
                            "Fast",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick  = { onPreviewText("This is how your reminders will sound.") },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Rounded.GraphicEq, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Preview voice")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguagePickerDialog(
    locales: List<Locale>,
    selectedLocale: Locale,
    onSelect: (languageTag: String) -> Unit,
    onDismiss: () -> Unit,
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape          = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            color          = MaterialTheme.colorScheme.surface,
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Choose language",
                    style    = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    locales.forEach { locale ->
                        val isSelected = locale.toLanguageTag() == selectedLocale.toLanguageTag()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(locale.toLanguageTag()) }
                                .padding(vertical = 12.dp),
                        ) {
                            Text(
                                text     = locale.getDisplayName(Locale.getDefault()),
                                style    = MaterialTheme.typography.bodyLarge,
                                color    = if (isSelected) MaterialTheme.colorScheme.secondary
                                           else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f),
                            )
                            if (isSelected) {
                                Icon(
                                    Icons.Rounded.RecordVoiceOver,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                }
            }
        }
    }
}

@Composable
private fun VoiceCarousel(
    voices: List<Voice>,
    selectedVoiceName: String,
    onSelectVoice: (voiceName: String) -> Unit,
) {
    val initialPage = remember(voices, selectedVoiceName) {
        voices.indexOfFirst { it.name == selectedVoiceName }.coerceAtLeast(0)
    }
    val pagerState = rememberPagerState(initialPage = initialPage) { voices.size }

    LaunchedEffect(pagerState.currentPage, voices) {
        val voice = voices.getOrNull(pagerState.currentPage) ?: return@LaunchedEffect
        if (voice.name != selectedVoiceName) onSelectVoice(voice.name)
    }

    HorizontalPager(
        state          = pagerState,
        modifier       = Modifier.fillMaxWidth().height(140.dp),
        pageSpacing    = 12.dp,
        contentPadding = PaddingValues(horizontal = 32.dp),
    ) { page ->
        val voice = voices[page]
        VoiceCard(
            voice    = voice,
            selected = voice.name == selectedVoiceName,
        )
    }

    Spacer(Modifier.height(10.dp))

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        voices.forEachIndexed { index, _ ->
            val isCurrent = index == pagerState.currentPage
            Spacer(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(if (isCurrent) 8.dp else 6.dp)
                    .background(
                        color = if (isCurrent) MaterialTheme.colorScheme.secondary
                                else MaterialTheme.colorScheme.outline,
                        shape = CircleShape,
                    ),
            )
        }
    }
}

@Composable
private fun VoiceCard(voice: Voice, selected: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        shape    = MaterialTheme.shapes.large,
        colors   = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer
                             else MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 4.dp else 0.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                Icons.Rounded.RecordVoiceOver,
                contentDescription = null,
                tint     = if (selected) MaterialTheme.colorScheme.onSecondaryContainer
                           else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text  = VoiceNames.friendlyName(voice),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (selected) MaterialTheme.colorScheme.onSecondaryContainer
                        else MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text      = VoiceNames.subtitle(voice),
                style     = MaterialTheme.typography.bodySmall,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
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
            textAlign = TextAlign.Center,
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
private fun PhaseReminderRow(
    emoji: String,
    name: String,
    text: String,
    onSave: (String) -> Unit,
    onPreview: (String) -> Unit,
) {
    var draft by remember(text) { mutableStateOf(text) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.width(10.dp))
            Text(
                text     = name,
                style    = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                onClick  = { onPreview(draft) },
                modifier = Modifier.size(36.dp),
                enabled  = draft.isNotBlank(),
            ) {
                Icon(
                    Icons.Rounded.PlayArrow,
                    contentDescription = "Preview $name reminder",
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value         = draft,
            onValueChange = { draft = it },
            modifier      = Modifier.fillMaxWidth(),
            placeholder   = { Text("e.g. \"$name, let's go.\"") },
            singleLine    = false,
            maxLines      = 3,
            textStyle     = MaterialTheme.typography.bodyMedium,
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor          = MaterialTheme.colorScheme.secondary,
            ),
        )
        if (draft != text) {
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { draft = text }) { Text("Cancel") }
                Spacer(Modifier.width(4.dp))
                TextButton(onClick = { onSave(draft) }) { Text("Save") }
            }
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
