package com.example.coffeproduction

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue

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
                        if(currentRoute == "home") {
                            FloatingActionButton(
                                onClick = {navController.navigate("add")},
                                containerColor = Color(0xFF006241),
                                contentColor = Color.White,
                                shape = CircleShape
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "Adicionar"
                                )
                            }
                        }
                    },
                ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding).fillMaxWidth()){
                            AppNavHost(navController)
                }
            }
        }
    }
}

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

    val database = Firebase.database
    val myRef = database.getReference("coffees")

    val originalCoffeList = remember { mutableStateListOf<Coffe>() }
    var coffeList by remember { mutableStateOf(originalCoffeList.toList()) }

    val maxPriceCoffe = originalCoffeList.maxByOrNull { it.price }?.id
    val maxAromaCoffe = originalCoffeList.maxByOrNull { it.aroma }?.id
    val minAcidityCoffe = originalCoffeList.minByOrNull { it.acidity }?.id

    LaunchedEffect(true) {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val coffees = mutableListOf<Coffe>()

                for (childSnapshot in snapshot.children) {
                    val coffee = childSnapshot.getValue(Coffe::class.java)
                    coffee?.let { coffees.add(it) }
                }

                originalCoffeList.clear()
                originalCoffeList.addAll(coffees)

                if (originalCoffeList.isEmpty()) {
                    val defaultCoffe = Coffe(
                        id = 0L,
                        name = "Café Latte",
                        description = "Café equilibrado com leite vaporizado",
                        aroma = 5,
                        acidity = 2,
                        bitterness = 1,
                        flavor = 5,
                        price = 9.99,
                        image = "https://cdn.starbuckschilledcoffee.com/4aee53/globalassets/evo/our-products/chilled-classics/chilled-cup/1_cc_caffelatte_r.png?width=480&height=600&rmode=max&format=webp"
                    )
                    myRef.setValue(listOf(defaultCoffe))
                    originalCoffeList.add(defaultCoffe)

                    val idRef = database.getReference("idCounter")
                    idRef.setValue(defaultCoffe.id)
                }

                coffeList = originalCoffeList.toList()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Erro ao ler dados: ${error.toException()}")
            }
        })
    }


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
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            CustomInput(
                value = text,
                onValueChange = {text = it; coffeList = originalCoffeList; coffeList = coffeList.filter { it.name.contains(text) }},
                label = "Pesquisar um café...")
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
                        item -> Box(modifier = Modifier.clickable {navController.navigate("details/${item.id}"); }){
                            if (maxPriceCoffe != null && maxAromaCoffe != null && minAcidityCoffe != null) {
                                CoffeBox(item, maxPriceCoffe, maxAromaCoffe, minAcidityCoffe)
                            }
                        }
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text("${coffeList.size} ${if (coffeList.size == 1) "Item encontrado" else "Itens encontrados"}")
        }
    }
}

@Composable
fun CustomInput(value: String, onValueChange: (String) -> Unit, label: String) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = {Text (label, color = Color(0xFF006241)) },
        modifier = Modifier.border(width = 2.dp, shape = RoundedCornerShape(4.dp), color = Color(0xFF006241)).fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun CoffeBox(item: Coffe, maxPriceCoffe: Long, maxAromaCoffe: Long, minAcidityCoffe: Long){
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
                    Text("Mais caro", fontSize = 9.sp, textAlign = TextAlign.Center,  modifier = Modifier.background(Color(0xFFD22B2B)).padding(3.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                }
                if(item.id == maxAromaCoffe){
                    Text("Mais aromático", fontSize = 9.sp, textAlign = TextAlign.Center, modifier = Modifier.background(Color(0xFFF1807E)).padding(3.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                }
                if(item.id == minAcidityCoffe){
                    Text("Menos ácido", fontSize = 9.sp, textAlign = TextAlign.Center, modifier = Modifier.background(Color(0xFFA8DAF5)).padding(3.dp))
                }
            }
        }
    }
}

