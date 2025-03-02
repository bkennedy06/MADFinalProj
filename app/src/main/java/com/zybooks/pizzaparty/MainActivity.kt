package com.zybooks.pizzaparty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.zybooks.pizzaparty.ui.theme.PizzaPartyTheme
import kotlin.math.ceil
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.ui.graphics.Color

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
      }, // Hamburg icon and profile functionality needed
      bottomBar = {
         BottomAppBar(
            actions = {
               IconButton(onClick = { /* Handle search click */ }) {
                  Icon(
                     imageVector = Icons.Default.Search,
                     contentDescription = "Search"
                  )
               }
            },
            floatingActionButton = {
               FloatingActionButton(
                  onClick = { /* Handle FAB click (Add Vinyl) */ },
                  containerColor = Color(0xFF8A48E1) // Fab color

               ) {
                  Icon(
                     imageVector = Icons.Default.Add,
                     contentDescription = "Add Vinyl",
                     tint = MaterialTheme.colorScheme.onPrimary // Change '+' color
                  )
               }
            }
         )
      } // FAB and search functionality needed
   ) { innerPadding ->
      Column(
         modifier = modifier
            .padding(innerPadding)
            .padding(10.dp)
      ) {
         VinylCollectionGrid()
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
fun HomeScreenPreview() {
   PizzaPartyTheme {
      PizzaPartyScreen()
   }
}

@Composable
fun VinylCollectionGrid() {
   val sampleAlbums = List(6) { "Album ${it + 1}" } // Placeholder data

   LazyVerticalGrid(
      columns = GridCells.Fixed(2), // 2-column grid
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(8.dp)
   ) {
      items(sampleAlbums) { album ->
         VinylItem(album)
      }
   }
}

@Composable
fun VinylItem(album: String) {
   Card(
      modifier = Modifier
         .padding(8.dp)
         .fillMaxWidth()
         .aspectRatio(1f),
      colors = CardDefaults.cardColors(containerColor = Color.LightGray) // Placeholder color
   ) {
      Box(modifier = Modifier.fillMaxSize()) {
         Text(
            text = album,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
         )
      }
   }
}

@Preview(showBackground = true)
@Composable
fun VinylGridPreview() {
   VinylCollectionGrid()
}