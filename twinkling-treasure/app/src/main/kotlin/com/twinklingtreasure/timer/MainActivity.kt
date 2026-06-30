package com.twinklingtreasure.timer

import android.Manifest
import android.app.AlarmManager
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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

    private val wallpaperLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            viewModel.setWallpaper(it.toString())
        }
    }

    private var enteringPip = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()
        ensureExactAlarmPermission()
        ensureOverlayPermission()
        startTimerService()
        viewModel.bindService(this)
        // The daily auto-start alarm is (re)armed by the ViewModel as settings load/change.

        setContent {
            val uiState          by viewModel.uiState.collectAsStateWithLifecycle()
            val isInPip          by viewModel.isInPipMode.collectAsStateWithLifecycle()
            val progressFraction by viewModel.progressFraction.collectAsStateWithLifecycle()
            val settings         by viewModel.settings.collectAsStateWithLifecycle()
            val availableVoices  by viewModel.availableVoices.collectAsStateWithLifecycle()

            var showSettings by remember { mutableStateOf(false) }

            val isDark = settings.forceDarkMode || isSystemInDarkTheme() || settings.wallpaperUri.isNotEmpty()

            TwinklingTreasureTheme(darkTheme = isDark) {
                if (showSettings && !isInPip) {
                    SettingsScreen(
                        settings               = settings,
                        availableVoices        = availableVoices,
                        onBack                 = { showSettings = false },
                        onSetVibrate           = viewModel::setVibrate,
                        onSetForceDark         = viewModel::setForceDark,
                        onPickWallpaper        = { wallpaperLauncher.launch(arrayOf("image/*")) },
                        onRemoveWallpaper      = { viewModel.setWallpaper("") },
                        onSetWallpaperOpacity  = viewModel::setWallpaperOpacity,
                        onSetPhaseMinutes      = viewModel::setPhaseMinutes,
                        onSetPhaseReminderText = viewModel::setPhaseReminderText,
                        onSetTtsVoice          = viewModel::setTtsVoice,
                        onSetTtsLanguage       = viewModel::setTtsLanguage,
                        onSetTtsPace           = viewModel::setTtsPace,
                        onPreviewText          = viewModel::previewText,
                        onSetAutoStartEnabled  = viewModel::setAutoStartEnabled,
                        onSetAutoStartTime     = viewModel::setAutoStartTime,
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        if (!isInPictureInPictureMode) viewModel.setInPipMode(false)
        viewModel.setAppForeground(true)
    }

    override fun onPause() {
        super.onPause()
        if (!enteringPip) viewModel.setAppForeground(false)
        enteringPip = false
    }

    private fun ensureExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!am.canScheduleExactAlarms()) {
                try {
                    startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                } catch (_: Exception) { /* some OEMs lack this screen */ }
            }
        }
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
            enteringPip = true
            val params = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(1, 1))
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        setAutoEnterEnabled(false)
                        setSeamlessResizeEnabled(false)
                    }
                }
                .build()
            enterPictureInPictureMode(params)
        }
    }

    private fun ensureOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            try {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")))
            } catch (_: Exception) {}
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
