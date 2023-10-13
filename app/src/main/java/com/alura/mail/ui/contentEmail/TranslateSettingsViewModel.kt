package com.alura.mail.ui.contentEmail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class TranslateSettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        TranslateSettingsUiState()
    )
    var uiState = _uiState.asStateFlow()

    init {
        loadLanguages()
    }

    private fun loadLanguages() {

        val languages = listOf(
            Language(
                id = 1,
                name = "Português",
                downloadState = DownloadState.DOWNLOADED
            ),
            Language(
                id = 2,
                name = "Inglês",
                downloadState = DownloadState.DOWNLOADED
            ),
            Language(
                id = 3,
                name = "Espanhol",
                downloadState = DownloadState.NOT_DOWNLOADED
            ),
            Language(
                id = 4,
                name = "Francês",
                downloadState = DownloadState.NOT_DOWNLOADED
            ),
            Language(
                id = 5,
                name = "Alemão",
                downloadState = DownloadState.DOWNLOADED
            ),
            Language(
                id = 6,
                name = "Italiano",
                downloadState = DownloadState.NOT_DOWNLOADED
            ),
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                languages = languages,
                downloadedLanguages = languages.filter { it.downloadState == DownloadState.DOWNLOADED },
                notDownloadedLanguages = languages.filter { it.downloadState == DownloadState.NOT_DOWNLOADED },
            )
        }
    }

}