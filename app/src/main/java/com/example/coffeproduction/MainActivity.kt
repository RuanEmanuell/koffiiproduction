package com.example.coffeproduction

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import com.example.coffeproduction.ui.theme.CoffeProductionTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.coffeproduction.model.Coffe

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry = navController.currentBackStackEntryAsState().value
            val currentRoute = navBackStackEntry?.destination?.route

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
                        },
                          navigationIcon = {
                              if(currentRoute != "home"){
                                  IconButton(onClick = { navController.navigateUp() }) {
                                      Icon(
                                          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                          contentDescription = "Voltar",
                                          tint = Color.White
                                      )
                                  }
                              }
                          }
                      )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {},
                            containerColor = Color(0xFF006241),
                            contentColor = Color.White,
                            shape = CircleShape
                        ) { Icon(
                            Icons.Filled.Add,
                            contentDescription = "Adicionar"
                        ) }
                    },
                ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding).fillMaxWidth()){
                            AppNavHost(navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController){
    var text by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var orderingItem by remember { mutableStateOf("Mais relevante") }
    var orderingList = listOf(
        "Preço (menor para maior)",
        "Preço (maior para menor)",
        "Aroma (menor para maior)",
        "Aroma (maior para menor)",
        "Sabor (menor para maior)",
        "Sabor (maior para menor)",
        "Acidez (menor para maior)",
        "Acidez (maior para menor)",
        "Mais relevante"
    )


    var coffe1 = Coffe(1, "Café Latte", "Café equilibrado com leite vaporizado e leve camada de espuma", 5, 1, 1, 5, 9.99, "https://cdn.starbuckschilledcoffee.com/4aee53/globalassets/evo/our-products/chilled-classics/chilled-cup/1_cc_caffelatte_r.png?width=480&height=600&rmode=max&format=webp")
    var coffe2 = Coffe(2, "Café Teste", "Café equilibrado com leite vaporizado e leve camada de espuma", 3, 2, 4, 2, 8.99, "https://cdn.starbuckschilledcoffee.com/4aee53/globalassets/evo/our-products/chilled-classics/chilled-cup/1_cc_caffelatte_r.png?width=480&height=600&rmode=max&format=webp")
    val originalCoffeList =  remember { mutableStateListOf(coffe1, coffe2) }
    var coffeList by remember { mutableStateOf(originalCoffeList.toList()) }


    fun orderCoffeList(orderingIndex: Int){
        coffeList = originalCoffeList;
        coffeList = when(orderingIndex){
            0 ->  coffeList.sortedBy { it.price }
            1 ->  coffeList.sortedByDescending { it.price }
            2 ->  coffeList.sortedBy { it.aroma }
            3 ->  coffeList.sortedByDescending { it.aroma }
            4 ->  coffeList.sortedBy { it.flavor }
            5 ->  coffeList.sortedByDescending { it.flavor }
            6 ->  coffeList.sortedBy { it.acidity }
            7 ->  coffeList.sortedByDescending { it.acidity }
            else -> coffeList
        }
    }

    Box(
        Modifier.background(color = Color.White).padding(vertical = 20.dp, horizontal = 10.dp)
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchInput(
                value = text,
                onValueChange = {text = it; coffeList = originalCoffeList; coffeList = coffeList.filter { it.name.contains(text) }})
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd){
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false}
                ) {
                    orderingList.forEach { item ->
                        DropdownMenuItem(text = {Text(item)}, onClick = {expanded = false; orderingItem = item; orderCoffeList(orderingList.indexOf(item))})
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically){
                    Text(orderingItem)
                    IconButton(onClick = {expanded = !expanded},
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Filtro"
                        )
                    }
                }
            }
            LazyRow (modifier = Modifier.padding(vertical = 5.dp).fillMaxSize()) {
                items(coffeList) {
                        item -> Box(modifier = Modifier.clickable {navController.navigate("details/${item.id}")  }){
                          CoffeBox(item)
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
        }
    }
}

@Composable
fun DetailsScreen(navController: NavHostController, id: Int){
    Text(id.toString())
}

@Composable
fun AppNavHost(navController: NavHostController) {

        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(navController)
             }
            composable("details/{id}", arguments = listOf(navArgument("id") {type = NavType.IntType})) {
                backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id")
                if (id != null) {
                    DetailsScreen(navController, id)
                }
            }
        }
    }
}