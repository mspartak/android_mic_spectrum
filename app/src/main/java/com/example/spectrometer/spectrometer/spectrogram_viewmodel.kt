package com.example.greetingcard.mic

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.spectrometer.spectrometer.MicChecker
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.math.sqrt

class SpectUpdateThread(vm : SpectrogramViewModel) : Runnable {

    var loc_vm = vm

    override fun run()  {

        /* Make counter of updates (Basically needed to trigger recompose) */
        var cnt = loc_vm.counter.value
        cnt = cnt?.inc()
        loc_vm._counter.postValue(cnt)

        // half FFT width
        val half_fft_width = (loc_vm.SAMPLES_COUNT/2).toInt()

        // uglu attenuation coefficients
        val SAMPLES_ATT = 5
        val FFT_ATT = 10000

        var data_samples: ShortArray = loc_vm.MyMicChecker.getSamples()
        var y: DoubleArray = DoubleArray(loc_vm.SAMPLES_COUNT)
        var x: DoubleArray = DoubleArray(loc_vm.SAMPLES_COUNT)

        for (n in 0..loc_vm.SAMPLES_COUNT-1) {
            x[n] = data_samples[n].toDouble()
            loc_vm.data_samples[n] = (x[n]/SAMPLES_ATT).toInt()
        }

        /* Perform FFT transform for obtained samples */
        var loc_fft = loc_vm.MyFft.fft(x,y)

        /* Calculate power of spectrum */
        for (n in 0..half_fft_width-1) {
            loc_vm.data_fft[n] = sqrt(( x[n ].pow(2) + y[n].pow(2) )/FFT_ATT).toInt()
        }
    }
}

class SpectrogramViewModel(AppContext: Context) : ViewModel() {

    // ---  CONFIG ITEMS ----
    val SAMPLES_COUNT: Int = 128 // Samples to capture for  FFT
    val SAMPLE_RATE = 8000       // Microphone sample rate in Hz
    val PERIOD_OF_SPECTRUM_UPDATE: Long = 200 // Period of spectrum view update in ms
    // ---  CONFIG ITEMS (end) ----

    val _counter = MutableLiveData(0)
    val counter: LiveData<Int>  = _counter
    val _started = MutableLiveData(false)
    var started: LiveData<Boolean> = _started

    var MyMicChecker = MicChecker(AppContext, SAMPLE_RATE)
    var data_samples = MutableList<Int>(SAMPLES_COUNT) {0}
    val data_samples_xlim = mutableListOf(0f, SAMPLES_COUNT.toFloat())
    var data_fft = MutableList<Int>((SAMPLES_COUNT / 2).toInt()) { 0 }
    val data_fft_xlim = mutableListOf(0f, (SAMPLE_RATE / 2).toFloat() )

    // Create my FFT object
    val MyFft = FFT(SAMPLES_COUNT)

    init {
        // Start Microphone sampling
        MyMicChecker.startCheck()
    }

    fun startThread() {
        val exec = ScheduledThreadPoolExecutor(1)
        val period: Long = PERIOD_OF_SPECTRUM_UPDATE // the period between successive executions
        exec.scheduleAtFixedRate(SpectUpdateThread(this), 1000, period, TimeUnit.MILLISECONDS)
        _started.postValue(true)
    }
}


