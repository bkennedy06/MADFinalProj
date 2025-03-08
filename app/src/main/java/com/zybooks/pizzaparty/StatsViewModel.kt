package com.zybooks.pizzaparty

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

class StatsViewModel : ViewModel() {
    private val _totalAlbums = mutableStateOf(0)
    val totalAlbums: State<Int> = _totalAlbums

    private val _totalArtists = mutableStateOf(0)
    val totalArtists: State<Int> = _totalArtists

    private val _mostOwnedArtist = mutableStateOf("n/a")
    val mostOwnedArtist: State<String> = _mostOwnedArtist

    private val _totalMarketValue = mutableStateOf(0.0)
    val totalMarketValue: State<Double> = _totalMarketValue

    private val _oldestAlbum = mutableStateOf("Unknown")
    val oldestAlbum: State<String> = _oldestAlbum

    private val _mostExpensiveAlbum = mutableStateOf("Unknown ($0.00)")
    val mostExpensiveAlbum: State<String> = _mostExpensiveAlbum

    fun updateStats() {
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(navController: NavController, statsViewModel: StatsViewModel = viewModel()) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            StatsRow("Total Albums Owned: ${statsViewModel.totalAlbums.value}")
            StatsRow("Total Artists: ${statsViewModel.totalArtists.value}")
            StatsRow("Most Owned Artist: ${statsViewModel.mostOwnedArtist.value}")
            StatsRow("Total Market Value: $${statsViewModel.totalMarketValue.value}")
            StatsRow("Oldest Album: ${statsViewModel.oldestAlbum.value}")
            StatsRow("Most Expensive Album: ${statsViewModel.mostExpensiveAlbum.value}")
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
