package com.alura.mail.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.alura.mail.ui.settings.TranslateSettingsScreen

internal const val translateSettingsRoute = "translateSettings"

fun NavGraphBuilder.translateSettingsScreen() {
    composable(translateSettingsRoute) {
        TranslateSettingsScreen()
    }

}

internal fun NavHostController.navigateToTranslateSettingsScreen(
    navOptions: NavOptions? = null
) {
    navigate(translateSettingsRoute, navOptions)
}