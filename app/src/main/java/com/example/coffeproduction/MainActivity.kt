package com.example.coffeproduction

import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coffeproduction.ui.theme.CoffeProductionTheme
import androidx.compose.material3.Icon
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.coffeproduction.model.Coffe

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var text by remember { mutableStateOf("") }
            var coffe1 = Coffe(1, "Café Latte", "Café equilibrado com leite vaporizado e leve camada de espuma", 5, 1, 1, 5, 9.99, "https://cdn.starbuckschilledcoffee.com/4aee53/globalassets/evo/our-products/chilled-classics/chilled-cup/1_cc_caffelatte_r.png?width=480&height=600&rmode=max&format=webp")
            var coffe2 = Coffe(2, "Café Teste", "Café equilibrado com leite vaporizado e leve camada de espuma", 5, 1, 1, 5, 9.99, "https://cdn.starbuckschilledcoffee.com/4aee53/globalassets/evo/our-products/chilled-classics/chilled-cup/1_cc_caffelatte_r.png?width=480&height=600&rmode=max&format=webp")
            val originalCoffeList =  remember { mutableStateListOf(coffe1, coffe2) }
            var coffeList by remember { mutableStateOf(originalCoffeList.toList()) }
            CoffeProductionTheme {
                Scaffold(
                    topBar = {
                      TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF006241),
                            titleContentColor = Color.White
                        ),
                        title = {
                            Text("CoffeProduction");
                        }
                      )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {},
                            containerColor = Color(0xFF006241),
                            contentColor = Color.White
                            ) { Icon(
                                 Icons.Filled.Add,
                                 contentDescription = "Adicionar"
                        ) }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        Modifier.background(color = Color.White).padding(vertical = 20.dp, horizontal = 10.dp)
                    ) {
                        Column (
                            modifier = Modifier.fillMaxSize().padding(innerPadding),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SearchInput(
                                value = text,
                                onValueChange = {text = it; coffeList = originalCoffeList; coffeList = coffeList.filter { coffe -> coffe.name.contains(text) }})
                            LazyRow (modifier = Modifier.padding(vertical = 10.dp)) {
                               items(coffeList) {
                                   item -> CoffeBox(item)
                               }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchInput(value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = {Text ("Pesquisar um café...", color = Color(0xFF006241)) },
        modifier = Modifier.border(width = 2.dp, shape = RoundedCornerShape(4.dp), color = Color(0xFF006241)).fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun CoffeBox(item: Coffe){
    Column(
        modifier = Modifier.clip(shape = RoundedCornerShape(10.dp)).padding(horizontal = 6.dp).width(200.dp).height(400.dp).shadow(1.dp)
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
            Text(item.description, fontSize = 10.sp)
            Text(item.price.toString(), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}
