package com.alura.mail.ui.contentEmail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alura.mail.mlkit.DOWNLOAD_TAG
import com.alura.mail.mlkit.TextTranslate
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
    private val textTranslate: TextTranslate,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val emailDao = EmailDao()

    private val emailId: String = checkNotNull(savedStateHandle[emailIdArgument])

    private val _uiState = MutableStateFlow(ContentEmailUiState())
    var uiState = _uiState.asStateFlow()

    init {
        loadEmail()
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
            textTranslate.identifyLanguage(
                email.content,
                onSuccessful = { language ->
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
                }
            )
        }
    }

    private fun identifyLocalLanguage() {
        val languageCode = Locale.getDefault().language
        val languageName = Locale.getDefault().displayLanguage

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
        val languageCode = Locale.getDefault().language

        // Podemos chamar esse metodo direto aqui sem a necessidade de verificar se o idioma já foi baixado ou não
        // porque o metodo automaticamente só baixa o modelo se ele ainda não estiver baixado
        // motrar até o teste com a internet desligada nesse ponto
        textTranslate.downloadModel(
            languageCode,
            onSuccessful = {
                Log.i(DOWNLOAD_TAG, "Modelo padrão baixado com sucesso")
            },
            onFailure = {
                Log.i(DOWNLOAD_TAG, "Falha ao baixar o modelo padrão")
            }
        )
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
        if (_uiState.value.translatedState == TranslatedState.TRANSLATED) {
            _uiState.value.selectedEmail?.let { email ->
                _uiState.value = _uiState.value.copy(
                    selectedEmail = email.copy(
                        content = _uiState.value.originalContent.toString(),
                        subject = _uiState.value.originalSubject.toString()
                    ),
                    translatedState = TranslatedState.NOT_TRANSLATED
                )
            }
        } else {
            setTranslateState(TranslatedState.TRANSLATING)
            val languageIdentified = _uiState.value.languageIdentified?.code ?: return
            textTranslate.verifyModelDownloaded(
                languageIdentified,
                onSuccessful = {
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

    fun setTranslateState(state: TranslatedState) {
        _uiState.value = _uiState.value.copy(
            translatedState = state
        )
    }

    private fun translateEmail() {
        _uiState.value.selectedEmail?.let { email ->
            _uiState.value.languageIdentified?.let { languageIdentified ->
                _uiState.value.localLanguage?.let { localLanguage ->
                    textTranslate.translateText(
                        email.content,
                        targetLanguage = localLanguage.code,
                        sourceLanguage = languageIdentified.code,
                        onSuccessful = { translatedText ->
                            _uiState.value = _uiState.value.copy(
                                selectedEmail = email.copy(
                                    content = translatedText
                                ),
                                translatedState = TranslatedState.TRANSLATED
                            )
                            translateSubject()
                        }
                    )
                }
            }
        }
    }

    private fun translateSubject() {
        _uiState.value.selectedEmail?.let { email ->
            _uiState.value.languageIdentified?.let { languageIdentified ->
                _uiState.value.localLanguage?.let { localLanguage ->
                    textTranslate.translateText(
                        email.subject,
                        targetLanguage = localLanguage.code,
                        sourceLanguage = languageIdentified.code,
                        onSuccessful = { translatedText ->
                            _uiState.value = _uiState.value.copy(
                                selectedEmail = email.copy(
                                    subject = translatedText
                                ),
                            )
                        }
                    )
                }
            }
        }
    }

    fun downloadLanguageModel() {
        val languageIdentified = _uiState.value.languageIdentified?.code ?: return

        textTranslate.downloadModel(
            languageIdentified,
            onSuccessful = {
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
}