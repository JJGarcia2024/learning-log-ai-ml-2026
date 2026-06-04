package com.twinklingtreasure.timer

import android.Manifest
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twinklingtreasure.timer.service.TimerService
import com.twinklingtreasure.timer.ui.MainScreen
import com.twinklingtreasure.timer.ui.SettingsScreen
import com.twinklingtreasure.timer.ui.theme.TwinklingTreasureTheme
import com.twinklingtreasure.timer.viewmodel.TimerViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TimerViewModel by viewModels()

    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

    private val ringtoneLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val picked: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            }
            if (picked != null) {
                val name = try {
                    RingtoneManager.getRingtone(this, picked)?.getTitle(this) ?: "Ringtone"
                } catch (_: Exception) { "Ringtone" }
                viewModel.setAlarmSound(picked.toString(), name)
            }
        }
    }

    private val audioLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            viewModel.setAlarmSound(it.toString(), getDisplayName(it))
        }
    }

    private val wallpaperLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            viewModel.setWallpaper(it.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()
        startTimerService()
        viewModel.bindService(this)

        setContent {
            val uiState          by viewModel.uiState.collectAsStateWithLifecycle()
            val isInPip          by viewModel.isInPipMode.collectAsStateWithLifecycle()
            val progressFraction by viewModel.progressFraction.collectAsStateWithLifecycle()
            val settings         by viewModel.settings.collectAsStateWithLifecycle()

            var showSettings by remember { mutableStateOf(false) }

            val isDark = settings.forceDarkMode || isSystemInDarkTheme()

            TwinklingTreasureTheme(darkTheme = isDark) {
                if (showSettings && !isInPip) {
                    SettingsScreen(
                        settings             = settings,
                        onBack               = { showSettings = false },
                        onPickSystemRingtone = { launchRingtonePicker() },
                        onPickCustomAudio    = { audioLauncher.launch(arrayOf("audio/*")) },
                        onTestAlarm          = viewModel::testAlarm,
                        onClearAlarm         = { viewModel.setAlarmSound("", "No alarm") },
                        onSetVibrate         = viewModel::setVibrate,
                        onSetForceDark       = viewModel::setForceDark,
                        onPickWallpaper      = { wallpaperLauncher.launch(arrayOf("image/*")) },
                        onRemoveWallpaper    = { viewModel.setWallpaper("") },
                        onSetWallpaperOpacity = viewModel::setWallpaperOpacity,
                    )
                } else {
                    MainScreen(
                        uiState          = uiState,
                        isInPipMode      = isInPip,
                        progressFraction = progressFraction,
                        settings         = settings,
                        isDark           = isDark,
                        onStart          = viewModel::startTimer,
                        onPause          = viewModel::pauseTimer,
                        onSkip           = viewModel::skipPhase,
                        onReset          = viewModel::resetTimer,
                        onEnterPip       = ::enterPipMode,
                        onOpenSettings   = { showSettings = true },
                    )
                }
            }
        }
    }

    private fun launchRingtonePicker() {
        val cur = viewModel.settings.value.alarmSoundUri
        val existing: Uri? = when {
            cur.isEmpty()    -> null
            cur == "default" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            else             -> try { Uri.parse(cur) } catch (_: Exception) { null }
        }
        ringtoneLauncher.launch(
            Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
                    RingtoneManager.TYPE_ALARM or RingtoneManager.TYPE_RINGTONE or RingtoneManager.TYPE_NOTIFICATION)
                putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Choose alarm sound")
                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                existing?.let { putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, it) }
            }
        )
    }

    private fun getDisplayName(uri: Uri): String {
        val cursor = contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        return cursor?.use { if (it.moveToFirst()) it.getString(0) else null }
            ?: uri.lastPathSegment
            ?: "Custom audio"
    }

    override fun onResume() {
        super.onResume()
        viewModel.setInPipMode(false)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (viewModel.uiState.value.isRunning) enterPipMode()
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration,
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        viewModel.setInPipMode(isInPictureInPictureMode)
    }

    override fun onDestroy() {
        viewModel.unbindService(this)
        super.onDestroy()
    }

    private fun enterPipMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val params = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 7))
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        setAutoEnterEnabled(true)
                        setSeamlessResizeEnabled(false)
                    }
                }
                .build()
            enterPictureInPictureMode(params)
        }
    }

    private fun startTimerService() {
        val intent = Intent(this, TimerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent)
        else startService(intent)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
