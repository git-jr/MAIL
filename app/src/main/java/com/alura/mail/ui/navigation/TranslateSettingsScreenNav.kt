package com.alura.mail.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.alura.mail.ui.settings.TranslateSettingsScreen

internal const val translateSettingsRoute = "translateSettings"

fun NavGraphBuilder.translateSettingsScreen(navController: NavHostController) {

    composable(translateSettingsRoute) {
        TranslateSettingsScreen(
            onBackClick = { navController.popBackStack() }
        )
    }
}