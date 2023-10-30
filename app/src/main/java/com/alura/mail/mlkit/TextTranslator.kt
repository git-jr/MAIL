package com.alura.mail.mlkit

import android.util.Log
import com.alura.mail.model.Language
import com.alura.mail.util.FileUtil
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentifier
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions


class TextTranslator(private val fileUtil: FileUtil) {
    fun languageIdentifier(
        text: String,
        onSuccess: (Language) -> Unit,
        onFailure: () -> Unit
    ) {
        val languageIdentifier = LanguageIdentification.getClient()
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                Log.i("Language", languageCode)
                val languageName =
                    translatableLanguageModels[languageCode]
                        ?: LanguageIdentifier.UNDETERMINED_LANGUAGE_TAG

                if (languageName != LanguageIdentifier.UNDETERMINED_LANGUAGE_TAG) {
                    onSuccess(Language(code = languageCode, name = languageName))
                } else {
                    onFailure()
                }

            }.addOnFailureListener {
                Log.e("Language", "Error", it)
                onFailure()
            }
    }


    fun textTranslate(
        text: String,
        sourceLanguage: String,
        targetLanguage: String,
        onSuccess: (String) -> Unit,
        onFailure: () -> Unit
    ) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage)
            .setTargetLanguage(targetLanguage)
            .build()

        val translator = Translation.getClient(options)
        translator.translate(text)
            .addOnSuccessListener { translatedText ->
                Log.i("translator", translatedText)
                onSuccess(translatedText)
            }
            .addOnFailureListener { exception ->
                Log.e("translator", exception.toString())
                onFailure()
            }
    }


    fun verifyModelDownloaded(
        targetLanguage: String,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        val modelManager = RemoteModelManager.getInstance()

        modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models ->
                if (models.any { it.language == targetLanguage }) {
                    onSuccess()
                } else {
                    onFailure()
                }
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun downloadModel(
        languageModel: String,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        val localLanguageModel = TranslateRemoteModel.Builder(languageModel).build()

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        val modelManager = RemoteModelManager.getInstance()

        modelManager.download(localLanguageModel, conditions)
            .addOnSuccessListener {
                Log.i("modelManager", "Modelo padrão baixado com sucesso")
                onSuccess()
            }
            .addOnFailureListener {
                Log.e("modelManager", "Erro ao baixar modelo padrão", it)
                onFailure()
            }
    }

}

