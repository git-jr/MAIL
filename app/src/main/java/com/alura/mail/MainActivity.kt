package com.alura.mail

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.alura.mail.ui.navigation.HomeNavHost
import com.alura.mail.ui.theme.MAILTheme
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MAILTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    HomeNavHost(navController = navController)


                    val options = TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.PORTUGUESE)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build()
                    val ptEnTranslator = Translation.getClient(options)

                    val downloadManager = DownloadConditions.Builder()
                        .requireWifi() // opcional
                        .requireCharging() // opcional
                        .build()

                    ptEnTranslator.downloadModelIfNeeded(downloadManager)
                        .addOnSuccessListener {
                            Log.i("ptEnTranslator", "Modelo baixado com sucesso!")
                            ptEnTranslator.translate("Bom dia, como vai você?")
                                .addOnSuccessListener { translatedText ->
                                    Log.i("ptEnTranslator", translatedText)
                                }.addOnFailureListener { exception ->
                                    Log.e("ptEnTranslator", exception.toString())
                                }
                        }.addOnFailureListener { exception ->
                            Log.e("ptEnTranslator", "Falhao ao baixa modelo $exception")
                        }
                }
            }
        }
    }
}
