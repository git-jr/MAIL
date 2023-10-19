package com.alura.mail.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alura.mail.mlkit.TextTranslate
import com.alura.mail.model.DownloadState
import com.alura.mail.model.LanguageModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TranslateSettingsViewModel @Inject constructor(
    private val textTranslate: TextTranslate
) : ViewModel() {

    private val _uiState = MutableStateFlow(TranslateSettingsUiState())
    var uiState = _uiState.asStateFlow()

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        // carregar todos idiomas primeiro, depois carrgar os baixados
        // e fazer filtro, depois mudar estado de loading para loaded

        // carregar todos os idiomas disponíveis
        _uiState.value = _uiState.value.copy(
            allLanguageModels = textTranslate.getAllModels().sortedBy { it.name }
        )
        // carregar os idiomas baixados e de lá já faz a filtragem
        loadDownloadedLanguages()

    }

    private fun filterAllModelNotDownloaded(downloadedModels: List<LanguageModel>): List<LanguageModel> {
        return _uiState.value.allLanguageModels.filter { languageModel ->
            downloadedModels.none { it.id == languageModel.id }
        }
    }

    private fun loadDownloadedLanguages() {
        textTranslate.getDownloadedModels(
            onSuccessful = { downloadedModels ->
                if (downloadedModels.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        downloadedLanguageModels = downloadedModels.sortedBy { it.name },
                        notDownloadedLanguageModels = filterAllModelNotDownloaded(downloadedModels),
                        loadModelsState = AppState.Loaded
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        loadModelsState = AppState.Error
                    )
                }
            }
        )
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
        val languages = _uiState.value.allLanguageModels.toMutableList()
        val languageIndex = languages.indexOfFirst { it.id == languageModel.id }
        languages[languageIndex] = languageModel.copy(downloadState = downloadState)

        updateLanguages(languages)
    }

    private fun updateLanguages(languageModels: List<LanguageModel>) {
        val sortedLanguageModels = languageModels.sortedBy { it.name }
        _uiState.value = _uiState.value.copy(
            allLanguageModels = sortedLanguageModels,
            downloadedLanguageModels = sortedLanguageModels.filter { it.downloadState == DownloadState.DOWNLOADED },
            notDownloadedLanguageModels = sortedLanguageModels.filter { it.downloadState == DownloadState.NOT_DOWNLOADED || it.downloadState == DownloadState.DOWNLOADING },
            loadModelsState = AppState.Loaded
        )
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


    private fun loadLanguagesOld() {

        val languageModels = listOf(
            LanguageModel(
                id = UUID.randomUUID().toString(),
                name = "Português",
                downloadState = DownloadState.DOWNLOADED,
                size = "81 MB"
            ),
            LanguageModel(
                id = UUID.randomUUID().toString(),
                name = "Inglês",
                downloadState = DownloadState.DOWNLOADED,
                size = "82 MB"
            ),
            LanguageModel(
                id = UUID.randomUUID().toString(),
                name = "Espanhol",
                downloadState = DownloadState.NOT_DOWNLOADED,
                size = "83 MB"
            ),
            LanguageModel(
                id = UUID.randomUUID().toString(),
                name = "Francês",
                downloadState = DownloadState.NOT_DOWNLOADED,
                size = "78 MB"
            ),
            LanguageModel(
                id = UUID.randomUUID().toString(),
                name = "Alemão",
                downloadState = DownloadState.DOWNLOADED,
                size = "79 MB"
            ),
            LanguageModel(
                id = UUID.randomUUID().toString(),
                name = "Italiano",
                downloadState = DownloadState.NOT_DOWNLOADED,
                size = "56 MB"
            ),
        )

        updateLanguages(languageModels)
    }

}