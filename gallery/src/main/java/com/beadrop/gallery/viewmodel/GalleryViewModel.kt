package com.beadrop.gallery.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beadrop.core.domain.model.MediaAlbum
import com.beadrop.core.domain.model.MediaFilter
import com.beadrop.core.domain.model.MediaItem
import com.beadrop.core.domain.model.MediaSortOrder
import com.beadrop.storage.mediastore.MediaStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val mediaStoreManager: MediaStoreManager,
) : ViewModel() {

    private val _mediaItems = MutableStateFlow<List<MediaItem>>(emptyList())
    val mediaItems: StateFlow<List<MediaItem>> = _mediaItems.asStateFlow()

    private val _albums = MutableStateFlow<List<MediaAlbum>>(emptyList())
    val albums: StateFlow<List<MediaAlbum>> = _albums.asStateFlow()

    private val _currentFilter = MutableStateFlow(MediaFilter.ALL)
    val currentFilter: StateFlow<MediaFilter> = _currentFilter.asStateFlow()

    private val _sortOrder = MutableStateFlow(MediaSortOrder.DATE_DESC)
    val sortOrder: StateFlow<MediaSortOrder> = _sortOrder.asStateFlow()

    private val _selectedItems = MutableStateFlow<Set<Long>>(emptySet())
    val selectedItems: StateFlow<Set<Long>> = _selectedItems.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredItems: StateFlow<List<MediaItem>> = combine(
        _mediaItems,
        _searchQuery,
    ) { items, query ->
        if (query.isBlank()) items
        else items.filter { it.displayName.contains(query, ignoreCase = true) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    init {
        loadMedia()
        observeChanges()
    }

    fun loadMedia() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _mediaItems.value = mediaStoreManager.queryAllMedia(
                    sortOrder = _sortOrder.value,
                    filter = _currentFilter.value,
                )
                _albums.value = mediaStoreManager.queryAlbums()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun observeChanges() {
        viewModelScope.launch {
            mediaStoreManager.observeMediaChanges().collect {
                loadMedia()
            }
        }
    }

    fun setFilter(filter: MediaFilter) {
        _currentFilter.value = filter
        loadMedia()
    }

    fun setSortOrder(order: MediaSortOrder) {
        _sortOrder.value = order
        loadMedia()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleSelection(mediaId: Long) {
        val current = _selectedItems.value.toMutableSet()
        if (current.contains(mediaId)) {
            current.remove(mediaId)
        } else {
            current.add(mediaId)
        }
        _selectedItems.value = current
        _isSelectionMode.value = current.isNotEmpty()
    }

    fun selectAll() {
        _selectedItems.value = _mediaItems.value.map { it.id }.toSet()
        _isSelectionMode.value = true
    }

    fun clearSelection() {
        _selectedItems.value = emptySet()
        _isSelectionMode.value = false
    }

    fun deleteSelected() {
        viewModelScope.launch {
            val itemsToDelete = _mediaItems.value.filter { it.id in _selectedItems.value }
            mediaStoreManager.deleteMedia(itemsToDelete)
            clearSelection()
            loadMedia()
        }
    }
}
