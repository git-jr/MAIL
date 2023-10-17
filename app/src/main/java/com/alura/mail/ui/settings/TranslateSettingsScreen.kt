package com.alura.mail.ui.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alura.mail.R
import com.alura.mail.model.DownloadState
import com.alura.mail.model.Language
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
                        LanguageItem(
                            language = language,
                            onClick = {
                                translateSettingsViewModel.selectedLanguage(language)
                                translateSettingsViewModel.changeShowDeleteLanguageDialog(true)
                            }
                        )
                    }
                }
            }

            if (state.notDownloadedLanguages.isNotEmpty()) {
                item {
                    SeparatorLanguageItem("Toque para fazer o download")
                    state.notDownloadedLanguages.forEach { language ->
                        LanguageItem(
                            language = language,
                            onClick = {
                                translateSettingsViewModel.selectedLanguage(language)
                                translateSettingsViewModel.changeShowDownloadLanguageDialog(true)
                            }
                        )
                    }
                }
            }
        }
    }

    if (state.showDownloadLanguageDialog && state.selectedLanguage != null) {
        state.selectedLanguage?.let { selectedLanguage ->
            LanguageDialog(
                title = "${selectedLanguage.name} (${selectedLanguage.size})",
                description = "Para traduzir os e-mails recebidos para esse idioma mesmo sem conexão com a internet, faça o download do arquivo de tradução.",
                confirmText = "Download",
                onDismiss = {
                    translateSettingsViewModel.changeShowDownloadLanguageDialog(false)
                },
                onConfirm = {
                    translateSettingsViewModel.changeDownloadState(
                        language = selectedLanguage,
                        downloadState = DownloadState.DOWNLOADING
                    )

                    translateSettingsViewModel.downloadLanguage(
                        language = selectedLanguage
                    )
                    translateSettingsViewModel.changeShowDownloadLanguageDialog(false)
                }
            )
        }
    }


    if (state.showDeleteLanguageDialog && state.selectedLanguage != null) {
        state.selectedLanguage?.let { selectedLanguage ->
            LanguageDialog(
                title = "${selectedLanguage.name} (${selectedLanguage.size})",
                description = "Se você remover este arquivo de tradução, não poderá traduzir os e-mails recebidos para esse idioma até fazer o download novamente.",
                confirmText = "Remover",
                onDismiss = {
                    translateSettingsViewModel.changeShowDeleteLanguageDialog(false)
                },
                onConfirm = {
                    translateSettingsViewModel.changeDownloadState(
                        language = selectedLanguage,
                        downloadState = DownloadState.NOT_DOWNLOADED
                    )
                    translateSettingsViewModel.changeShowDeleteLanguageDialog(false)
                }
            )
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
private fun LanguageItem(
    language: Language,
    onClick: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .clickable { onClick() }
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
fun LanguageDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    description: String,
    confirmText: String,
) {
    Dialog(
        onDismissRequest = { onDismiss() }
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        MaterialTheme.shapes.extraLarge
                    )
                    .padding(22.dp)
            ) {
                Text(
                    text = title,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.size(24.dp))

                Text(
                    text = description,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.size(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cancelar",
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onDismiss() }
                            .padding(vertical = 8.dp, horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = confirmText,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onConfirm() }
                            .padding(vertical = 8.dp, horizontal = 12.dp)
                    )
                }
            }
        }

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