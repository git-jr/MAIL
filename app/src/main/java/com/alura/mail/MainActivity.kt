package com.alura.mail

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.alura.mail.mlkit.TextTranslator
import com.alura.mail.ui.navigation.HomeNavHost
import com.alura.mail.ui.theme.MAILTheme
import com.alura.mail.util.FileUtil
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

                    TextTranslator(FileUtil(this)).textTranslate(
                        text = "Uma arquitethura separada",
                        sourceLanguage = TranslateLanguage.PORTUGUESE,
                        targetLanguage = TranslateLanguage.ENGLISH,
                        onSuccess = { translatedText ->
                            Log.i("translator main", translatedText)
                        },
                        onFailure = {
                            Log.e("translator main", "Error")
                        }
                    )
                }
            }
        }
    }
}
