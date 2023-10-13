package com.alura.mail.ui.contentEmail

data class TranslateSettingsUiState(
    val languages: List<Language> = emptyList(),
    val downloadedLanguages: List<Language> = emptyList(),
    val notDownloadedLanguages: List<Language> = emptyList(),
)

data class Language(
    val id: Int,
    val name: String,
    val downloadState: DownloadState,
)

enum class DownloadState {
    DOWNLOADED,
    DOWNLOADING,
    NOT_DOWNLOADED,
}