package com.alura.mail.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun NavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {

//    NavHost(
//        navController = navController,
//        startDestination = emailListRoute,
//        modifier = modifier,
//    ) {
//        emailsListScreen(
//            onOpenEmail = { emailId ->
//                navController.navigateToContentEmailScreen(emailId)
//            }
//        )
//
//        contentEmailScreen(
//            onBack = {
//                navController.navigateUp()
//            }
//        )
//
//        translateSettingsScreen(
//            onBack = {
//                navController.navigateUp()
//            }
//        )
//    }
}
