package com.zybooks.pizzaparty

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

class VinylCollectionViewModel : ViewModel() {
    private val _albumList = mutableStateOf<List<Album>>(emptyList())
    val albumList: State<List<Album>> = _albumList

    fun addAlbum(album: Album) {
        _albumList.value += album
    }

    fun deleteAlbum(album: Album) {
        _albumList.value = _albumList.value.filter { it != album }
    }
}
