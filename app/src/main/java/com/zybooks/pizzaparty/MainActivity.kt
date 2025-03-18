package com.zybooks.pizzaparty

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import com.zybooks.pizzaparty.ui.theme.PizzaPartyTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ListItem
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.border
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import android.util.Log

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
   val vinylCollectionViewModel: VinylCollectionViewModel = viewModel()
   val discogsViewModel: DiscogsViewModel = viewModel()
   val statsViewModel: StatsViewModel = viewModel()

   NavHost(navController = navController, startDestination = "home") {
      composable("home") { PizzaPartyScreen(navController, vinylCollectionViewModel) }
      composable("settings") { SettingsScreen(navController) }
      composable("stats") { StatsScreen(navController, statsViewModel, vinylCollectionViewModel) }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PizzaPartyScreen(
   navController: NavController,
   vinylCollectionViewModel: VinylCollectionViewModel = viewModel(),
   discogsViewModel: DiscogsViewModel = viewModel()
) {
   val albumList by vinylCollectionViewModel.albumList
   var showBottomSheet by remember { mutableStateOf(false) }
   var showAddAlbumDialog by remember { mutableStateOf(false) }
   var isSearching by remember { mutableStateOf(false) }  // Controls search bar visibility
   var searchQuery by remember { mutableStateOf("") } // Stores search query
   val focusManager = LocalFocusManager.current

   // Filter albums based on search query
   val filteredAlbums = albumList.filter { album ->
      album.name.contains(searchQuery, ignoreCase = true) ||
              album.artist.contains(searchQuery, ignoreCase = true)
   }

   Scaffold(
      topBar = {
         CenterAlignedTopAppBar(
            title = { Text(text = stringResource(R.string.collection_title)) },
            navigationIcon = {
               IconButton(onClick = { showBottomSheet = true }) {
                  Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
               }
            },
            actions = {
               IconButton(onClick = { navController.navigate("settings") }) {
                  Icon(imageVector = Icons.Default.Settings, contentDescription = "Profile")
               }
            }
         )
      },
      bottomBar = {
         BottomAppBar(
            actions = {
               IconButton(onClick = { isSearching = true }) {
                  Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
               }
            },
            floatingActionButton = {
               FloatingActionButton(
                  onClick = { showAddAlbumDialog = true },
                  containerColor = Color(0xFF8A48E1)
               ) {
                  Icon(imageVector = Icons.Default.Add, contentDescription = "Add Vinyl", tint = MaterialTheme.colorScheme.onPrimary)
               }
            }
         )
      }
   ) { innerPadding ->
      Column(
         modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
         verticalArrangement = Arrangement.Center,
         horizontalAlignment = Alignment.CenterHorizontally
      ) {
         if (filteredAlbums.isEmpty() && searchQuery.isNotBlank()) {
            Text("No matching albums found", style = MaterialTheme.typography.headlineSmall)
         } else if (albumList.isEmpty()) {
            Text(text = stringResource(R.string.empty_collection_message), style = MaterialTheme.typography.headlineSmall)
            Text(text = stringResource(R.string.empty_collection_subtext), style = MaterialTheme.typography.bodyLarge)
         } else {
            VinylCollectionGrid(albums = filteredAlbums, onDeleteAlbum = { vinylCollectionViewModel.deleteAlbum(it) })
         }
      }
   }

   // Bottom sheet menu
   if (showBottomSheet) {
      ModalBottomSheet(
         onDismissRequest = { showBottomSheet = false }
      ) {
         Column {
            ListItem(
               leadingContent = { Icon(Icons.Default.List, contentDescription = "Wishlist") },
               headlineContent = { Text(text = stringResource(R.string.wishlist)) },
               modifier = Modifier.clickable { /* Navigate to Wishlist */ }
            )
            ListItem(
               leadingContent = { Icon(Icons.Default.Info, contentDescription = "Statistics") },
               headlineContent = { Text(text = stringResource(R.string.statistics)) },
               modifier = Modifier.clickable { navController.navigate("stats") }
            )
            ListItem(
               leadingContent = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
               headlineContent = { Text(text = stringResource(R.string.settings)) },
               modifier = Modifier.clickable {
                  navController.navigate("settings")
                  showBottomSheet = false
               }
            )
         }
      }
   }

   // Search bar
   if (isSearching) {
      Box(
         modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)) // Darken background
            .clickable { isSearching = false; focusManager.clearFocus() } // Click outside to dismiss
      ) {
         Surface(
            modifier = Modifier
               .fillMaxWidth()
               .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            tonalElevation = 8.dp
         ) {
            Row(
               verticalAlignment = Alignment.CenterVertically,
               modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
               Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
               Spacer(modifier = Modifier.width(8.dp))
               TextField(
                  value = searchQuery,
                  onValueChange = { searchQuery = it },
                  placeholder = { Text(text = stringResource(R.string.search_text)) },
                  singleLine = true,
                  modifier = Modifier
                     .weight(1f)
                     .focusable(true)
               )
               Spacer(modifier = Modifier.width(8.dp))
               IconButton(onClick = { isSearching = false; focusManager.clearFocus() }) {
                  Icon(imageVector = Icons.Default.Close, contentDescription = "Close Search")
               }
            }
         }
      }
   }

   // Add album
   if (showAddAlbumDialog) {
      AddAlbumDialog(
         onDismiss = { showAddAlbumDialog = false },
         onAddAlbum = { newAlbum ->
            vinylCollectionViewModel.addAlbum(newAlbum)
            showAddAlbumDialog = false
         },
         discogsViewModel = discogsViewModel
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
               onDelete = { onDeleteAlbum(album) }
            )
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
         .clickable { onClick() },
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
   ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {

         AsyncImage(
            model = album.imageUri,
            contentDescription = "Album Cover",
            modifier = Modifier
               .fillMaxWidth()
               .aspectRatio(1f)
               .clip(RoundedCornerShape(8.dp))
               .background(Color.Gray), // neutral background if empty
            contentScale = ContentScale.Crop
         )

         Spacer(modifier = Modifier.height(8.dp))
         Text(text = album.name, style = MaterialTheme.typography.bodyLarge, maxLines = 1)
         Text(text = album.artist, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
      }
   }
}

