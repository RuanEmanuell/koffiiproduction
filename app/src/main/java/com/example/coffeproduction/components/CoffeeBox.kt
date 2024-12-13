package com.example.coffeproduction.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.coffeproduction.R
import com.example.coffeproduction.model.Coffee

@Composable
fun CoffeBox(item: Coffee, maxPriceCoffe: Long, maxAromaCoffe: Long, minAcidityCoffe: Long){
    Column(
        modifier = Modifier.clip(shape = RoundedCornerShape(10.dp)).padding(horizontal = 6.dp).width(225.dp).height(400.dp).shadow(1.dp)
    ){
        Box(
            modifier = Modifier.padding(10.dp).fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center
        ){
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.image)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "Café ${item.name}",
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center
            )
        }
        Column(modifier = Modifier.background(Color(0xFFF9F7F5)).fillMaxSize().padding(horizontal = 8.dp) ){
            Text(item.name, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))
            Text(item.description, fontSize = 12.sp)
            Text("R$ ${item.price}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                if(item.id == maxPriceCoffe){
                    Text("Mais caro", fontSize = 9.sp, textAlign = TextAlign.Center,  modifier = Modifier.background(
                        Color(0xFFD22B2B)
                    ).padding(3.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                }
                if(item.id == maxAromaCoffe){
                    Text("Mais aromático", fontSize = 9.sp, textAlign = TextAlign.Center, modifier = Modifier.background(
                        Color(0xFFF1807E)
                    ).padding(3.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                }
                if(item.id == minAcidityCoffe){
                    Text("Menos ácido", fontSize = 9.sp, textAlign = TextAlign.Center, modifier = Modifier.background(
                        Color(0xFFA8DAF5)
                    ).padding(3.dp))
                }
            }
        }
    }
}