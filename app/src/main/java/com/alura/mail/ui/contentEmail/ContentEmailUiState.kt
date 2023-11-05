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
    val rangeList: List<IntRange> = emptyList(),
    val replies: List<Email> = emptyList()

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

enum class SuggestionAction {
    SMART_REPLY, // 0
    ADDRESS, // 1
    DATE_TIME, // 2
    EMAIL, // 3
    FLIGHT_NUMBER, // 4
    IBAN, // 5
    ISBN, // 6
    PAYMENT_CARD_NUMBER, // 7
    PHONE_NUMBER,  // 8
    TRACKING_NUMBER, // 9
    URL, // 10
    MONEY, // 11
}

