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
import com.alura.mail.mlkit.translatableLanguageModels
import com.alura.mail.ui.navigation.HomeNavHost
import com.alura.mail.ui.theme.MAILTheme
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentifier.UNDETERMINED_LANGUAGE_TAG
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

                    val text = ""
                    languageIdentifier(
                        text,
                        onSuccess = { languageName ->
                            Log.i("language", languageName)
                        },
                        onFailure = {
                            Log.i("language", "falha ao tentar identificar o idioma")
                        }
                    )
                }
            }
        }
    }

    private fun languageIdentifier(
        text: String,
        onSuccess: (String) -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        val languageIdentifier = LanguageIdentification.getClient()
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener {
                val languageName =
                    translatableLanguageModels[it] ?: UNDETERMINED_LANGUAGE_TAG
                if (languageName == UNDETERMINED_LANGUAGE_TAG) {
                    onFailure()
                    Log.i("language", "undetermined")
                } else {
                    onSuccess(languageName)
                    Log.i("language", languageName)
                }
            }
            .addOnFailureListener {
                onFailure()
                Log.e("language", "error", it)
            }
    }
}

