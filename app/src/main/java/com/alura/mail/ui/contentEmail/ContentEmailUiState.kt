package com.alura.mail.ui.contentEmail

import com.alura.mail.model.Email
import com.alura.mail.model.Language

data class ContentEmailUiState(
    val selectedEmail: Email? = null,
    val originalContent: String? = null,
    val originalSubject: String? = null,
    val localLanguage: Language? = null,
    val languageIdentified: Language? = null,
    val needTranslate: Boolean = false,
    val translatedState: TranslatedState = TranslatedState.NOT_TRANSLATED,
    val showDownloadLanguageDialog: Boolean = false,
)

enum class TranslatedState {
    TRANSLATED,
    NOT_TRANSLATED,
    TRANSLATING
}