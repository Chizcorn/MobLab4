package com.example.moblab4

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetroitViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsDataStore = SettingsDataStore(application)

    private val _uiState = MutableStateFlow(DetroitUiState())
    val uiState: StateFlow<DetroitUiState> = _uiState.asStateFlow()

    val isDarkMode: StateFlow<Boolean> = settingsDataStore.darkModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val currentLanguage: StateFlow<String> = settingsDataStore.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    init {
        viewModelScope.launch {
            settingsDataStore.darkModeFlow.collect { darkMode ->
                _uiState.update { it.copy(darkMode = darkMode) }
            }
        }
        viewModelScope.launch {
            settingsDataStore.languageFlow.collect { language ->
                _uiState.update { it.copy(language = language) }
            }
        }
    }

    fun selectDrawerItem(item: DrawerItem) {
        _uiState.update { it.copy(selectedDrawerItem = item) }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setDarkMode(enabled)
        }
    }

    fun setLanguage(languageCode: String) {
        viewModelScope.launch {
            settingsDataStore.setLanguage(languageCode)
        }
    }
}

class DetroitViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetroitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetroitViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class DetroitUiState(
    val selectedDrawerItem: DrawerItem = DrawerItem.Categories,
    val darkMode: Boolean = false,
    val language: String = "en"
)

sealed class DrawerItem(val route: String, val titleResId: Int) {
    data object Categories : DrawerItem("categories", R.string.drawer_categories)
    data object About : DrawerItem("about", R.string.drawer_about)
    data object Settings : DrawerItem("settings", R.string.drawer_settings)
}