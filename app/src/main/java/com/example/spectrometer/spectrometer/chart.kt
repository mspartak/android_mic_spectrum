package com.example.greetingcard.mic


import android.text.Layout
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times

class ChartConfig {
    var chart_height: Dp = 250.dp
    var min_x: Float = 0f
    var max_x: Float = 10f
    var zero_offset_enabled: Boolean = true
    var color_background: Color = Color.Cyan
    var color_bars: Color = Color.Blue
    var name: String = "name"
}


@Composable
fun Chart(cfg: ChartConfig, data: MutableList<Int>)
{
    Surface {
        Column(modifier = Modifier.background(Color.Yellow)
                          .fillMaxWidth()) {
            ChartView(cfg, data)
            Row (modifier = Modifier.fillMaxWidth()) {
                Text(text = cfg.min_x.toString(),
                    fontSize = 20.sp)
                Text(text = cfg.name,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 30.dp))
                Text(modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    fontSize = 20.sp,
                    text = cfg.max_x.toString())
            }
        }

    }
}

@Composable
fun ChartView(cfg: ChartConfig, data: MutableList<Int>){

    val channels_count = data.size
    var bar_width: Float
    val zero_offset: Dp
    var bar_height: Dp
    val height_limit: Dp

    if (true == cfg.zero_offset_enabled) {
        zero_offset = (cfg.chart_height / 2)
        height_limit = (cfg.chart_height / 2)
    }
    else {
        zero_offset = cfg.chart_height
        height_limit = cfg.chart_height
    }

    Canvas(
        Modifier
            .height(cfg.chart_height)
            .fillMaxWidth()
            .background(color = cfg.color_background)){

        // Adapt bars width according to to Canvas size and channels
        bar_width = (size.width / channels_count)

        for (i in 0..channels_count-1) {

            // Limit bar heights to avoid exit out of Canvas range
            if (data[i].dp > height_limit) {
                bar_height = -height_limit
            }
            else if (data[i].dp < -height_limit) {
                bar_height = height_limit
            }
            else {
                bar_height = -data[i].dp
            }

            // Draw bars
            drawRect(
                color = cfg.color_bars,
                topLeft = Offset( (i * bar_width) , zero_offset.toPx()),
                size = Size((bar_width-1), bar_height.toPx())
            )
        }
    }  // Canvas
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    val list = mutableListOf(1000, 1,30,1,-30,1,1,1,20, 30, 40, 50, 190, -190, 30, 50, 20, 30)
    val cfg = ChartConfig()
    cfg.color_bars = Color.Red
    cfg.zero_offset_enabled = true
    Chart(cfg, list)
}