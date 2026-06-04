package com.twinklingtreasure.timer

import android.Manifest
import android.app.PictureInPictureParams
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twinklingtreasure.timer.service.TimerService
import com.twinklingtreasure.timer.ui.MainScreen
import com.twinklingtreasure.timer.ui.theme.TwinklingTreasureTheme
import com.twinklingtreasure.timer.viewmodel.TimerViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TimerViewModel by viewModels()

    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

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

            TwinklingTreasureTheme {
                MainScreen(
                    uiState          = uiState,
                    isInPipMode      = isInPip,
                    progressFraction = progressFraction,
                    onStart          = viewModel::startTimer,
                    onPause          = viewModel::pauseTimer,
                    onSkip           = viewModel::skipPhase,
                    onReset          = viewModel::resetTimer,
                    onEnterPip       = ::enterPipMode,
                )
            }
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
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
