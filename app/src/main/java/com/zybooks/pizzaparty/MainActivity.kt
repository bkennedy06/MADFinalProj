package com.zybooks.pizzaparty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PizzaPartyScreen(modifier: Modifier = Modifier) {
   var showAddAlbumDialog by remember { mutableStateOf(false) }
   var albumList by remember { mutableStateOf(mutableListOf<Album>()) } // Store user albums

   Scaffold(
      topBar = {
         CenterAlignedTopAppBar(
            title = { Text(text = "Collection") },
            navigationIcon = {
               IconButton(onClick = { /* Handle menu click */ }) {
                  Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
               }
            },
            actions = {
               IconButton(onClick = { /* Handle profile click */ }) {
                  Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Profile")
               }
            }
         )
      },
      bottomBar = {
         BottomAppBar(
            actions = {
               IconButton(onClick = { /* Handle search click */ }) {
                  Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
               }
            },
            floatingActionButton = {
               FloatingActionButton(
                  onClick = { showAddAlbumDialog = true },
                  containerColor = Color(0xFF8A48E1) // FAB color
               ) {
                  Icon(imageVector = Icons.Default.Add, contentDescription = "Add Vinyl", tint = MaterialTheme.colorScheme.onPrimary)
               }
            }
         )
      }
   ) { innerPadding ->
      Column(
         modifier = modifier
            .padding(innerPadding)
            .padding(10.dp)
      ) {
         VinylCollectionGrid(albumList) // Pass albums to grid
      }
   }

   if (showAddAlbumDialog) {
      AddAlbumDialog(
         onDismiss = { showAddAlbumDialog = false },
         onAddAlbum = { newAlbum ->
            albumList = albumList.toMutableList().apply { add(newAlbum) } // Add new album to list
         }
      )
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
fun VinylCollectionGrid(albums: List<Album>) {
   LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(8.dp)
   ) {
      items(albums) { album ->
         var showDialog by remember { mutableStateOf(false) }

         VinylItem(album = album, onClick = { showDialog = true })

         if (showDialog) {
            VinylDetailsPopup(album = album, onDismiss = { showDialog = false })
         }
      }
   }
}


@Composable
fun VinylItem(album: Album, onClick: () -> Unit) {
   Card(
      modifier = Modifier
         .padding(8.dp)
         .fillMaxWidth()
         .aspectRatio(1f)
         .clickable { onClick() },
      shape = RoundedCornerShape(12.dp),
   ) {
      Column(
         modifier = Modifier.fillMaxSize(),
         horizontalAlignment = Alignment.CenterHorizontally
      ) {
         Image(
            painter = painterResource(id = android.R.drawable.ic_menu_gallery), // Placeholder
            contentDescription = "Album Cover",
            modifier = Modifier
               .fillMaxWidth()
               .aspectRatio(1f)
         )
         Spacer(modifier = Modifier.height(8.dp))
         Text(
            text = album.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
         )
      }
   }
}


@Composable
fun VinylDetailsPopup(album: Album, onDismiss: () -> Unit) {
   Dialog(onDismissRequest = { onDismiss() }) {
      Surface(
         shape = RoundedCornerShape(16.dp),
         color = MaterialTheme.colorScheme.surface,
         modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
      ) {
         Column(
            modifier = Modifier
               .fillMaxWidth()
               .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
         ) {
            Image(
               painter = painterResource(id = android.R.drawable.ic_menu_gallery), // Placeholder
               contentDescription = "Album Cover",
               modifier = Modifier
                  .fillMaxWidth()
                  .aspectRatio(1f)
                  .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = album.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = "Artist: ${album.artist}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Release Year: ${album.releaseYear}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Genre: ${album.genre}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
         }
      }
   }
}




@Composable
fun AddAlbumDialog(onDismiss: () -> Unit, onAddAlbum: (Album) -> Unit) {
   var albumName by remember { mutableStateOf("") }
   var artist by remember { mutableStateOf("") }
   var releaseYear by remember { mutableStateOf("") }
   var genre by remember { mutableStateOf("") }
   var albumCover by remember { mutableStateOf<Int?>(null) } // Placeholder for image

   Dialog(onDismissRequest = { onDismiss() }) {
      Surface(
         shape = RoundedCornerShape(16.dp),
         color = MaterialTheme.colorScheme.surface,
         modifier = Modifier.padding(16.dp)
      ) {
         Column(
            modifier = Modifier.padding(16.dp)
         ) {
            Text(text = "Add Entry", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
               value = albumName,
               onValueChange = { albumName = it },
               label = { Text("Title") },
               singleLine = true,
               modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
               value = artist,
               onValueChange = { artist = it },
               label = { Text("Artist") },
               singleLine = true,
               modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
               value = releaseYear,
               onValueChange = { releaseYear = it },
               label = { Text("Release Year") },
               singleLine = true,
               modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
               value = genre,
               onValueChange = { genre = it },
               label = { Text("Genre") },
               singleLine = true,
               modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Left-aligned Image Upload Button
            Row(
               modifier = Modifier.fillMaxWidth(),
               verticalAlignment = Alignment.CenterVertically
            ) {
               IconButton(
                  onClick = { /* TODO: Implement image picker */ },
                  modifier = Modifier
                     .size(50.dp)
                     .clip(RoundedCornerShape(12.dp))
                     .background(MaterialTheme.colorScheme.primaryContainer)
               ) {
                  Icon(
                     imageVector = Icons.Default.Add,
                     contentDescription = "Upload Album Cover",
                     tint = MaterialTheme.colorScheme.onPrimaryContainer
                  )
               }
               Spacer(modifier = Modifier.width(8.dp))
               Text(text = "Upload Cover", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
               Button(onClick = { onDismiss() }) {
                  Text("Cancel")
               }
               Button(onClick = {
                  onAddAlbum(Album(albumName, artist, releaseYear, genre))
                  onDismiss()
               }) {
                  Text("Add")
               }
            }
         }
      }
   }
}



data class Album(
   val name: String,
   val artist: String,
   val releaseYear: String,
   val genre: String
)

