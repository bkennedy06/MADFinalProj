package com.zybooks.pizzaparty

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import kotlinx.coroutines.launch

class DiscogsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DiscogsRepository(application.applicationContext)

    private val _searchResults = mutableStateOf<List<DiscogsAlbumResult>>(emptyList())
    val searchResults: State<List<DiscogsAlbumResult>> = _searchResults

    fun searchAlbums(query: String) {
        viewModelScope.launch {
            _searchResults.value = repository.searchAlbums(query)
        }
    }

    fun clearResults() {
        _searchResults.value = emptyList()
    }
}
