package com.example.spectrometer.spectrometer

import android.Manifest.permission.RECORD_AUDIO
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
//import javax.inject.Inject

//class MicChecker @Inject constructor(@ApplicationContext val context: Context) {
class MicChecker constructor(private val context: Context, sample_rate: Int) {
    private var audioRecord: AudioRecord? = null
    private var minSize = 0
    private val _samplerate = sample_rate

    fun startCheck() {
        minSize = AudioRecord.getMinBufferSize(_samplerate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        if (ActivityCompat.checkSelfPermission(context, RECORD_AUDIO) != PERMISSION_GRANTED) {
            //Timber.d("==> record audio permission not granted")
            println("==> record audio permission not granted")
            return
        }
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            8000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minSize
        )
        audioRecord?.startRecording()
    }

//    fun stopCheck() {
//        audioRecord?.stop()
//    }

    fun getSamples() : ShortArray {
        val buffer = ShortArray(minSize)
        audioRecord?.read(buffer, 0, minSize)
        return buffer
    }
}