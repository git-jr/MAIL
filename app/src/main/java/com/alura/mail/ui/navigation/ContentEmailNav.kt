package com.alura.mail.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.alura.mail.ui.contentEmail.ContentEmailScreen
import com.alura.mail.ui.contentEmail.ContentEmailViewModel
import com.alura.mail.ui.home.navigateDirect

internal const val contentEmailRoute = "emails"
internal const val emailIdArgument = "emailId"
internal const val contentEmailFullPath = "$contentEmailRoute/{$emailIdArgument}"

fun NavGraphBuilder.contentEmailScreen() {
    composable(contentEmailFullPath) {
        val viewModel = viewModel<ContentEmailViewModel>()
        val state by viewModel.uiState.collectAsState()

        if (state.selectedEmail != null) {
            ContentEmailScreen(
                email = state.selectedEmail!!,
                textTranslateFor = "Traduzir do Inglês para Português",
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Carregando...",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            }
        }

    }
}

internal fun NavHostController.navigateToContentEmailScreen(
    emailId: String
) {
    navigateDirect("$contentEmailRoute/$emailId")
}