@Composable
    fun DetailsScreen(id: Long, navController: NavHostController){
        val database = Firebase.database
        val myRef = database.getReference("coffees")
        var coffe: Coffe? by remember { mutableStateOf(null) }

        LaunchedEffect(true) {
            myRef.child(id.toString()).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val currentCoffe = dataSnapshot.getValue(Coffe::class.java)
                        coffe = currentCoffe
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Erro ao ler dados: ${error.toException()}")
                }
            })
        }

        fun editCoffe(){

        }

        fun deleteCoffe(){
            myRef.child(id.toString()).removeValue()
            navController.navigate("home")
        }

        if (coffe != null) {
            Column (modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(coffe!!.image)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = coffe!!.name,
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.background(color = Color(0xFF006241), shape = CircleShape).width(250.dp).height(250.dp)
                )
                Text(coffe!!.name, fontSize = 25.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 10.dp))
                Text(coffe!!.description, fontSize = 15.sp, modifier = Modifier.padding(top = 2.dp))
                Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
                    Text("R$ ", fontSize = 25.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 5.dp))
                    Text(coffe!!.price.toString().substring(0, coffe!!.price.toString().indexOf(".")), fontSize = 40.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 5.dp))
                    Text(coffe!!.price.toString().substring(coffe!!.price.toString().indexOf("."), coffe!!.price.toString().length), fontSize = 25.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 5.dp))
                }
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp), horizontalArrangement = Arrangement.Center){
                    Text("Aroma: ${coffe!!.aroma}/5", textAlign = TextAlign.Center, modifier = Modifier.background(Color(0xFFF1807E)).width(95.dp).padding(vertical = 10.dp), fontSize = 13.sp)
                    Spacer(Modifier.width(5.dp))
                    Text("Acidez: ${coffe!!.acidity}/5", textAlign = TextAlign.Center, modifier = Modifier.background(Color(0xFFA8DAF5)).width(95.dp).padding(vertical = 10.dp), fontSize = 13.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                    Text("Amargor: ${coffe!!.bitterness}/5", textAlign = TextAlign.Center, modifier = Modifier.background(Color(0xFFEDED5E)).width(95.dp).padding(vertical = 10.dp), fontSize = 13.sp)
                    Spacer(Modifier.width(5.dp))
                    Text("Sabor: ${coffe!!.flavor}/5", textAlign = TextAlign.Center, modifier = Modifier.background(Color(0xFFF4CBFF)).width(95.dp).padding(vertical = 10.dp), fontSize = 13.sp)
                }

                Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                    IconButton(onClick = {deleteCoffe()}, modifier = Modifier.padding(10.dp).background(color = Color(0xFF006241), shape = CircleShape)) {
                        Icon(Icons.Filled.Edit, "Editar", tint = Color.White)
                    }
                    IconButton(onClick = {editCoffe()}, modifier = Modifier.padding(10.dp).background(color = Color(0xFF006241), shape = CircleShape)) {
                        Icon(Icons.Filled.Delete, "Deletar", tint = Color.White)
                    }

                }
            }
        } else {
            CircularProgressIndicator()
        }
    }

