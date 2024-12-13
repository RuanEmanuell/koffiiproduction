import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.coffeproduction.components.CoffeBox
import com.example.coffeproduction.components.CustomInput
import com.example.coffeproduction.model.Coffee
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

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

    val originalCoffeeList = remember { mutableStateListOf<Coffee>() }
    var coffeList by remember { mutableStateOf(originalCoffeeList.toList()) }

    val maxPriceCoffe = originalCoffeeList.maxByOrNull { it.price }?.id
    val maxAromaCoffe = originalCoffeeList.maxByOrNull { it.aroma }?.id
    val minAcidityCoffe = originalCoffeeList.minByOrNull { it.acidity }?.id

    LaunchedEffect(true) {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val coffees = mutableListOf<Coffee>()

                for (childSnapshot in snapshot.children) {
                    val coffee = childSnapshot.getValue(Coffee::class.java)
                    coffee?.let { coffees.add(it) }
                }

                originalCoffeeList.clear()
                originalCoffeeList.addAll(coffees)

                if (originalCoffeeList.isEmpty()) {
                    val defaultCoffee = Coffee(
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
                    myRef.setValue(listOf(defaultCoffee))
                    originalCoffeeList.add(defaultCoffee)

                    val idRef = database.getReference("idCounter")
                    idRef.setValue(defaultCoffee.id)
                }

                coffeList = originalCoffeeList.toList()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Erro ao ler dados: ${error.toException()}")
            }
        })
    }


    fun orderCoffeList(orderingIndex: Int){
        coffeList = originalCoffeeList;
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
                onValueChange = {text = it; coffeList = originalCoffeeList; coffeList = coffeList.filter { it.name.contains(text) }},
                label = "Pesquisar um café...")
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd){
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false}
                ) {
                    orderingList.forEach { item ->
                        DropdownMenuItem(text = { Text(item) }, onClick = {expanded = false; orderingItem = item; orderCoffeList(orderingList.indexOf(item))})
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
