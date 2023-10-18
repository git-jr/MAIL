package com.alura.mail.ui.contentEmail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alura.mail.dao.EmailDao
import com.alura.mail.mlkit.DOWNLOAD_TAG
import com.alura.mail.mlkit.TextTranslate
import com.alura.mail.model.Language
import com.alura.mail.ui.navigation.emailIdArgument
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class ContentEmailViewModel(
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
                originalContent = email?.content
            )
            identifyEmailLanguage()
            identifyLocalLanguage()
        }
    }

    private fun identifyEmailLanguage() {
        _uiState.value.selectedEmail?.let { email ->
            TextTranslate().identifyLanguage(
                email.content,
                onSuccessful = { language ->
                    _uiState.value = _uiState.value.copy(
                        languageIdentified = language,
                    )
                    verifyIfNeedTranslate()
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
        TextTranslate().downloadModel(
            languageCode,
            onSuccessful = {
                Log.i(DOWNLOAD_TAG, "Modelo baixado com sucesso")
            },
            onFailure = {
                Log.i(DOWNLOAD_TAG, "Falha ao baixar o modelo")
            }
        )
    }

    private fun verifyIfNeedTranslate() {
        val languageIdentified = _uiState.value.languageIdentified
        val localLanguage = _uiState.value.localLanguage

        if (languageIdentified != null && localLanguage != null) {
            if (languageIdentified.code != localLanguage.code) {
                _uiState.value = _uiState.value.copy(
                    needTranslate = true
                )
            }
        }
    }

    fun tryTranslateEmail() {
        // se já foi traduzido volta ao original
        if (_uiState.value.alreadyTranslated) {
            _uiState.value.selectedEmail?.let { email ->
                _uiState.value = _uiState.value.copy(
                    selectedEmail = email.copy(
                        content = _uiState.value.originalContent.toString()
                    ),
                    alreadyTranslated = false
                )
            }
        } else {
            val languageIdentified = _uiState.value.languageIdentified?.code ?: return
            TextTranslate().modelHasBeenDownloaded(
                languageIdentified,
                onSuccessful = {
                    Log.i(DOWNLOAD_TAG, "Modelo NECESSÁRIO já sponivel!!!!")
                    translateEmail()
                },
                onFailure = {
                    Log.i(DOWNLOAD_TAG, "Model NÃO disponivel AINDA!!!!")
                    _uiState.value = _uiState.value.copy(
                        showDownloadLanguageDialog = true
                    )
                }
            )
        }
    }

    private fun translateEmail() {
        _uiState.value.selectedEmail?.let { email ->
            _uiState.value.languageIdentified?.let { languageIdentified ->
                _uiState.value.localLanguage?.let { localLanguage ->
                    TextTranslate().translateText(
                        email.content,
                        targetLanguage = localLanguage.code,
                        sourceLanguage = languageIdentified.code,
                        onSuccessful = { translatedText ->
                            _uiState.value = _uiState.value.copy(
                                selectedEmail = email.copy(
                                    content = translatedText
                                ),
                                alreadyTranslated = true
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
                    TextTranslate().translateText(
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

        Log.i(DOWNLOAD_TAG, "Tentando baixar o modelo para o idioma $languageIdentified")
        TextTranslate().downloadModel(
            languageIdentified,
            onSuccessful = {
                Log.i(DOWNLOAD_TAG, "Modelo baixado com sucesso")
                translateEmail()
            },
            onFailure = {
                Log.i(DOWNLOAD_TAG, "Falha ao baixar o modelo")
            }
        )
    }

    fun changeShowDownloadLanguageDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(
            showDownloadLanguageDialog = show
        )
    }
}