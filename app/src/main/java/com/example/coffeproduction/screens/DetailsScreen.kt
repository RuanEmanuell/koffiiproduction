package com.example.coffeproduction.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.coffeproduction.R
import com.example.coffeproduction.model.Coffee
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

@Composable
fun DetailsScreen(id: Long, navController: NavHostController){
    val database = Firebase.database
    val myRef = database.getReference("coffees")
    var coffee: Coffee? by remember { mutableStateOf(null) }

    LaunchedEffect(true) {
        myRef.child(id.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val currentCoffee = dataSnapshot.getValue(Coffee::class.java)
                    coffee = currentCoffee
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Erro ao ler dados: ${error.toException()}")
            }
        })
    }

    fun editCoffe(){
        navController.navigate("add/${id}")
    }

    fun deleteCoffe(){
        myRef.child(id.toString()).removeValue()
        navController.navigate("home")
    }

    if (coffee != null) {
        Column (modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(coffee!!.image)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = coffee!!.name,
                alignment = Alignment.Center,
                contentScale = ContentScale.Fit,
                modifier = Modifier.background(color = Color(0xFF006241), shape = CircleShape).width(250.dp).height(250.dp)
            )
            Text(coffee!!.name, fontSize = 25.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 10.dp))
            Text(coffee!!.description, fontSize = 15.sp, modifier = Modifier.padding(top = 2.dp))
            Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
                Text("R$ ", fontSize = 25.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 5.dp))
                Text(coffee!!.price.toString().substring(0, coffee!!.price.toString().indexOf(".")), fontSize = 40.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 5.dp))
                Text(coffee!!.price.toString().substring(coffee!!.price.toString().indexOf("."), coffee!!.price.toString().length), fontSize = 25.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 5.dp))
            }
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp), horizontalArrangement = Arrangement.Center){
                Text("Aroma: ${coffee!!.aroma}/5", textAlign = TextAlign.Center, modifier = Modifier.background(
                    Color(0xFFF1807E)
                ).width(95.dp).padding(vertical = 10.dp), fontSize = 13.sp)
                Spacer(Modifier.width(5.dp))
                Text("Acidez: ${coffee!!.acidity}/5", textAlign = TextAlign.Center, modifier = Modifier.background(
                    Color(0xFFA8DAF5)
                ).width(95.dp).padding(vertical = 10.dp), fontSize = 13.sp)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                Text("Amargor: ${coffee!!.bitterness}/5", textAlign = TextAlign.Center, modifier = Modifier.background(
                    Color(0xFFEDED5E)
                ).width(95.dp).padding(vertical = 10.dp), fontSize = 13.sp)
                Spacer(Modifier.width(5.dp))
                Text("Sabor: ${coffee!!.flavor}/5", textAlign = TextAlign.Center, modifier = Modifier.background(
                    Color(0xFFF4CBFF)
                ).width(95.dp).padding(vertical = 10.dp), fontSize = 13.sp)
            }

            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                IconButton(onClick = {editCoffe()}, modifier = Modifier.padding(10.dp).background(color = Color(0xFF006241), shape = CircleShape)) {
                    Icon(Icons.Filled.Edit, "Editar", tint = Color.White)
                }
                IconButton(onClick = {deleteCoffe()}, modifier = Modifier.padding(10.dp).background(color = Color(0xFF006241), shape = CircleShape)) {
                    Icon(Icons.Filled.Delete, "Deletar", tint = Color.White)
                }

            }
        }
    } else {
        CircularProgressIndicator()
    }
}