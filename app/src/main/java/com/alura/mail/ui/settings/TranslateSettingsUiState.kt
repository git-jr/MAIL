package com.alura.mail.ui.settings

import com.alura.mail.model.LanguageModel

data class TranslateSettingsUiState(
    val languageModels: List<LanguageModel> = emptyList(),
    val downloadedLanguageModels: List<LanguageModel> = emptyList(),
    val notDownloadedLanguageModels: List<LanguageModel> = emptyList(),
    val showDownloadLanguageDialog: Boolean = false,
    val showDeleteLanguageDialog: Boolean = false,
    val selectedLanguageModel: LanguageModel? = null,
)

