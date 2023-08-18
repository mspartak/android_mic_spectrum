package com.example.greetingcard.mic


import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


@Composable
fun SpectrogramMain(AppContext: Context) {

//    val spectrogramVM: SpectrogramViewModel = viewModel()
    val spectrogramVM = SpectrogramViewModel(AppContext)

    Spectrogram_Statefull(
        vm = spectrogramVM,
        onStartThread = {spectrogramVM.startThread()}
    )
}

@Composable
fun Spectrogram_Statefull(
    vm: SpectrogramViewModel,
    onStartThread: () -> Unit) {

    val counter = vm.counter.observeAsState()
    val started = vm.started.observeAsState()

    Spectrogram_Stateless(
        started = started,
        onStartThread = onStartThread,
        counter = counter,
        data_samples = vm.data_samples,
        data_samples_xlim = vm.data_samples_xlim,
        data_fft = vm.data_fft,
        data_fft_xlim = vm.data_fft_xlim
    )
}

@Composable
fun Spectrogram_Stateless(
    started: State<Boolean?>,
    onStartThread: () -> Unit,
    counter: State<Int?>,
    data_samples: MutableList<Int>,
    data_samples_xlim: MutableList<Float>,
    data_fft: MutableList<Int>,
    data_fft_xlim: MutableList<Float>) {

    val cfg_time_chart = ChartConfig()
    cfg_time_chart.min_x = data_samples_xlim[0]
    cfg_time_chart.max_x = data_samples_xlim[1]
    cfg_time_chart.color_bars = Color.Blue
    cfg_time_chart.name = "time, Samples"

    val cfg_fft_chart = ChartConfig()
    cfg_fft_chart.min_x = data_fft_xlim[0]
    cfg_fft_chart.max_x = data_fft_xlim[1]
    cfg_fft_chart.color_background= Color.Green
    cfg_fft_chart.color_bars = Color.Red
    cfg_fft_chart.zero_offset_enabled = false
    cfg_fft_chart.name = "frequency, Hz"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.LightGray
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
               verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = counter.value.toString())

            Button(
                onClick = onStartThread,
                enabled = !started.value!!
            ) {
                Text("Start thread")
            }
                // Plot samples in time domain
                Chart(cfg_time_chart, data = data_samples)
                // Plot FFT
                Chart(cfg_fft_chart, data = data_fft)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val _started = MutableLiveData(false)
    var started: LiveData<Boolean> = _started
    val _cnt = MutableLiveData(77)
    var cnt: LiveData<Int> = _cnt
    val list = mutableListOf(10, 20, 30, 40, 50, 90, 190, 30, -90, -40, -50, 30, 50, 20, 30)
    Spectrogram_Stateless(
        started = started.observeAsState(),
        onStartThread = {},
        counter = cnt.observeAsState(),
        data_samples = list,
        data_samples_xlim = mutableListOf(0f,10f),
        data_fft = list,
        data_fft_xlim = mutableListOf(22f,55f))
}