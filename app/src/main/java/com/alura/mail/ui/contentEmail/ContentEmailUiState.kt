package com.alura.mail.ui.contentEmail

import com.alura.mail.model.Email
import com.alura.mail.model.Language

data class ContentEmailUiState(
    val selectedEmail: Email? = null,
    val originalContent: String? = null,
    val originalSubject: String? = null,
    val localLanguage: Language? = null,
    val languageIdentified: Language? = null,
    val canBeTranslate: Boolean = false,
    val translatedState: TranslatedState = TranslatedState.NOT_TRANSLATED,
    val showDownloadLanguageDialog: Boolean = false,
    val showTranslateButton: Boolean = true,
    val suggestions: List<Suggestion> = emptyList(),
    val selectedSuggestion: Suggestion? = null,
    val rangeList: List<IntRange> = emptyList()

)

enum class TranslatedState {
    TRANSLATED,
    NOT_TRANSLATED,
    TRANSLATING
}


data class Suggestion(
    val text: String,
    val action: SuggestionAction = SuggestionAction.SMART_REPLY,
    val icon: Int? = null
)

data class Entity(
    val text: String,
    val type: String,
    val start: Int,
    val end: Int
)

enum class SuggestionAction {
    SMART_REPLY,
    ADDRESS,
    DATE_TIME,
    EMAIL,
    PHONE_NUMBER,
    URL,
    IBAN,
    ISBN,
    PAYMENT_CARD_NUMBER,
    TRACKING_NUMBER,
    FLIGHT_NUMBER
}