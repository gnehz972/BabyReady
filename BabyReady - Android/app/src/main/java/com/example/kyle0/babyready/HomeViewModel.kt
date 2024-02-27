package com.example.kyle0.babyready

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

sealed interface PingStatus {
    data object IDLE : PingStatus
    data object PINGING : PingStatus
    data object SUCCESS : PingStatus
    data object FAILED : PingStatus
}


sealed interface RecordingTime {
    data object SHORT : RecordingTime
    data object ENOUGH : RecordingTime
    data object MAX : RecordingTime
}

class HomeViewModel(
    private val repository: PredictionRepository = PredictionRepository(),
    private val recorder: Recorder = Recorder(),
    private val audioPath: String = BabyApp.context.cacheDir.absolutePath + "baby-cry.3gp"
) : ViewModel() {

    var pingStatus by mutableStateOf<PingStatus>(PingStatus.IDLE)
    var prediction by mutableStateOf("")
    var predicting by mutableStateOf(false)
    var amplitude by mutableIntStateOf(0)
    var recordingStarted by mutableStateOf(false)
    var recordingTime by mutableStateOf<RecordingTime>(RecordingTime.SHORT)
    private var peekAmplitudeJob: Job? = null

    fun ping() {
        viewModelScope.launch {
            pingStatus = PingStatus.PINGING
            val result = repository.ping()
            pingStatus = if (result) PingStatus.SUCCESS else PingStatus.FAILED
        }
    }

    fun startRecording() {
        recorder.startRecording(audioPath)
        recordingStarted = true
    }

    fun monitorRecording() {
        peekAmplitudeJob = viewModelScope.launch {
            var count = 0
            val countInterval = 300L
            val enoughCount = 10
            val maxCount = 30
            recordingTime = RecordingTime.SHORT
            while (recordingStarted && count < maxCount) {
                amplitude = recorder.peekAmplitude()
                count++
                delay(countInterval)
                if (count == enoughCount) {
                    recordingTime = RecordingTime.ENOUGH
                }
                if (count == maxCount) {
                    recordingTime = RecordingTime.MAX
                }
            }

            if (recordingStarted
                && recordingTime == RecordingTime.MAX
            ) {
                stopRecording()
            }
        }
    }

    fun stopRecording() {
        recorder.stopRecording()
        recordingStarted = false
        peekAmplitudeJob?.cancel()
    }

    fun reset() {
        recordingTime = RecordingTime.SHORT
    }

    fun predict() {
        viewModelScope.launch {
            predicting = true
            delay(1000)
            val file = File(audioPath)
            val result = repository.predict(file)

            prediction = result.ifEmpty {
                "fail prediction"
            }
            predicting = false
        }
    }

    fun clearPrediction() {
        prediction = ""
    }

}