package com.pnc.jetpackcomposedemos.core.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
): ViewModel() {

    //the repo gives us a cold flow from the datastore
    //turn that into the state the ui can read
    //(the initial value will show until the dataStore file is read)
    val settings: StateFlow<AppSettings> = repository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppSettings()
    )

    fun setThemeMode(mode:ThemeMode){
        viewModelScope.launch{
            repository.setThemeMode(mode)
        }
    }

    fun setShowArtistLocation(show:Boolean){
        viewModelScope.launch{
            repository.setShowArtistLocation(show)
        }
    }


}