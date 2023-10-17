package com.alura.mail.ui.contentEmail

import com.alura.mail.model.Language

data class TranslateSettingsUiState(
    val languages: List<Language> = emptyList(),
    val downloadedLanguages: List<Language> = emptyList(),
    val notDownloadedLanguages: List<Language> = emptyList(),
    val showDownloadLanguageDialog: Boolean = false,
    val showDeleteLanguageDialog: Boolean = false,
    val selectedLanguage: Language? = null,
)