@Composable
fun VinylDetailsPopup(album: Album, onDismiss: () -> Unit, onDelete: () -> Unit) {
   var showConfirmDelete by remember { mutableStateOf(false) }

   if (showConfirmDelete) {
      AlertDialog(
         onDismissRequest = { showConfirmDelete = false },
         title = { Text(text = stringResource(R.string.deletion_confirm)) },
         text = { Text(text = stringResource(R.string.confirm_delete_album, album.name)) },
         confirmButton = {
            Button(onClick = {
               onDelete() // Delete album
               showConfirmDelete = false
               onDismiss() // Close popup
            }) { Text(text = stringResource(R.string.delete)) }
         },
         dismissButton = {
            Button(onClick = { showConfirmDelete = false }) {
               Text(text = stringResource(R.string.cancel))
            }
         }
      )
   }

   AlertDialog(
      onDismissRequest = { onDismiss() },
      confirmButton = {
         Button(onClick = { showConfirmDelete = true }) {
            Text(text = stringResource(R.string.delete))
         }
      },
      title = {
         Text(text = album.name)
      },
      text = {
         Column {
            AsyncImage(
               model = album.imageUri,
               contentDescription = "Album Cover",
               modifier = Modifier
                  .fillMaxWidth()
                  .aspectRatio(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color.Gray), // Simple background if no image
               contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(R.string.artistVar, album.artist))
            Text(text = stringResource(R.string.yearVar, album.releaseYear))
            Text(text = stringResource(R.string.genreVar, album.genre))
            //Text("Market Value: ${album.marketValue ?: "N/A"}")
         }
      }
   )
}

