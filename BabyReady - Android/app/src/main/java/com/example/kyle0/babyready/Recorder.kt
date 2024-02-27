package com.example.kyle0.babyready

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.IOException

class Recorder(
    private val context: Context = BabyApp.context,
) {

    private var recorder: MediaRecorder? = null
    fun startRecording(path: String) {
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }
        recorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(path)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
                start()
            } catch (e: IOException) {
                Log.w("Recorder", "start failed")
            }
        }
    }

    fun peekAmplitude(): Int {
        return recorder?.maxAmplitude ?: 0
    }

    fun stopRecording() {
        recorder?.apply {
            try {
                stop()
                release()
            } catch (e: Exception) {
                Log.w("Recorder", "stop failed")
            }
        }
        recorder = null
    }
}