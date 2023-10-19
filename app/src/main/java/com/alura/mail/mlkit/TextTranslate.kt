package com.alura.mail.mlkit

import android.content.Context
import android.util.Log
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
import java.io.File

private const val LANGUAGE_TAG = "LanguageIdentification"
private const val TRANSLATE_TAG = "TranslateTag"
const val DOWNLOAD_TAG = "DownloadTag"

class TextTranslate {
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
        val modelManager = RemoteModelManager.getInstance()

        val remoteModel = TranslateRemoteModel
            .Builder(
                TranslateLanguage.fromLanguageTag(targetLanguage).toString(),
            )
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
        context: Context,
        onSuccessful: (List<LanguageModel>) -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        val modelManager = RemoteModelManager.getInstance()

        modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models: MutableSet<TranslateRemoteModel> ->

                models.forEach { model ->
                    Log.e(DOWNLOAD_TAG, "model: $model")
                    Log.e(DOWNLOAD_TAG, "Name: ${model.modelName.toString()}")
                    Log.e(DOWNLOAD_TAG, "NameForBackend: ${model.modelNameForBackend}")
                    Log.e(DOWNLOAD_TAG, "Size: ${getSizeModel(context, model.modelNameForBackend)}")
                }


                val languageModels = models.map {
                    LanguageModel(
                        id = it.uniqueModelNameForPersist,
                        name = it.language,
                        downloadState = DownloadState.DOWNLOADED,
                        size = getSizeModel(context, it.modelNameForBackend)
                    )
                }

                onSuccessful(languageModels)
            }
            .addOnFailureListener {
                Log.e(DOWNLOAD_TAG, "Error getting downloaded models: $it")
                onFailure()
            }

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
                val languageName = codeNameLanguagesMap[languageCode]
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
                    val languageName = codeNameLanguagesMap[languageTag]
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

fun getSizeModel(context: Context, modelName: String): String {
    val modelFile =
        File(context.filesDir.parent, "no_backup/com.google.mlkit.translate.models/$modelName")

    return if (modelFile.exists()) {
        formatBytesToMB(getFolderSize(modelFile))
    } else {
        "0 MB"
    }
}

// Função para obter o tamanho de uma pasta recursivamente
private fun getFolderSize(folder: File): Long {
    var size: Long = 0

    if (folder.exists()) {
        val files = folder.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isFile) {
                    size += file.length()
                } else if (file.isDirectory) {
                    size += getFolderSize(file)
                }
            }
        }
    }
    return size
}

fun formatBytesToMB(bytes: Long): String {
    return "${bytes / 1024 / 1024} MB"
}

val codeNameLanguagesMap = mapOf(
    "af" to "africâner",
    "sou" to "amárico",
    "ar" to "árabe",
    "ar-latn" to "árabe",
    "az" to "azerbaijano",
    "ser" to "bielorrusso",
    "bg" to "búlgaro",
    "bg-latn" to "búlgaro",
    "bn" to "bengali",
    "bs" to "bósnio",
    "ca" to "catalão",
    "ceb" to "cebuano",
    "co" to "córsico",
    "cs" to "tcheco",
    "cy" to "galês",
    "da" to "dinamarquês",
    "de" to "alemão",
    "el" to "grego",
    "el-latn" to "grego",
    "en" to "inglês",
    "eo" to "esperanto",
    "es" to "espanhol",
    "et" to "estoniano",
    "eu" to "basco",
    "fa" to "persa",
    "fi" to "finlandês",
    "fil" to "filipino",
    "fr" to "francês",
    "fy" to "frísio ocidental",
    "ga" to "irlandês",
    "gd" to "escocês gaélico",
    "gl" to "galego",
    "gu" to "gujarati",
    "ha" to "hauçá",
    "haw" to "havaiana",
    "he" to "hebraico",
    "hi" to "hindi",
    "hi-latn" to "hindi",
    "hnn" to "hmong",
    "hr" to "croata",
    "ht" to "haitiano",
    "hu" to "húngaro",
    "hy" to "armênio",
    "id" to "indonésio",
    "ig" to "igbo",
    "is" to "islandês",
    "it" to "italiano",
    "ja" to "japonês",
    "jat-latn" to "japonês",
    "jv" to "javanês",
    "ka" to "georgiano",
    "kk" to "cazaque",
    "km" to "khmer",
    "kn" to "canarim",
    "ko" to "coreano",
    "ku" to "curdo",
    "ky" to "quirguiz",
    "la" to "latina",
    "lb" to "luxemburguês",
    "lo" to "laosiano",
    "lt" to "lituano",
    "lv" to "letão",
    "mg" to "malgaxe",
    "mi" to "maori",
    "mk" to "macedônio",
    "ml" to "malaiala",
    "mn" to "mongol",
    "mr" to "marata",
    "ms" to "malaio",
    "mt" to "maltês",
    "my" to "birmanês",
    "ne" to "nepalês",
    "nl" to "holandês",
    "no" to "norueguês",
    "ny" to "nianja",
    "pa" to "punjabi",
    "pl" to "polonês",
    "ps" to "pashto",
    "pt" to "português",
    "ro" to "romeno",
    "ru" to "russo",
    "ru-latn" to "russo",
    "sd" to "sindi",
    "si" to "cingalês",
    "sk" to "eslovaco",
    "sl" to "esloveno",
    "sm" to "samoano",
    "sn" to "chona",
    "so" to "somali",
    "sq" to "albanês",
    "sr" to "sérvio",
    "st" to "sesotho",
    "su" to "sundanês",
    "sv" to "sueco",
    "sw" to "suaíli",
    "ta" to "tâmil",
    "te" to "télugo",
    "tg" to "tajique",
    "th" to "tailandês",
    "tr" to "turco",
    "uk" to "ucraniano",
    "ur" to "urdu",
    "uz" to "usbeque",
    "vi" to "vietnamita",
    "xh" to "xhosa",
    "yi" to "ídiche",
    "yo" to "iorubá",
    "zh" to "chinês",
    "zh-lattn" to "chinês",
    "zu" to "zulu"
)


