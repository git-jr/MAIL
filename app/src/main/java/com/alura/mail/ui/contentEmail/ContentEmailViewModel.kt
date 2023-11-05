package com.alura.mail.ui.contentEmail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alura.mail.R
import com.alura.mail.mlkit.EntityExtractor
import com.alura.mail.mlkit.TextTranslator
import com.alura.mail.model.Email
import com.alura.mail.model.Language
import com.alura.mail.model.User
import com.alura.mail.samples.EmailDao
import com.alura.mail.ui.navigation.emailIdArgument
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ContentEmailViewModel @Inject constructor(
    private val textTranslator: TextTranslator,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val emailDao = EmailDao()

    private val emailId: String = checkNotNull(savedStateHandle[emailIdArgument])

    private val _uiState = MutableStateFlow(ContentEmailUiState())
    var uiState = _uiState.asStateFlow()

    init {
        loadEmail()
        // loadFakeSuggestions()
        loadReplies()

        viewModelScope.launch {
            delay(1000)
            loadEntities()
        }
    }

    private fun loadEntities() {
        val entityExtraction = EntityExtractor()
        entityExtraction.gentRanges(
            text = _uiState.value.selectedEmail?.content ?: return,
            onSuccess = {
                _uiState.value = _uiState.value.copy(
                    rangeList = it
                )

                Log.e("entityExtractor", "Tamanho: ${it.size}")
            }
        )

        entityExtraction.extractSuggestion(
            text = _uiState.value.selectedEmail?.content ?: return,
            onSuccess = {
                _uiState.value = _uiState.value.copy(
                    suggestions = entityExtraction.entityToSuggestionAction(it)
                )
                Log.e("entityExtractor", "Tamanho: ${_uiState.value.suggestions}")
            }
        )
    }

    private fun loadReplies() {
        _uiState.value = _uiState.value.copy(
            replies = emailDao.getEmails().take(3)
        )
    }

    private fun loadFakeSuggestions() {
        viewModelScope.launch {
            val entitySuggestions = listOf(
//                Suggestion("Ir para site", SuggestionAction.URL, R.drawable.ic_link),
//                Suggestion("Ligue", SuggestionAction.PHONE_NUMBER, R.drawable.ic_call),
//                Suggestion("Envie email", SuggestionAction.EMAIL, R.drawable.ic_email),
//                Suggestion("Abrir Maps", SuggestionAction.ADDRESS, R.drawable.ic_location),
                Suggestion(
                    "2019/09/29, let's meet tomorrow at 6pm",
                    SuggestionAction.DATE_TIME,
                    R.drawable.ic_copy
                ),
                Suggestion("Torre Eiffel", SuggestionAction.ADDRESS, R.drawable.ic_location),
                Suggestion(
                    "Rua Vergueiro, 3185 - São Paulo,",
                    SuggestionAction.ADDRESS,
                    R.drawable.ic_location
                ),
                Suggestion("juniorrr77@gmail.com", SuggestionAction.EMAIL, R.drawable.ic_email),
                Suggestion(
                    "https://www.youtube.com/@Paradoxo10",
                    SuggestionAction.URL,
                    R.drawable.ic_link
                ),
                Suggestion("4002-8922", SuggestionAction.PHONE_NUMBER, R.drawable.ic_call),
                Suggestion("IBAN", SuggestionAction.IBAN, R.drawable.ic_copy),
                Suggestion(
                    "Cartão de crédito",
                    SuggestionAction.PAYMENT_CARD_NUMBER,
                    R.drawable.ic_copy
                ),
                Suggestion(
                    "Número de rastreio",
                    SuggestionAction.TRACKING_NUMBER,
                    R.drawable.ic_copy
                ),
            )

            val smartSuggestions = listOf(
                Suggestion("Bom dia"),
                Suggestion("Ok"),
                Suggestion("Combinado"),
            )

            _uiState.value = _uiState.value.copy(
                suggestions = entitySuggestions + smartSuggestions
            )
        }
    }

    private fun loadEmail() {
        viewModelScope.launch {
            val email = emailDao.getEmailById(emailId)
            _uiState.value = _uiState.value.copy(
                selectedEmail = email,
                originalContent = email?.content,
                originalSubject = email?.subject
            )
            identifyEmailLanguage()
            identifyLocalLanguage()
        }
    }

    private fun identifyEmailLanguage() {
        _uiState.value.selectedEmail?.let { email ->
            textTranslator.languageIdentifier(
                email.content,
                onSuccess = { language ->
                    _uiState.value = _uiState.value.copy(
                        languageIdentified = language,
                    )
                    verifyIfNeedTranslate()
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        languageIdentified = null,
                        canBeTranslate = false
                    )
                    verifyIfNeedTranslate()
                }
            )
        }
    }

    private fun identifyLocalLanguage() {
        val languageCode = Locale.getDefault().language
        val languageName = Locale.getDefault().displayLanguage

        Log.i("identifyLocalLanguage", "languageCode: $languageCode")
        Log.i("identifyLocalLanguage", "languageName: $languageName")

        _uiState.value = _uiState.value.copy(
            localLanguage = Language(
                code = languageCode,
                name = languageName
            )
        )
        verifyIfNeedTranslate()
        downloadDefaultLanguageModel()
    }

    private fun downloadDefaultLanguageModel() {
        val localLanguage = Locale.getDefault().language
        textTranslator.downloadModel(localLanguage)
    }

    private fun verifyIfNeedTranslate() {
        val languageIdentified = _uiState.value.languageIdentified
        val localLanguage = _uiState.value.localLanguage

        if (languageIdentified != null && localLanguage != null) {
            if (languageIdentified.code != localLanguage.code) {
                _uiState.value = _uiState.value.copy(
                    canBeTranslate = true
                )
            }
        }
    }

    fun tryTranslateEmail() {
        with(_uiState.value) {
            if (translatedState == TranslatedState.TRANSLATED) {
                selectedEmail?.let { email ->
                    _uiState.value = _uiState.value.copy(
                        selectedEmail = email.copy(
                            content = originalContent.toString(),
                            subject = originalSubject.toString()
                        ),
                        translatedState = TranslatedState.NOT_TRANSLATED
                    )
                }
            } else {
                setTranslateState(TranslatedState.TRANSLATING)
                val languageIdentified = _uiState.value.languageIdentified?.code.toString()

                textTranslator.verifyModelDownloaded(
                    languageIdentified,
                    onSuccess = {
                        translateEmail()
                    },
                    onFailure = {
                        _uiState.value = _uiState.value.copy(
                            showDownloadLanguageDialog = true
                        )
                    }
                )
            }
        }
    }

    fun setTranslateState(state: TranslatedState) {
        _uiState.value = _uiState.value.copy(
            translatedState = state
        )
    }

    private fun translateEmail() {
        with(_uiState.value) {
            selectedEmail?.let { email ->
                languageIdentified?.let { languageIdentified ->
                    localLanguage?.let { localLanguage ->
                        textTranslator.textTranslate(
                            text = email.content,
                            targetLanguage = localLanguage.code,
                            sourceLanguage = languageIdentified.code,
                            onSuccess = { translatedText ->
                                _uiState.value = _uiState.value.copy(
                                    selectedEmail = email.copy(
                                        content = translatedText
                                    ),
                                    translatedState = TranslatedState.TRANSLATED
                                )
                                translateSubject()
                            },
                            onFailure = {

                            }
                        )
                    }
                }
            }
        }
    }

    private fun translateSubject() {
        with(_uiState.value) {
            selectedEmail?.let { email ->
                languageIdentified?.let { languageIdentified ->
                    localLanguage?.let { localLanguage ->
                        textTranslator.textTranslate(
                            email.subject,
                            targetLanguage = localLanguage.code,
                            sourceLanguage = languageIdentified.code,
                            onSuccess = { translatedText ->
                                _uiState.value = _uiState.value.copy(
                                    selectedEmail = email.copy(
                                        subject = translatedText
                                    ),
                                )
                            },
                            onFailure = {}
                        )
                    }
                }
            }
        }
    }

    fun downloadLanguageModel() {
        val languageIdentified = _uiState.value.languageIdentified?.code ?: return
        textTranslator.downloadModel(
            languageIdentified,
            onSuccess = {
                translateEmail()
            },
            onFailure = {
                _uiState.value = _uiState.value.copy(
                    translatedState = TranslatedState.NOT_TRANSLATED
                )
            }
        )
    }

    fun showDownloadLanguageDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(
            showDownloadLanguageDialog = show
        )
    }

    fun hideTranslateButton() {
        _uiState.value = _uiState.value.copy(
            showTranslateButton = false
        )
    }

    fun addReply(text: String) {
        _uiState.value = _uiState.value.copy(
            replies = _uiState.value.replies + Email(
                subject = "",
                content = text,
                id = UUID.randomUUID().toString(),
                time = System.currentTimeMillis(),
                color = 0XFF8F4F24,
                user = User("Você"),
            )
        )
    }

    fun setSelectSuggestion(suggestion: Suggestion) {
        _uiState.value = _uiState.value.copy(
            selectedSuggestion = suggestion
        )
    }

}


