package com.example.kyle0.babyready

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    Column {
        Row {
            Button(onClick = {
                viewModel.ping()
            }) {
                Text(text = "Ping")
            }
            when (viewModel.pingStatus) {
                PingStatus.PINGING -> CircularProgressIndicator()
                PingStatus.SUCCESS -> Icon(imageVector = Icons.Filled.Done, contentDescription = "Done")
                PingStatus.FAILED -> Icon(imageVector = Icons.Filled.Error, contentDescription = "Error")
                PingStatus.IDLE -> Unit
            }
        }
        val recorderPermissionState =
            rememberPermissionState(permission = android.Manifest.permission.RECORD_AUDIO)
        val textToShow = if (recorderPermissionState.status.isGranted) {
            "Permission granted"
        } else if (recorderPermissionState.status.shouldShowRationale) {
            "The RECORD_AUDIO is important for this app. Please grant the permission."
        } else {
            "RECORD_AUDIO not available"
        }
        Text(text = textToShow)
        Button(onClick = {
            if (recorderPermissionState.status.isGranted) {
                viewModel.startRecording()
            } else {
                recorderPermissionState.launchPermissionRequest()
            }

        }) {
            Text(text = "Start Record")
        }
        Button(onClick = {
            viewModel.stopRecording()
        }) {
            Text(text = "Stop Record")
        }

        Text(text = viewModel.prediction)
    }
}