@Composable
fun AddAlbumDialog(
   onDismiss: () -> Unit,
   onAddAlbum: (Album) -> Unit,
   discogsViewModel: DiscogsViewModel
) {
   // Input states
   var albumName by remember { mutableStateOf("") }
   var artist by remember { mutableStateOf("") }
   var releaseYear by remember { mutableStateOf("") }
   var genre by remember { mutableStateOf("") }
   var albumCoverUri by remember { mutableStateOf<Uri?>(null) }
   var showSuggestions by remember { mutableStateOf(false) } // For dropdown
   val context = LocalContext.current
   var albumCoverUrl by remember { mutableStateOf("") } // For API image
   var marketValue by remember { mutableStateOf("") }
   val searchResults = discogsViewModel.searchResults.value

   // Image picker
   val imagePickerLauncher = rememberLauncherForActivityResult(
      contract = ActivityResultContracts.GetContent()
   ) { uri: Uri? -> albumCoverUri = uri }

   Dialog(onDismissRequest = { onDismiss() }) {
      Surface(
         shape = RoundedCornerShape(16.dp),
         color = MaterialTheme.colorScheme.surface,
         modifier = Modifier.padding(16.dp)
      ) {
         Column(modifier = Modifier.padding(16.dp)) {
            Text(text = stringResource(R.string.add_entry), style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            // Album Title (Searchable)
            Column {
               TextField(
                  value = albumName,
                  onValueChange = {
                     albumName = it
                     if (it.isNotBlank()) {
                        discogsViewModel.searchAlbums(it) // Live API call
                     } else {
                        discogsViewModel.clearResults() // Clear results if input is empty
                     }
                  },
                  label = { Text(text = stringResource(R.string.title)) },
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth(),
                  trailingIcon = {
                     Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Album"
                     )
                  }
               )

               // Dropdown suggestions
               if (searchResults.isNotEmpty()) {
                  Column(
                     modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clip(RoundedCornerShape(8.dp))
                  ) {
                     searchResults.forEach { album ->
                        Text(
                           text = album.title ?: stringResource(R.string.unknown),
                           modifier = Modifier
                              .clickable {
                                 // Split title to get artist and album title
                                 val (parsedArtist, parsedTitle) = splitTitle(album.title)

                                 // Autofill fields
                                 albumName = parsedTitle
                                 artist = parsedArtist
                                 releaseYear = album.year ?: ""
                                 genre = album.genre?.joinToString(", ") ?: ""

                                 albumCoverUrl = album.coverImage ?: ""
                                 //marketValue = (album.lowestPrice ?: 0.0).toDouble()
                                 //Log.d("MoneyDebug", "Market value: ${album.lowestPrice}")
                                 discogsViewModel.clearResults()
                              }
                              .padding(8.dp)
                        )
                     }
                  }
               }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Other fields
            TextField(value = artist, onValueChange = { artist = it },
               label = { Text(text = stringResource(R.string.artist)) },
               singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = releaseYear, onValueChange = { releaseYear = it },
               label = { Text(text = stringResource(R.string.release_year)) },
               singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = genre, onValueChange = { genre = it },
               label = { Text(text = stringResource(R.string.genre)) },
               singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = marketValue, onValueChange = { marketValue = it },
               label = { Text("Price: ") },
               singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            // Image selection and preview
            Row(
               verticalAlignment = Alignment.CenterVertically,
               modifier = Modifier.fillMaxWidth()
            ) {
               // Button to upload or replace the image
               IconButton(
                  onClick = { imagePickerLauncher.launch("image/*") },
                  modifier = Modifier
                     .size(50.dp)
                     .clip(RoundedCornerShape(12.dp))
                     .background(MaterialTheme.colorScheme.primaryContainer)
               ) {
                  Icon(
                     imageVector = Icons.Default.Add,
                     contentDescription = "Upload Cover",
                     tint = MaterialTheme.colorScheme.onPrimaryContainer
                  )
               }
               Spacer(modifier = Modifier.width(8.dp))

               // Preview either user-uploaded image or Discogs image
               val previewImage = albumCoverUri?.let { uri -> uri } ?: albumCoverUrl.takeIf { it.isNotBlank() }

               if (previewImage != null) {
                  AsyncImage(
                     model = previewImage,
                     contentDescription = "Album Cover Preview",
                     modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                     contentScale = ContentScale.Crop
                  )
               } else {
                  Text(text = stringResource(R.string.upload_cover), style = MaterialTheme.typography.bodyLarge)
               }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
               Button(onClick = { onDismiss() }) { Text(text = stringResource(R.string.cancel)) }
               Button(onClick = {
                  if (albumName.isNotBlank()) {
                     val finalImageUri = albumCoverUri?.toString() ?: albumCoverUrl // Use uploaded image if available, otherwise API image
                     onAddAlbum(
                        Album(
                           name = albumName,
                           artist = artist,
                           releaseYear = releaseYear,
                           genre = genre,
                           imageUri = finalImageUri, // now dynamically chosen
                           marketValue = (marketValue).toDouble()
                        )
                     )
                     onDismiss()
                  }
               }) { Text(text = stringResource(R.string.add)) }
            }
         }
      }
   }
}

fun splitTitle(title: String?): Pair<String, String> { // Helper for getting title and artist
   if (title.isNullOrEmpty()) return "" to ""

   val parts = title.split(" - ", limit = 2)
   val artist = parts.getOrNull(0)?.trim() ?: ""
   val albumTitle = parts.getOrNull(1)?.trim() ?: title // Fall back to whole title if no split found

   return artist to albumTitle
}

data class Album(
   val name: String,
   val artist: String,
   val releaseYear: String,
   val genre: String,
   val imageUri: String,
   val marketValue: Double
)
