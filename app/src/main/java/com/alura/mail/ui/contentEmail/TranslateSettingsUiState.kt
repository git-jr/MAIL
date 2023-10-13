package com.alura.mail.ui.contentEmail

data class TranslateSettingsUiState(
    val languages: List<Language> = emptyList(),
    val downloadedLanguages: List<Language> = emptyList(),
    val notDownloadedLanguages: List<Language> = emptyList(),
    val showDownloadLanguageDialog: Boolean = false,
    val showDeleteLanguageDialog: Boolean = false,
    val selectedLanguage: Language? = null,
)

data class Language(
    val id: Int,
    val name: String,
    val downloadState: DownloadState,
    val size: String
)

enum class DownloadState {
    DOWNLOADED,
    DOWNLOADING,
    NOT_DOWNLOADED,
}