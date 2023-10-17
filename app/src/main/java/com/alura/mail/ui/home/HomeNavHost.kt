package com.alura.mail.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alura.mail.ui.components.HomeAppBar
import com.alura.mail.ui.components.HomeBottomBar
import com.alura.mail.ui.components.HomeFAB
import com.alura.mail.ui.navigation.contentEmailScreen
import com.alura.mail.ui.navigation.emailListRoute
import com.alura.mail.ui.navigation.emailsListScreen
import com.alura.mail.ui.navigation.navigateToContentEmailScreen
import com.alura.mail.ui.navigation.translateSettingsScreen

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val homeViewModel = viewModel<HomeViewModel>()
    val state by homeViewModel.uiState.collectAsState()
    val showEmailsList = state.showEmailsList

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyListState()

    val expandedFab by remember {
        derivedStateOf {
            listState.isScrollInProgress.not()
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentDestination by remember {
        derivedStateOf {
            navBackStackEntry?.destination
        }
    }

    val selectedEmail: Email? by remember { mutableStateOf(null) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (currentDestination?.route == emailListRoute) {
                HomeAppBar(scrollBehavior)
            } else {
                DefaultAppBar(
                    title = selectedEmail?.subject ?: "Configurações de idioma5",
                    scrollBehavior = scrollBehavior,
                    onBack = {
                        navController.navigateUp()
                    }
                )
            }
        },
        floatingActionButton = {
            if (showEmailsList) {
                HomeFAB(expanded = expandedFab)
            }
        },
        bottomBar = {
            HomeBottomBar(
                currentTab = currentDestination?.route ?: emailListRoute,
                onItemSelected = { route ->
                    navController.navigateDirect(route)
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = emailListRoute,
                modifier = modifier,
            ) {
                emailsListScreen(
                    onOpenEmail = { emailId ->
                        navController.navigateToContentEmailScreen(emailId)
                    },
                    listState = listState
                )
                contentEmailScreen()
                translateSettingsScreen()
            }
        }
    }
}


@ExperimentalMaterial3Api
@Composable
fun DefaultAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = { onBack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "buscar",
                    modifier = Modifier,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        ),
    )
}

fun NavHostController.navigateDirect(rota: String) = this.navigate(rota) {
    popUpTo(this@navigateDirect.graph.findStartDestination().id) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}
