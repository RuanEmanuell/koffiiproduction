package com.example.coffeproduction.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.coffeproduction.R
import com.example.coffeproduction.components.CustomInput
import com.example.coffeproduction.components.CustomSlider
import com.example.coffeproduction.model.Coffee
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

@Composable
fun AddScreen(id: Long?, navController: NavHostController,){
    var itemName by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }
    var itemAroma by remember { mutableStateOf(1) }
    var itemAcidity by remember { mutableStateOf(1) }
    var itemBitterness by remember { mutableStateOf(1) }
    var itemFlavor by remember { mutableStateOf(1) }
    var itemPrice by remember { mutableStateOf(0.0) }
    var itemImage by remember { mutableStateOf(0) }

    val imageList = listOf(
        "https://cdn.starbuckschilledcoffee.com/4aee53/globalassets/evo/our-products/chilled-classics/chilled-cup/1_cc_caffelatte_r.png?width=480&height=600&rmode=max&format=webp",
        "https://cdn.starbuckschilledcoffee.com/4aeb25/globalassets/evo/our-products/frappuccino/2_frp_creamycoffee.png?width=480&height=600&rmode=max&format=webp",
        "https://cdn.starbuckschilledcoffee.com/4aef4a/globalassets/evo/our-products/chilled-classics/chilled-cup/1_cc_caramelmacchiato_r.png?width=480&height=600&rmode=max&format=webp",
        "https://dutchshopper.com/cdn/shop/files/908858_grande.png?v=1733635647",
        "https://assets.caseys.com/m/6236c752af7a0ad1/400x400-1200002845_base.PNG")

    val database = Firebase.database
    val idRef = database.getReference("idCounter")
    var idCounter = 0L;

    val myRef = database.getReference("coffees")
    if(id != -1L){
        myRef.child(id.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val editCoffee = dataSnapshot.getValue(Coffee::class.java)

                    if(editCoffee != null){
                        itemName = editCoffee.name
                        itemDescription = editCoffee.description
                        itemAroma = editCoffee.aroma
                        itemAcidity = editCoffee.acidity
                        itemBitterness = editCoffee.bitterness
                        itemFlavor = editCoffee.flavor
                        itemPrice = editCoffee.price
                        itemImage = imageList.indexOf(editCoffee.image)
                    }
                } else {
                    Log.e("IdCounter", "Nó não encontrado")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("IdCounter", "Falha ao ler dados: ${error.toException()}")
            }
        })
    }


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
        if(id == -1L){
            idRef.setValue(idCounter + 1)
        }
        var newCoffee = Coffee(idCounter + 1, itemName, itemDescription, itemAroma, itemAcidity, itemBitterness, itemFlavor, itemPrice, imageList.get(itemImage))
        if(id != -1L){
            newCoffee.id = id!!
        }
        val newCoffeeRef = myRef.child(newCoffee.id.toString())
        newCoffeeRef.setValue(newCoffee)
        navController.navigate("home")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(5.dp).verticalScroll(rememberScrollState())){
        Text( if(id == -1L) "Adicionar Café" else "Editar Café" , fontWeight = FontWeight.Bold, fontSize = 25.sp, modifier = Modifier.padding(vertical = 30.dp))
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
        )
        ) {
            Text( if(id == -1L) "Adicionar Café" else "Editar Café", fontWeight = FontWeight.Bold);
        }
    }
}