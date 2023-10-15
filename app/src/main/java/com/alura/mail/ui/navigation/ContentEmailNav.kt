package com.alura.mail.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.alura.mail.dao.EmailDao
import com.alura.mail.ui.contentEmail.ContentEmailScreen

internal const val contentEmailRoute = "emails"
internal const val emailIdArgument = "emailId"
internal const val contentEmailFullPath = "$contentEmailRoute/{$emailIdArgument}"

fun NavGraphBuilder.contentEmailScreen() {
    composable(contentEmailFullPath) { backStackEntry ->
        val emailId = backStackEntry.arguments?.getString(emailIdArgument)
        val tempEmail = EmailDao().getEmails().firstOrNull { it.id.toString() == emailId }

        tempEmail?.let {
            ContentEmailScreen(
                email = it,
                textTranslateFor = "Traduzir do Inglês para Português",
            )
        }

    }

}

internal fun NavHostController.navigateToContentEmailScreen(
    emailId: String, navOptions: NavOptions? = null
) {
    navigate("$contentEmailRoute/$emailId", navOptions)
}