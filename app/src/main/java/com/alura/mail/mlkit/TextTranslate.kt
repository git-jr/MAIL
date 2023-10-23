package com.alura.mail.mlkit

import android.util.Log
import com.alura.mail.model.DownloadState
import com.alura.mail.model.Language
import com.alura.mail.model.LanguageModel
import com.alura.mail.util.FileUtil
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentifier.UNDETERMINED_LANGUAGE_TAG
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

private const val LANGUAGE_TAG = "LanguageIdentification"
private const val TRANSLATE_TAG = "TranslateTag"
const val DOWNLOAD_TAG = "DownloadTag"

class TextTranslate(private val fileUtil: FileUtil) {

    fun translateText(
        text: String,
        targetLanguage: String,
        sourceLanguage: String,
        onSuccessful: (String) -> Unit = {},
        onFailure: () -> Unit = {},
    ) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage)
            .setTargetLanguage(targetLanguage)
            .build()

        val translator = Translation.getClient(options)

        translator.use {
            it.translate(text)
                .addOnSuccessListener { translatedText ->
                    onSuccessful(translatedText)
                }
                .addOnFailureListener {
                    onFailure()
                }
        }
    }

    fun verifyModelDownloaded(
        targetLanguage: String,
        onSuccessful: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        val modelManager = RemoteModelManager.getInstance()

        modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models ->
                if (models.any { it.language == targetLanguage }) {
                    onSuccessful()
                } else {
                    onFailure()
                }
            }
            .addOnFailureListener {
                onFailure()
            }

    }

    fun downloadModel(
        targetLanguage: String,
        onSuccessful: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        TranslateLanguage.fromLanguageTag(targetLanguage)?.let { language ->
            val modelManager = RemoteModelManager.getInstance()

            val remoteModel = TranslateRemoteModel
                .Builder(language)
                .build()

            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()

            modelManager.download(remoteModel, conditions)
                .addOnSuccessListener {
                    onSuccessful()
                }
                .addOnFailureListener {
                    onFailure()
                }
        } ?: run {
            onFailure()
        }
    }

    fun removeModel(
        targetLanguage: String,
        onSuccessful: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        TranslateLanguage.fromLanguageTag(targetLanguage)?.let { language ->
            val modelManager = RemoteModelManager.getInstance()
            val remoteModel = TranslateRemoteModel
                .Builder(language)
                .build()

            modelManager.deleteDownloadedModel(remoteModel)
                .addOnSuccessListener {
                    onSuccessful()
                }
                .addOnFailureListener {
                    onFailure()
                }
        } ?: run {
            onFailure()
        }
    }


    fun getDownloadedModels(
        onSuccessful: (List<LanguageModel>) -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        val modelManager = RemoteModelManager.getInstance()

        modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models: MutableSet<TranslateRemoteModel> ->
                val languageModels = mutableListOf<LanguageModel>()

                models.forEach { model ->
                    try {
                        languageModels.add(
                            LanguageModel(
                                id = model.language,
                                name = translatableLanguageModels[model.language] ?: model.language,
                                downloadState = DownloadState.DOWNLOADED,
                                size = fileUtil.getSizeModel(model.modelNameForBackend)
                            )
                        )
                    } catch (e: Exception) {
                        Log.e(DOWNLOAD_TAG, "Error: $e")
                        if (model.language == TranslateLanguage.ENGLISH) {
                            Log.e(DOWNLOAD_TAG, "Ingles detectado ${model.language}")
                            languageModels.add(
                                LanguageModel(
                                    id = model.language,
                                    name = translatableLanguageModels[model.language]
                                        ?: model.language,
                                    downloadState = DownloadState.DOWNLOADED,
                                    size = ""
                                )
                            )
                        }
                    }
                }
                onSuccessful(languageModels)
            }
            .addOnFailureListener {
                onFailure()
            }

    }


    fun getAllModels(): List<LanguageModel> {
        val languageModels = mutableListOf<LanguageModel>()

        TranslateLanguage.getAllLanguages().forEach {
            // log codigos de linguagem
            Log.i("DOWNLOAD_TAG", "Language: $it")
            languageModels.add(
                LanguageModel(
                    id = it,
                    name = translatableLanguageModels[it] ?: it,
                    downloadState = DownloadState.NOT_DOWNLOADED,
                    size = "",
                )
            )
        }

        return languageModels
    }

    fun translateIfModelAvailableOrDownloadAndTranslate(
        text: String,
        sourceLanguage: String,
        targetLanguage: String,
        onModelAvailable: () -> Unit = {},
        onModelNotAvailable: () -> Unit
    ) {
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage)
            .setTargetLanguage(targetLanguage)
            .build()
        val translator = Translation.getClient(options)

        translator.use { safeTranslator ->
            safeTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    safeTranslator.translate(text)
                        .addOnSuccessListener { translatedText ->
                            Log.i(TRANSLATE_TAG, "Translated text: $translatedText")
                        }
                        .addOnFailureListener { exception ->
                            Log.e(TRANSLATE_TAG, "Error: $exception")
                        }
                    Log.e(TRANSLATE_TAG, "Model avaliable!")
                    onModelAvailable()
                }
                .addOnFailureListener { exception ->
                    Log.e(TRANSLATE_TAG, "Model not avaliable: $exception")
                    onModelNotAvailable()
                }
        }
    }


    fun identifyLanguage(
        text: String,
        onSuccessful: (Language) -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        val languageIdentifier = LanguageIdentification
            .getClient()

        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                val languageName = translatableLanguageModels[languageCode]
                if (languageCode == UNDETERMINED_LANGUAGE_TAG || languageName == null) {
                    Log.i(LANGUAGE_TAG, "Can't identify language.")
                    onFailure()
                } else {
                    onSuccessful(
                        Language(languageName, languageCode)
                    )
                }
            }
            .addOnFailureListener {
                onFailure()
            }

        languageIdentifier.identifyPossibleLanguages(text)
            .addOnSuccessListener { identifiedLanguages ->
                for (identifiedLanguage in identifiedLanguages) {
                    val languageTag = identifiedLanguage.languageTag
                    val languageName = translatableLanguageModels[languageTag]
                    val confidence = (identifiedLanguage.confidence * 100).toInt()

                    Log.i(
                        LANGUAGE_TAG,
                        "Detected language: $languageTag - $languageName - Probability: $confidence%"
                    )
                }
            }
    }
}


