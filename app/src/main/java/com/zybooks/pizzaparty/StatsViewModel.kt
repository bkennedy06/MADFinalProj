package com.zybooks.pizzaparty

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

class StatsViewModel : ViewModel() {
    private val _totalAlbums = mutableStateOf(0)
    val totalAlbums: State<Int> = _totalAlbums

    private val _totalArtists = mutableStateOf(0)
    val totalArtists: State<Int> = _totalArtists

    private val _mostOwnedArtist = mutableStateOf("N/A")
    val mostOwnedArtist: State<String> = _mostOwnedArtist

    private val _totalMarketValue = mutableDoubleStateOf(0.0)
    val totalMarketValue: State<Double> = _totalMarketValue

    private val _oldestAlbum = mutableStateOf("Unknown")
    val oldestAlbum: State<String> = _oldestAlbum

    private val _mostExpensiveAlbum = mutableStateOf("Unknown ($0.00)")
    val mostExpensiveAlbum: State<String> = _mostExpensiveAlbum

    fun updateStats(albums: List<Album>) {
        _totalAlbums.value = albums.size
        _totalArtists.value = albums.map { it.artist }.distinct().size

        // Most Owned Artist
        val artistFrequency = albums.groupingBy { it.artist }.eachCount()
        _mostOwnedArtist.value = artistFrequency.maxByOrNull { it.value }?.key ?: "N/A"

        // Oldest Album
        _oldestAlbum.value = albums.minByOrNull { it.releaseYear.toIntOrNull() ?: Int.MAX_VALUE }?.name ?: "Unknown"

        // Calculate Market Value (Using API Price)
        val totalMarket = albums.sumOf { it.marketValue }
        val mostExpensive = albums.maxByOrNull { it.marketValue }

        _totalMarketValue.doubleValue = totalMarket
        _mostExpensiveAlbum.value =
            mostExpensive?.let { "${it.name} ($${"%.2f".format(it.marketValue)})" } ?: "Unknown ($0.00)"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    navController: NavController,
    statsViewModel: StatsViewModel = viewModel(),
    vinylCollectionViewModel: VinylCollectionViewModel = viewModel()
) {
    val albumList by vinylCollectionViewModel.albumList

    LaunchedEffect(albumList) {
        statsViewModel.updateStats(albumList)
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.statistics)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { statsViewModel.updateStats(albumList) }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh Stats")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatsRow(stringResource(R.string.total_albums, statsViewModel.totalAlbums.value))
            StatsRow(stringResource(R.string.total_artists, statsViewModel.totalArtists.value))
            StatsRow(stringResource(R.string.most_owned_artist, statsViewModel.mostOwnedArtist.value))
            StatsRow(stringResource(R.string.total_market_value, "%.2f".format(statsViewModel.totalMarketValue.value)))
            StatsRow(stringResource(R.string.oldest_album, statsViewModel.oldestAlbum.value))
            StatsRow(stringResource(R.string.most_expensive_album, statsViewModel.mostExpensiveAlbum.value))
        }
    }
}

@Composable
fun StatsRow(label: String) {
    Column {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Divider(color = Color.Gray.copy(alpha = 0.2f), thickness = 1.dp)
    }
}
