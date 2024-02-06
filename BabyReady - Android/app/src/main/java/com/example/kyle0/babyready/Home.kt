package com.example.kyle0.babyready

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
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
        Column {
            Text(text = textToShow)
            Text(text = viewModel.prediction)
        }

        Column {
            val interactionSource = remember { MutableInteractionSource() }
            var showReleaseTip : Boolean by remember { mutableStateOf(false) }
            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collect {
                    when (it) {
                        is PressInteraction.Press -> {
                            if (recorderPermissionState.status.isGranted) {
                                showReleaseTip = true
                                viewModel.startRecording()
                            } else {
                                recorderPermissionState.launchPermissionRequest()
                            }
                        }

                        is PressInteraction.Release -> {
                            viewModel.stopRecording()
                            showReleaseTip = false
                        }

                        is PressInteraction.Cancel -> {
                            viewModel.stopRecording()
                            viewModel.predict()
                            showReleaseTip = false
                        }
                    }
                }
            }

            if (showReleaseTip){
                Text(text = "Release to predict")
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                interactionSource = interactionSource,
                onClick = {
                    viewModel.stopRecording()
                },
                shape = SemiOvalShape()
            ) {
                Text(text = "Press to predict")
            }

        }
    }
}