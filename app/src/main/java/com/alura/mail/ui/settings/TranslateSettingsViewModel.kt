package com.alura.mail.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alura.mail.model.DownloadState
import com.alura.mail.model.LanguageModel
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

        val languageModels = listOf(
            LanguageModel(
                id = 1,
                name = "Português",
                downloadState = DownloadState.DOWNLOADED,
                size = "81 MB"
            ),
            LanguageModel(
                id = 2,
                name = "Inglês",
                downloadState = DownloadState.DOWNLOADED,
                size = "82 MB"
            ),
            LanguageModel(
                id = 3,
                name = "Espanhol",
                downloadState = DownloadState.NOT_DOWNLOADED,
                size = "83 MB"
            ),
            LanguageModel(
                id = 4,
                name = "Francês",
                downloadState = DownloadState.NOT_DOWNLOADED,
                size = "78 MB"
            ),
            LanguageModel(
                id = 5,
                name = "Alemão",
                downloadState = DownloadState.DOWNLOADED,
                size = "79 MB"
            ),
            LanguageModel(
                id = 6,
                name = "Italiano",
                downloadState = DownloadState.NOT_DOWNLOADED,
                size = "56 MB"
            ),
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                languageModels = languageModels,
                downloadedLanguageModels = languageModels.filter { it.downloadState == DownloadState.DOWNLOADED },
                notDownloadedLanguageModels = languageModels.filter { it.downloadState == DownloadState.NOT_DOWNLOADED || it.downloadState == DownloadState.DOWNLOADING },
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

    fun changeDownloadState(languageModel: LanguageModel, downloadState: DownloadState) {
        val languages = _uiState.value.languageModels.toMutableList()
        val languageIndex = languages.indexOfFirst { it.id == languageModel.id }
        languages[languageIndex] = languageModel.copy(downloadState = downloadState)

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                languageModels = languages,
                downloadedLanguageModels = languages.filter { it.downloadState == DownloadState.DOWNLOADED },
                notDownloadedLanguageModels = languages.filter { it.downloadState == DownloadState.NOT_DOWNLOADED || it.downloadState == DownloadState.DOWNLOADING },
            )
        }
    }

    fun selectedLanguage(languageModel: LanguageModel) {
        _uiState.value = _uiState.value.copy(
            selectedLanguageModel = languageModel
        )
    }

    fun downloadLanguage(languageModel: LanguageModel) {
        changeDownloadState(
            languageModel = languageModel,
            downloadState = DownloadState.DOWNLOADING
        )

        viewModelScope.launch {
            delay(2000)
            changeDownloadState(
                languageModel = languageModel,
                downloadState = DownloadState.DOWNLOADED
            )
        }
    }

}