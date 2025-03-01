package com.zybooks.pizzaparty

import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zybooks.pizzaparty.ui.theme.PizzaPartyTheme
import kotlin.math.ceil
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold

class MainActivity : ComponentActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      setContent {
         PizzaPartyTheme {
            Surface(
               modifier = Modifier.fillMaxSize(),
               color = MaterialTheme.colorScheme.background
            ) {
               PizzaPartyScreen()
            }
         }
      }
   }
}

fun calculateNumPizzas(
   numPeople: Int,
   hungerLevel: String
): Int {
   val slicesPerPizza = 8
   val slicesPerPerson = when (hungerLevel) {
      "Light" -> 2
      "Medium" -> 3
      else -> 4
   }

   return ceil(numPeople * slicesPerPerson / slicesPerPizza.toDouble()).toInt()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PizzaPartyScreen(modifier: Modifier = Modifier) {
   var totalPizzas by remember { mutableIntStateOf(0) }
   var numPeopleInput by remember { mutableStateOf("") }
   var hungerLevel by remember { mutableStateOf("Medium") }

   Scaffold(
      topBar = {
         CenterAlignedTopAppBar(
            title = { Text(text = "Record Holder") },
            navigationIcon = {
               IconButton(onClick = { /* Handle menu click */ }) {
                  Icon(
                     imageVector = Icons.Default.Menu, // Hamburger icon
                     contentDescription = "Menu"
                  )
               }
            },
            actions = {
               IconButton(onClick = { /* Handle profile click */ }) {
                  Icon(
                     imageVector = Icons.Default.AccountCircle, // Profile icon
                     contentDescription = "Profile"
                  )
               }
            }
         )
      }
   ) { innerPadding ->
      Column(
         modifier = modifier
            .padding(innerPadding) // This ensures content is positioned below the AppBar
            .padding(10.dp)
      ) {
         NumberField(
            labelText = "Number of people?",
            textInput = numPeopleInput,
            onValueChange = { numPeopleInput = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
         )

         RadioGroup(
            labelText = "How hungry?",
            radioOptions = listOf("Light", "Medium", "Ravenous"),
            selectedOption = hungerLevel,
            onSelected = { hungerLevel = it },
            modifier = Modifier.fillMaxWidth()
         )

         Text(
            text = "Total pizzas: $totalPizzas",
            fontSize = 22.sp,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
         )

         Button(
            onClick = {
               totalPizzas = calculateNumPizzas(numPeopleInput.toIntOrNull() ?: 0, hungerLevel)
            },
            modifier = Modifier.fillMaxWidth()
         ) {
            Text("Calculate")
         }
      }
   }
}

@Composable
fun NumberField(
   labelText: String,
   textInput: String,
   onValueChange: (String) -> Unit,
   modifier: Modifier = Modifier
) {
   TextField(
      value = textInput,
      onValueChange = onValueChange,
      label = { Text(labelText) },
      singleLine = true,
      keyboardOptions = KeyboardOptions(
         keyboardType = KeyboardType.Number
      ),
      modifier = modifier
   )
}

@Composable
fun RadioGroup(
   labelText: String,
   radioOptions: List<String>,
   selectedOption: String,
   onSelected: (String) -> Unit,
   modifier: Modifier = Modifier
) {
   val isSelectedOption: (String) -> Boolean = { selectedOption == it }

   Column {
      Text(labelText)
      radioOptions.forEach { option ->
         Row(
            modifier = modifier
               .selectable(
                  selected = isSelectedOption(option),
                  onClick = { onSelected(option) },
                  role = Role.RadioButton
               )
               .padding(8.dp)
         ) {
            RadioButton(
               selected = isSelectedOption(option),
               onClick = null,
               modifier = modifier.padding(end = 8.dp)
            )
            Text(
               text = option,
               modifier = modifier.fillMaxWidth()
            )
         }
      }
   }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
   PizzaPartyTheme {
      PizzaPartyScreen()
   }
}