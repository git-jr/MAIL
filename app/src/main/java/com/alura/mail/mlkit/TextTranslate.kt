package com.alura.mail.mlkit

import android.util.Log
import com.alura.mail.FileUtil
import com.alura.mail.model.DownloadState
import com.alura.mail.model.Language
import com.alura.mail.model.LanguageModel
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
    fun verifyModelDownloaded(
        targetLanguage: String,
        onSuccessful: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        val modelManager = RemoteModelManager.getInstance()

        modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models ->
                Log.e(DOWNLOAD_TAG, "modelos baixados: ${models.map { it.language }}")

                if (models.any { it.language == targetLanguage }) {
                    onSuccessful()
                } else {
                    onFailure()
                }
            }
            .addOnFailureListener {
                Log.e(DOWNLOAD_TAG, "Error getting downloaded models: $it")
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
                    Log.i(DOWNLOAD_TAG, "Model downloaded.")
                    onSuccessful()
                }
                .addOnFailureListener {
                    Log.e(DOWNLOAD_TAG, "Error downloading model: $it")
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
                    Log.i(DOWNLOAD_TAG, "Model removed.")
                    onSuccessful()
                }
                .addOnFailureListener {
                    Log.e(DOWNLOAD_TAG, "Error removing model: $it")
                    onFailure()
                }
        } ?: run {
            onFailure()
        }
    }


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

        with(translator) {
            translate(text)
                .addOnSuccessListener { translatedText ->
                    Log.e(TRANSLATE_TAG, "Idioma original: $sourceLanguage")
                    Log.e(TRANSLATE_TAG, "Idioma traduzido: $targetLanguage")
                    Log.e(TRANSLATE_TAG, "Traslation success: $translatedText")
                    onSuccessful(translatedText)
                }
                .addOnFailureListener { exception ->
                    Log.e(TRANSLATE_TAG, "Translation failed: $exception")
                    onFailure()
                }
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
                                name = languagesNames[model.language] ?: model.language,
                                downloadState = DownloadState.DOWNLOADED,
                                size = fileUtil.getSizeModel(model.modelNameForBackend)
                            )
                        )
                    } catch (e: Exception) {
                        Log.e(DOWNLOAD_TAG, "Error: $e")
                    }
                }
                onSuccessful(languageModels)
            }
            .addOnFailureListener {
                Log.e(DOWNLOAD_TAG, "Error getting downloaded models: $it")
                onFailure()
            }

    }


    fun getAllModels(): List<LanguageModel> {
        val languageModels = mutableListOf<LanguageModel>()

        TranslateLanguage.getAllLanguages().forEach {
            Log.i(TRANSLATE_TAG, "Idioma: $it")
            languageModels.add(
                LanguageModel(
                    id = it,
                    name = languagesNames[it] ?: it,
                    downloadState = DownloadState.NOT_DOWNLOADED,
                    size = "",
                )
            )
        }

        return languageModels
    }

    fun verifyIfModelAreAvailableAndDownloadAutomatically(
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


        translator.use {
            it.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    Log.e(TRANSLATE_TAG, "Model avaliable!")
                    onModelAvailable()
                }
                .addOnFailureListener { exception ->
                    Log.e(TRANSLATE_TAG, "Model not avaliable: $exception")
                    onModelNotAvailable()
                }
        }

//        translator.downloadModelIfNeeded(conditions)
//            .addOnSuccessListener {
//                Log.e(TRANSLATE_TAG, "Model avaliable!")
//                onModelAvailable()
//            }
//            .addOnFailureListener { exception ->
//                Log.e(TRANSLATE_TAG, "Model not avaliable: $exception")
//                onModelNotAvailable()
//            }
    }


    private fun downloadLanguageModelOld(
        sourceLanguage: String,
        targetLanguage: String,
        onSuccessful: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        val modelManager = RemoteModelManager.getInstance()

        val sourceModel = TranslateRemoteModel.Builder(sourceLanguage).build()
        val targetModel = TranslateRemoteModel.Builder(targetLanguage).build()
        targetModel.language

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        modelManager.download(sourceModel, conditions)
            .addOnSuccessListener {
                modelManager.download(targetModel, conditions)
                    .addOnSuccessListener {
                        onSuccessful()
                    }
                    .addOnFailureListener {
                        onFailure()
                        Log.e(TRANSLATE_TAG, "Error downloading targetModel: $it")
                    }
            }
            .addOnFailureListener {
                onFailure()
                Log.e(TRANSLATE_TAG, "Error downloading model: $it")
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
                val languageName = languagesNames[languageCode]
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
                    val languageName = languagesNames[languageTag]
                    val confidence = (identifiedLanguage.confidence * 100).toInt()

                    Log.i(
                        LANGUAGE_TAG,
                        "Detected language: $languageTag - $languageName - Probability: $confidence%"
                    )
                }
            }
    }


    fun translateTextOld(
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

        with(translator) {
            verifyIfModelAreAvailableAndDownloadAutomatically(
                sourceLanguage = targetLanguage,
                targetLanguage = sourceLanguage,
                onModelAvailable = {
                    translate(text)
                        .addOnSuccessListener { translatedText ->
                            onSuccessful(translatedText)
                        }
                        .addOnFailureListener { exception ->
                            Log.e(TRANSLATE_TAG, "Translation failed: $exception")
                            onFailure()
                        }
                },
                onModelNotAvailable = {
                    downloadLanguageModelOld(
                        sourceLanguage = targetLanguage,
                        targetLanguage = sourceLanguage,
                        onSuccessful = {
                            translate(text)
                                .addOnSuccessListener { translatedText ->
                                    onSuccessful(translatedText)
                                }
                                .addOnFailureListener { exception ->
                                    Log.e(TRANSLATE_TAG, "Translation failed: $exception")
                                    onFailure()
                                }
                        },
                        onFailure = {
                            onFailure()
                        }
                    )
                }
            )

        }

    }
}


