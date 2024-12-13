package com.example.coffeproduction.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomSlider(label: String, value: Float, onValueChange: (Float) -> Unit){
    Column (modifier = Modifier.padding(vertical = 5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 20.sp, color = Color(0xFF006241))
        Slider(value = value, onValueChange = onValueChange, steps = 4, valueRange = 1F..5F,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF006241),
                activeTrackColor = Color(0xFF006241),
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ))
    }
}