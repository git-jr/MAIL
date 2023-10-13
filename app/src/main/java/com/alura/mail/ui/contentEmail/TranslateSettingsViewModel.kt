package com.alura.mail.ui.contentEmail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class TranslateSettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TranslateSettingsUiState())
    var uiState = _uiState.asStateFlow()

    init {
        loadLanguages()
    }

    private fun loadLanguages() {

        val languages = listOf(
            Language(
                id = 1,
                name = "Português",
                downloadState = DownloadState.DOWNLOADED,
                size = "81 MB"
            ),
            Language(
                id = 2,
                name = "Inglês",
                downloadState = DownloadState.DOWNLOADED,
                size = "82 MB"
            ),
            Language(
                id = 3,
                name = "Espanhol",
                downloadState = DownloadState.NOT_DOWNLOADED,
                size = "83 MB"
            ),
            Language(
                id = 4,
                name = "Francês",
                downloadState = DownloadState.NOT_DOWNLOADED,
                size = "78 MB"
            ),
            Language(
                id = 5,
                name = "Alemão",
                downloadState = DownloadState.DOWNLOADED,
                size = "79 MB"
            ),
            Language(
                id = 6,
                name = "Italiano",
                downloadState = DownloadState.NOT_DOWNLOADED,
                size = "56 MB"
            ),
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                languages = languages,
                downloadedLanguages = languages.filter { it.downloadState == DownloadState.DOWNLOADED },
                notDownloadedLanguages = languages.filter { it.downloadState == DownloadState.NOT_DOWNLOADED || it.downloadState == DownloadState.DOWNLOADING },
            )
        }
    }


    fun changeShowDownloadLanguageDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(
            showDownloadLanguageDialog = show
        )
    }

    fun changeShowDeleteLanguageDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(
            showDeleteLanguageDialog = show
        )
    }

    fun changeDownloadState(language: Language, downloadState: DownloadState) {
        val languages = _uiState.value.languages.toMutableList()
        val languageIndex = languages.indexOfFirst { it.id == language.id }
        languages[languageIndex] = language.copy(downloadState = downloadState)

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                languages = languages,
                downloadedLanguages = languages.filter { it.downloadState == DownloadState.DOWNLOADED },
                notDownloadedLanguages = languages.filter { it.downloadState == DownloadState.NOT_DOWNLOADED || it.downloadState == DownloadState.DOWNLOADING },
            )
        }
    }

    fun selectedLanguage(language: Language) {
        _uiState.value = _uiState.value.copy(
            selectedLanguage = language
        )
    }

    fun downloadLanguage(language: Language) {
        changeDownloadState(
            language = language,
            downloadState = DownloadState.DOWNLOADING
        )

        viewModelScope.launch {
            delay(2000)
            changeDownloadState(
                language = language,
                downloadState = DownloadState.DOWNLOADED
            )
        }
    }

}