package com.example.kyle0.babyready

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Home(
    viewModel: HomeViewModel = viewModel()
) {
    Column(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        val recorderPermissionState =
            rememberPermissionState(permission = android.Manifest.permission.RECORD_AUDIO)
        val textToShow = if (recorderPermissionState.status.isGranted) {
            "RECORD_AUDIO Permission granted"
        } else if (recorderPermissionState.status.shouldShowRationale) {
            "The RECORD_AUDIO permission is important for this app. Please grant the permission."
        } else {
            "RECORD_AUDIO permission is not available, pls check in APP SETTING"
        }
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (recorderPermissionState.status.isGranted.not()) {
                Text(text = textToShow, color = Color.White)
            }
            Text(
                text = "Guess why baby cries", color = Color.White,
                modifier = Modifier
                    .padding(10.dp)
            )
            Text(
                text = viewModel.prediction,
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(10.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            var showReleaseTip: Boolean by remember { mutableStateOf(false) }
            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collect {
                    when (it) {
                        is PressInteraction.Press -> {
                            viewModel.clearPrediction()
                            if (recorderPermissionState.status.isGranted) {
                                showReleaseTip = true
                                viewModel.startRecording()
                                viewModel.monitorRecording()
                            } else {
                                recorderPermissionState.launchPermissionRequest()
                            }
                        }

                        is PressInteraction.Release -> {
                            viewModel.stopRecording()
                            if (viewModel.recordingTime != RecordingTime.SHORT) {
                                viewModel.predict()
                            }
                            viewModel.reset()
                            showReleaseTip = false
                        }

                        is PressInteraction.Cancel -> {
                            viewModel.stopRecording()
                            viewModel.reset()
                            showReleaseTip = false
                        }
                    }
                }
            }

            if (viewModel.recordingStarted) {
                WaveForm(
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(100.dp),
                    amplitude = viewModel.amplitude
                )
            }

            val tip = when (viewModel.recordingTime) {
                is RecordingTime.SHORT -> "hold for at least 3 seconds"
                is RecordingTime.ENOUGH -> "Enough, release to predict"
                is RecordingTime.MAX -> "Max recording, release to predict"
            }

            if (showReleaseTip) {
                Text(
                    text = tip,
                    color = Color.LightGray,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                )
            }

            if (viewModel.predicting) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(20.dp)
                )
            }

            val bgColor = when (viewModel.recordingTime) {
                is RecordingTime.SHORT -> MaterialTheme.colorScheme.primary
                is RecordingTime.ENOUGH -> Color(0xFF568231)
                is RecordingTime.MAX -> Color(0xFF335C0E)
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = bgColor,
                ),
                interactionSource = interactionSource,
                onClick = {},
                shape = SemiOvalShape()
            ) {
                Text(text = "Press to record baby cry")
            }

        }
    }
}