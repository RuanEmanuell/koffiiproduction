package com.example.coffeproduction

import HomeScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.coffeproduction.screens.AddScreen
import com.example.coffeproduction.screens.DetailsScreen
import com.example.coffeproduction.ui.theme.CoffeProductionTheme

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
                            Text("KoffiiProduction");
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
                                onClick = {navController.navigate("add/-1")},
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
            composable("add/{id}", arguments = listOf(navArgument("id") {type = NavType.LongType})) {
                    backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id")
                AddScreen(id, navController)
            }
        }
    }
}