@Composable
fun AddScreen(navController: NavHostController){
    var itemName by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }
    var itemAroma by remember { mutableStateOf(1) }
    var itemAcidity by remember { mutableStateOf(1) }
    var itemBitterness by remember { mutableStateOf(1) }
    var itemFlavor by remember { mutableStateOf(1) }
    var itemPrice by remember { mutableStateOf(0.0) }
    var itemImage by remember { mutableStateOf(0) }

    val database = Firebase.database
    val idRef = database.getReference("idCounter")
    var idCounter = 0L;

    var imageList = listOf(
        "https://cdn.starbuckschilledcoffee.com/4aee53/globalassets/evo/our-products/chilled-classics/chilled-cup/1_cc_caffelatte_r.png?width=480&height=600&rmode=max&format=webp",
        "https://cdn.starbuckschilledcoffee.com/4aeb25/globalassets/evo/our-products/frappuccino/2_frp_creamycoffee.png?width=480&height=600&rmode=max&format=webp",
        "https://cdn.starbuckschilledcoffee.com/4aef4a/globalassets/evo/our-products/chilled-classics/chilled-cup/1_cc_caramelmacchiato_r.png?width=480&height=600&rmode=max&format=webp",
        "https://dutchshopper.com/cdn/shop/files/908858_grande.png?v=1733635647",
        "https://assets.caseys.com/m/6236c752af7a0ad1/400x400-1200002845_base.PNG")

    idRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                idCounter = dataSnapshot.getValue(Long::class.java) ?: 0L
            } else {
                Log.e("IdCounter", "Nó não encontrado")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("IdCounter", "Falha ao ler dados: ${error.toException()}")
        }
    })

    fun saveCoffe(){
        val myRef = database.getReference("coffees")
        idRef.setValue(idCounter + 1)
        val newCoffee = Coffe(idCounter + 1, itemName, itemDescription, itemAroma, itemAcidity, itemBitterness, itemFlavor, itemPrice, imageList.get(itemImage))
        val newCoffeeRef = myRef.child(newCoffee.id.toString())
        newCoffeeRef.setValue(newCoffee)
        navController.navigate("home")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(5.dp).verticalScroll(rememberScrollState())){
        Text("Adicionar / Editar Café", fontWeight = FontWeight.Bold, fontSize = 25.sp, modifier = Modifier.padding(vertical = 30.dp))
        CustomInput(itemName, onValueChange = {itemName = it},  "Digite o nome do café")
        Spacer(modifier = Modifier.height(10.dp))
        CustomInput(itemDescription, onValueChange = {itemDescription = it},  "Digite a descrição do café")
        Spacer(modifier = Modifier.height(10.dp))
        CustomInput(itemPrice.toString(), onValueChange = {itemPrice = it.toDouble()},  "Digite o preço (em R$)")
        Spacer(modifier = Modifier.height(10.dp))
        CustomSlider(
            label = "Aroma: $itemAroma",
            value = itemAroma.toFloat(),
            onValueChange = {itemAroma = it.toInt()}
        )
        CustomSlider(
            label = "Acidez: $itemAcidity",
            value = itemAcidity.toFloat(),
            onValueChange = { itemAcidity = it.toInt() }
        )
        CustomSlider(
            label = "Amargor: $itemBitterness",
            value = itemBitterness.toFloat(),
            onValueChange = { itemBitterness = it.toInt() }
        )
        CustomSlider(
            label = "Sabor: $itemFlavor",
            value = itemFlavor.toFloat(),
            onValueChange = { itemFlavor = it.toInt() }
        )
        Text("Selecione uma imagem: ", fontSize = 20.sp, color = Color(0xFF006241))
        Spacer(modifier = Modifier.height(5.dp))
        LazyRow (modifier = Modifier.fillMaxSize()) {
            itemsIndexed(imageList){
                    index, item -> Box (modifier = Modifier.width(150.dp).height(150.dp).background(shape = CircleShape, color =  if (index == itemImage) Color(0xFF006241) else Color.White).padding(2.dp).clickable { itemImage = index }, contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = "Imagem de café",
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center
                )
            }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = {saveCoffe()}, colors = ButtonColors(
            containerColor = Color(0xFF006241),
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray
        )) {
            Text("Adicionar Café", fontWeight = FontWeight.Bold);
        }
    }
}

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

@Composable
fun AppNavHost(navController: NavHostController) {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(navController)
             }
            composable("details/{id}", arguments = listOf(navArgument("id") {type = NavType.LongType})) {
                backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id")
                if (id != null) {
                    DetailsScreen(id, navController)
                }
            }
            composable("add") {
                AddScreen(navController)
            }
        }
    }
}