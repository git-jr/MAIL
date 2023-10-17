package com.alura.mail.ui.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.alura.mail.ui.components.LoadScreen
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

        state.selectedEmail?.let {
            ContentEmailScreen(
                state = state,
            )
        } ?: run {
            LoadScreen()
        }
    }
}

internal fun NavHostController.navigateToContentEmailScreen(
    emailId: String
) {
    navigateDirect("$contentEmailRoute/$emailId")
}