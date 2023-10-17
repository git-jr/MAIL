package com.alura.mail.ui.navigation

import androidx.compose.foundation.lazy.LazyListState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.alura.mail.ui.home.EmailsListScreen

internal const val emailListRoute = "emailList"

fun NavGraphBuilder.emailsListScreen(
    onOpenEmail: (String) -> Unit = {},
    onSendNewMessage: () -> Unit = {},
    listState: LazyListState
) {
    composable(emailListRoute) {
        EmailsListScreen(
            onClick = {
                onOpenEmail(it.id.toString())
            },
            listState = listState
        )
    }

}

internal fun NavHostController.navigateToEmailsListScreen(
    navOptions: NavOptions? = null
) {
    navigate(emailListRoute, navOptions)
}
