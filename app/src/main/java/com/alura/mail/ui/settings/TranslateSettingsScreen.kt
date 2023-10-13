package com.alura.mail.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alura.mail.R
import com.alura.mail.ui.contentEmail.DownloadState
import com.alura.mail.ui.contentEmail.Language
import com.alura.mail.ui.contentEmail.TranslateSettingsViewModel

@Composable
fun TranslateSettingsScreen(modifier: Modifier = Modifier) {
    val translateSettingsViewModel = viewModel<TranslateSettingsViewModel>()
    val state by translateSettingsViewModel.uiState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
    ) {
        Text(
            text = "Ao fazer o download de um idioma, você pode traduzir os e-mails recebidos para esse idioma mesmo sem conexão com a internet.",
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            modifier = modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.size(8.dp))

        LazyColumn {
            if (state.downloadedLanguages.isNotEmpty()) {
                item {
                    SeparatorLanguageItem("Download concluído")
                    state.downloadedLanguages.forEach { language ->
                        LanguageItem(language)
                    }
                }
            }

            if (state.notDownloadedLanguages.isNotEmpty()) {
                item {
                    SeparatorLanguageItem("Toque para fazer o download")
                    state.notDownloadedLanguages.forEach { language ->
                        LanguageItem(language)
                    }
                }
            }
        }
    }
}

@Composable
private fun SeparatorLanguageItem(title: String) {
    Text(
        text = title,
        fontSize = MaterialTheme.typography.bodySmall.fontSize,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 4.dp),
    )
}

@Composable
private fun LanguageItem(language: Language) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .clickable { }
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = language.name,
            fontSize = 18.sp,
        )

        Icon(
            painter = painterResource(id = getIconByLanguage(language)),
            contentDescription = getDescriptionByLanguage(language),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
            modifier = Modifier.size(26.dp),
        )
    }
}

@Composable
private fun getIconByLanguage(language: Language) = when (language.downloadState) {
    DownloadState.DOWNLOADED -> R.drawable.ic_delete
    DownloadState.DOWNLOADING -> R.drawable.ic_downloading
    DownloadState.NOT_DOWNLOADED -> R.drawable.ic_download
}

@Composable
private fun getDescriptionByLanguage(language: Language) = when (language.downloadState) {
    DownloadState.DOWNLOADED -> "Idioma baixado"
    DownloadState.DOWNLOADING -> "Baixando idioma"
    DownloadState.NOT_DOWNLOADED -> "Toque para fazer o download"
}