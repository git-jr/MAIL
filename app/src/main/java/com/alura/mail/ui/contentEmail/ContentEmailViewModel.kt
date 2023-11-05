package com.alura.mail.ui.contentEmail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alura.mail.mlkit.EntityExtractor
import com.alura.mail.mlkit.Message
import com.alura.mail.mlkit.ResponseGenerator
import com.alura.mail.mlkit.TextTranslator
import com.alura.mail.model.Language
import com.alura.mail.samples.EmailDao
import com.alura.mail.ui.navigation.emailIdArgument
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ContentEmailViewModel @Inject constructor(
    private val textTranslator: TextTranslator,
    private val responseGenerator: ResponseGenerator,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val emailDao = EmailDao()

    private val emailId: String = checkNotNull(savedStateHandle[emailIdArgument])

    private val _uiState = MutableStateFlow(ContentEmailUiState())
    var uiState = _uiState.asStateFlow()

    init {
        loadEmail()

        viewModelScope.launch {
//            delay(1000)
            loadEntities()
            loadSmartSuggestions()
        }
    }

    private fun loadSmartSuggestions() {
        _uiState.value.selectedEmail?.let { email ->
            val conversation = mutableListOf(
                Message(
                    content = email.content,
                    isLocalUser = false
                )
            )

            email.replies.forEach { reply ->
                conversation.add(
                    Message(
                        content = reply.content,
                        isLocalUser = email.user.name != reply.user.name
                    )
                )
            }


            responseGenerator.generateResponse(
                conversation,
                onSuccess = {
                    Log.i("loadSmartSuggestions", "Sugestões: $it")
                    _uiState.value = _uiState.value.copy(
                        suggestions = _uiState.value.suggestions
                                + responseGenerator.messageToSuggestionAction(it)
                    )
                    loadSmartActions()
                },
                onFailure = {
                    loadSmartActions()
                }
            )
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
    }

    private fun loadSmartActions() {
        val entityExtraction = EntityExtractor()
        entityExtraction.extractSuggestion(
            text = _uiState.value.selectedEmail?.content ?: return,
            onSuccess = {
                _uiState.value = _uiState.value.copy(
                    suggestions = _uiState.value.suggestions
                            + entityExtraction.entityToSuggestionAction(it)
                )
            }
        )
    }

//    private fun loadReplies() {
//        _uiState.value = _uiState.value.copy(
//            replies = emailDao.getEmails().take(3)
//        )
//    }

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
        _uiState.value.selectedEmail?.let { email ->
            _uiState.value = _uiState.value.copy(
                selectedEmail = email.copy(
                    replies = email.replies + EmailDao().mountLocalEmail(text)
                )
            )
        }

    }

    fun setSelectSuggestion(suggestion: Suggestion) {
        _uiState.value = _uiState.value.copy(
            selectedSuggestion = suggestion
        )
    }

}


