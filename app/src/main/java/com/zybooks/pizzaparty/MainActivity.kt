package com.zybooks.pizzaparty

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import android.graphics.ImageDecoder
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.filled.Info
import com.zybooks.pizzaparty.ui.theme.PizzaPartyTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ListItem
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.scale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContent {
         PizzaPartyTheme {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
               MainScreen()
            }
         }
      }
   }
}


@Composable
fun MainScreen() {
   val navController = rememberNavController()
   val vinylCollectionViewModel: VinylCollectionViewModel = viewModel() // Shared ViewModel

   NavHost(navController = navController, startDestination = "home") {
      composable("home") { PizzaPartyScreen(navController, vinylCollectionViewModel) }
      composable("settings") { SettingsScreen(navController) }
      composable("stats") { StatsScreen(navController) }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PizzaPartyScreen(navController: NavController, vinylCollectionViewModel: VinylCollectionViewModel = viewModel()) {
   val albumList by vinylCollectionViewModel.albumList
   var showBottomSheet by remember { mutableStateOf(false) }
   var showAddAlbumDialog by remember { mutableStateOf(false) }

   Scaffold(
      topBar = {
         CenterAlignedTopAppBar(
            title = { Text(text = "Collection") },
            navigationIcon = {
               IconButton(onClick = { showBottomSheet = true }) {
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
               var fabScale by remember { mutableStateOf(1f) }
               var hasAnimated by remember { mutableStateOf(false) } // Tracks if animation has run

               LaunchedEffect(albumList.isNotEmpty()) {
                  if (!hasAnimated && albumList.isEmpty()) {
                     hasAnimated = true
                     while (true) {
                        fabScale = 1.2f
                        delay(500)
                        fabScale = 1f
                        delay(500)
                     }
                  }
               }

               FloatingActionButton(
                  onClick = { showAddAlbumDialog = true },
                  containerColor = Color(0xFF8A48E1),
                  modifier = Modifier.scale(fabScale) // Apply scale animation
               ) {
                  Icon(imageVector = Icons.Default.Add, contentDescription = "Add Vinyl",
                     tint = MaterialTheme.colorScheme.onPrimary)
               }
            }
         )
      }
   ) { innerPadding ->
      Column(
         modifier = Modifier
            .padding(innerPadding)
            .padding(10.dp)
            .fillMaxSize(),
         verticalArrangement = Arrangement.Center,
         horizontalAlignment = Alignment.CenterHorizontally
      ) {
         if (albumList.isEmpty()) {
            // Show empty state message if no albums
            Text(
               text = "Wow, it's empty in here...",
               style = MaterialTheme.typography.headlineSmall,
               modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
               text = "Let's start building your collection!",
               style = MaterialTheme.typography.bodyLarge
            )
         } else {
            VinylCollectionGrid(albums = albumList, onDeleteAlbum = { vinylCollectionViewModel.deleteAlbum(it) })
         }
      }
   }

   if (showBottomSheet) {
      ModalBottomSheet(
         onDismissRequest = { showBottomSheet = false }
      ) {
         Column {
            ListItem(
               headlineContent = { Text("Wishlist") },
               leadingContent = { Icon(Icons.Default.List, contentDescription = "Wishlist") },
               modifier = Modifier.clickable { /* Handle Wishlist */ }
            )
            ListItem(
               headlineContent = { Text("Statistics") },
               leadingContent = { Icon(Icons.Default.Info, contentDescription = "Statistics") },
               modifier = Modifier.clickable {
                  showBottomSheet = false
                  navController.navigate("stats")
               }
            )
            ListItem(
               headlineContent = { Text("Settings") },
               leadingContent = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
               modifier = Modifier.clickable {
                  showBottomSheet = false
                  navController.navigate("settings")
               }
            )
         }
      }
   }

   if (showAddAlbumDialog) {
      AddAlbumDialog(
         onDismiss = { showAddAlbumDialog = false },
         onAddAlbum = { newAlbum ->
            vinylCollectionViewModel.addAlbum(newAlbum)
         }
      )
   }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
   val navController = rememberNavController() // fake it til ya make it
   PizzaPartyTheme {
      PizzaPartyScreen(navController)
   }
}


@Composable
fun VinylCollectionGrid(albums: List<Album>, onDeleteAlbum: (Album) -> Unit) {
   LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(8.dp)
   ) {
      items(albums) { album ->
         var showDialog by remember { mutableStateOf(false) }

         VinylItem(album = album, onClick = { showDialog = true })

         if (showDialog) {
            VinylDetailsPopup(
               album = album,
               onDismiss = { showDialog = false },
               onDelete = { onDeleteAlbum(it) }
            )
         }
      }
   }
}

@Composable
fun VinylItem(album: Album, onClick: () -> Unit) {
   val context = LocalContext.current

   val bitmap = remember(album.imageUri) {
      album.imageUri.takeIf { it.isNotEmpty() }?.let { uriString ->
         val uri = Uri.parse(uriString)
         if (Build.VERSION.SDK_INT < 28) {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
         } else {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
         }
      }
   }

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
         bitmap?.let {
            Image(
               bitmap = it.asImageBitmap(),
               contentDescription = "Album Cover",
               modifier = Modifier
                  .fillMaxWidth()
                  .aspectRatio(1f)
                  .clip(RoundedCornerShape(12.dp)),
               contentScale = ContentScale.Crop
            )
         } ?: Image(
            painter = painterResource(id = android.R.drawable.ic_menu_gallery), // Placeholder
            contentDescription = "Placeholder Album Cover",
            modifier = Modifier
               .fillMaxWidth()
               .aspectRatio(1f)
               .clip(RoundedCornerShape(12.dp))
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
fun VinylDetailsPopup(album: Album, onDismiss: () -> Unit, onDelete: (Album) -> Unit) {
   val context = LocalContext.current
   var showDeleteDialog by remember { mutableStateOf(false) }

   val bitmap = remember(album.imageUri) {
      album.imageUri.takeIf { it.isNotEmpty() }?.let { uriString ->
         val uri = Uri.parse(uriString)
         if (Build.VERSION.SDK_INT < 28) {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
         } else {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
         }
      }
   }

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
            bitmap?.let {
               Image(
                  bitmap = it.asImageBitmap(),
                  contentDescription = "Album Cover",
                  modifier = Modifier
                     .fillMaxWidth()
                     .aspectRatio(1f)
                     .clip(RoundedCornerShape(12.dp)),
                  contentScale = ContentScale.Crop
               )
            } ?: Image(
               painter = painterResource(id = android.R.drawable.ic_menu_gallery),
               contentDescription = "Placeholder Album Cover",
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

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
               Button(onClick = { onDismiss() }) {
                  Text("Close")
               }
               Button(
                  onClick = { showDeleteDialog = true },
                  colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
               ) {
                  Text("Delete")
               }
            }
         }
      }
   }

   // Confirmation Dialog
   if (showDeleteDialog) {
      DeleteConfirmationDialog(
         album = album,
         onDismiss = { showDeleteDialog = false },
         onConfirmDelete = {
            onDelete(album)
            showDeleteDialog = false
            onDismiss()
         }
      )
   }
}


@Composable
fun AddAlbumDialog(onDismiss: () -> Unit, onAddAlbum: (Album) -> Unit) {
   var albumName by remember { mutableStateOf("") }
   var artist by remember { mutableStateOf("") }
   var releaseYear by remember { mutableStateOf("") }
   var genre by remember { mutableStateOf("") }
   var albumCoverUri by remember { mutableStateOf<Uri?>(null) }
   val context = LocalContext.current

   val imagePickerLauncher = rememberLauncherForActivityResult(
      contract = ActivityResultContracts.GetContent()
   ) { uri: Uri? ->
      albumCoverUri = uri
   }

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

            TextField(value = albumName, onValueChange = { albumName = it }, label = { Text("Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            TextField(value = artist, onValueChange = { artist = it }, label = { Text("Artist") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            TextField(value = releaseYear, onValueChange = { releaseYear = it }, label = { Text("Release Year") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            TextField(value = genre, onValueChange = { genre = it }, label = { Text("Genre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            // Image Selection Row
            Row(
               modifier = Modifier.fillMaxWidth(),
               verticalAlignment = Alignment.CenterVertically
            ) {
               IconButton(
                  onClick = { imagePickerLauncher.launch("image/*") },
                  modifier = Modifier
                     .size(50.dp)
                     .clip(RoundedCornerShape(12.dp))
                     .background(MaterialTheme.colorScheme.primaryContainer)
               ) {
                  Icon(imageVector = Icons.Default.Add, contentDescription = "Upload Album Cover", tint = MaterialTheme.colorScheme.onPrimaryContainer)
               }
               Spacer(modifier = Modifier.width(8.dp))

               albumCoverUri?.let { uri ->
                  val bitmap = remember(uri) {
                     if (Build.VERSION.SDK_INT < 28) {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                     } else {
                        val source = ImageDecoder.createSource(context.contentResolver, uri)
                        ImageDecoder.decodeBitmap(source)
                     }
                  }

                  Image(
                     bitmap = bitmap.asImageBitmap(),
                     contentDescription = "Selected Image",
                     modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp)),
                     contentScale = ContentScale.Crop
                  )
               } ?: Text(text = "Upload Cover", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
               Button(onClick = { onDismiss() }) {
                  Text("Cancel")
               }
               Button(onClick = {
                  if (albumName.isNotBlank() && albumCoverUri != null) {
                     onAddAlbum(Album(albumName, artist, releaseYear, genre, albumCoverUri.toString()))
                     onDismiss()
                  }
               }) {
                  Text("Add")
               }
            }
         }
      }
   }
}

@Composable
fun DeleteConfirmationDialog(album: Album, onDismiss: () -> Unit, onConfirmDelete: () -> Unit) {
   Dialog(onDismissRequest = { onDismiss() }) {
      Surface(
         shape = RoundedCornerShape(16.dp),
         color = MaterialTheme.colorScheme.surface,
         modifier = Modifier.padding(16.dp)
      ) {
         Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
         ) {
            Text("Delete Album?", style = MaterialTheme.typography.headlineSmall)
            Text("Are you sure you want to delete \"${album.name}\"?", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
               Button(onClick = { onDismiss() }) {
                  Text("Cancel")
               }
               Button(
                  onClick = { onConfirmDelete() },
                  colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
               ) {
                  Text("Delete")
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
   val genre: String,
   val imageUri: String
)
