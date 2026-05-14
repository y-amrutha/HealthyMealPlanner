package com.example.healthymealplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MealPlannerApp() }
    }
}

@Composable
fun MealPlannerApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome") {

        composable("welcome") { WelcomeScreen(navController) }
        composable("age") { AgeScreen(navController) }
        composable("health") { HealthScreen(navController) }

        composable("food/{condition}") { backStackEntry ->
            val condition = backStackEntry.arguments?.getString("condition") ?: ""
            FoodScreen(navController, condition)
        }

        composable("meal/{foods}/{condition}") { backStackEntry ->
            val foods = backStackEntry.arguments?.getString("foods") ?: ""
            val condition = backStackEntry.arguments?.getString("condition") ?: ""
            MealScreen(navController, foods, condition)
        }
    }
}

@Composable
fun AppBackground(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        content()
    }
}

@Composable
fun WelcomeScreen(navController: NavHostController) {

    AppBackground {

        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                "Healthy Meal Planner",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(id = R.drawable.meal),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(onClick = { navController.navigate("age") }) {
                Text("Start", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun AgeScreen(navController: NavHostController) {

    var age by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AppBackground {

        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                "Enter Age",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = age,
                onValueChange = {
                    age = it
                    error = ""
                },
                label = { Text("Age", fontWeight = FontWeight.Bold) },
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.2f)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    cursorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (error.isNotEmpty()) {
                Text(error, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                val a = age.toIntOrNull()
                if (a == null) error = "Enter valid age"
                else if (a < 35) error = "Only for age 35+"
                else navController.navigate("health")
            }) {
                Text("Next")
            }
        }
    }
}

@Composable
fun HealthScreen(navController: NavHostController) {

    var selected by remember { mutableStateOf("") }

    val options = listOf(
        "Blood Pressure",
        "Diabetes",
        "Leg Pain",
        "Body Pain",
        "Maintain Health"
    )

    AppBackground {

        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Select Condition", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(modifier = Modifier.height(20.dp))

            options.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = selected == it, onClick = { selected = it })
                    Text(it, color = Color.White, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(onClick = {
                navController.navigate("food/$selected")
            }) {
                Text("Next")
            }
        }
    }
}

@Composable
fun FoodScreen(navController: NavHostController, condition: String) {

    var food by remember { mutableStateOf("") }

    AppBackground {

        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {

            Button(onClick = { navController.popBackStack() }) {
                Text("Back")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Enter Available Food", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(modifier = Modifier.height(10.dp))

            Text("Example: rice, dal, potato fry", color = Color.White)

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = food,
                onValueChange = { food = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(onClick = {
                navController.navigate("meal/$food/$condition")
            }) {
                Text("Generate")
            }
        }
    }
}

@Composable
fun MealScreen(
    navController: NavHostController,
    foods: String,
    condition: String
) {

    val foodList = foods.lowercase().split(",").map { it.trim() }

    fun containsFood(word: String): Boolean {
        return foodList.any { it.contains(word) }
    }

    AppBackground {

        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {

            Button(onClick = { navController.popBackStack() }) {
                Text("Back")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Foods Available", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(modifier = Modifier.height(10.dp))

            foodList.forEach {
                Text("• $it", color = Color.White, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Healthy Suggestions", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)

            if (containsFood("rice")) {
                Text("• Try chapati instead of rice for better digestion", color = Color.White)
            }

            if (containsFood("dal")) {
                Text("• Dal is a good protein source", color = Color.White)
            }

            if (containsFood("fry") || containsFood("fried")) {
                Text("• Reduce fried foods for better heart health", color = Color.White)
            }

            if (!containsFood("fruit")) {
                Text("• Add fruits for vitamins", color = Color.White)
            }

            if (!containsFood("vegetable")) {
                Text("• Add vegetables for nutrition", color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Health Advice", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)

            when (condition) {
                "Blood Pressure" ->
                    Text("• Avoid salty food and eat fruits & vegetables", color = Color.White)

                "Diabetes" ->
                    Text("• Reduce white rice and prefer chapati or millets", color = Color.White)

                "Leg Pain" ->
                    Text("• Eat calcium rich foods like milk, nuts", color = Color.White)

                "Body Pain" ->
                    Text("• Eat protein foods like dal, eggs, nuts", color = Color.White)

                else ->
                    Text("• Maintain balanced diet and regular exercise", color = Color.White)
            }
        }
    }
}