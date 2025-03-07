package com.zybooks.pizzaparty

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

class VinylCollectionViewModel : ViewModel() {

    private val _albumList = mutableStateOf(mutableListOf<Album>())
    val albumList: State<MutableList<Album>> = _albumList

    fun addAlbum(album: Album) {
        _albumList.value.add(album)
    }
}
