package com.example.kyle0.babyready

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File

sealed interface PingStatus {
    data object IDLE : PingStatus
    data object PINGING : PingStatus
    data object SUCCESS : PingStatus
    data object FAILED : PingStatus
}


class HomeViewModel(
    private val repository: PredictionRepository = PredictionRepository(),
    private val recorder: Recorder = Recorder(),
    private val audioPath: String = BabyApp.context.cacheDir.absolutePath + "baby-cry.3gp"
) : ViewModel() {

    var pingStatus by mutableStateOf<PingStatus>(PingStatus.IDLE)
    var prediction by mutableStateOf("")

    fun ping() {
        viewModelScope.launch {
            pingStatus = PingStatus.PINGING
            val result = repository.ping()
            pingStatus = if (result) PingStatus.SUCCESS else PingStatus.FAILED
        }
    }

    fun startRecording() {
        recorder.startRecording(audioPath)
    }

    fun stopRecording() {
        recorder.stopRecording()

        viewModelScope.launch {
            val file = File(audioPath)
            val result = repository.predict(file)

            prediction = result.ifEmpty {
                "fail prediction"
            }
        }


    }

}