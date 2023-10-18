package com.alura.mail.ui.home

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.alura.mail.ui.navigation.contentEmailFullPath
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

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyListState()

    val expandedFab by remember {
        derivedStateOf { listState.isScrollInProgress.not() }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentDestination by remember {
        derivedStateOf {
            homeViewModel.changeCurrentDestination(
                navBackStackEntry?.destination?.route ?: emailListRoute
            )
            navBackStackEntry?.destination
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (state.showEmailsList) {
                HomeAppBar(scrollBehavior)
            }
        },
        floatingActionButton = {
            if (state.showEmailsList) {
                HomeFAB(expanded = expandedFab)
            }
        },
        bottomBar = {
            if (currentDestination?.route != contentEmailFullPath) {
                HomeBottomBar(
                    currentTab = currentDestination?.route ?: emailListRoute,
                    onItemSelected = { route ->
                        navController.navigateDirect(route)
                    }
                )
            }
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
                enterTransition = { fadeIn(animationSpec = tween(200)) },
                exitTransition = { fadeOut(animationSpec = tween(200)) },
                popEnterTransition = { fadeIn(animationSpec = tween(200)) },
                popExitTransition = { fadeOut(animationSpec = tween(200)) },
            ) {
                emailsListScreen(
                    onOpenEmail = { email ->
                        navController.navigateToContentEmailScreen(email.id)
                        homeViewModel.setSelectedEmail(email)
                    },
                    listState = listState,
                )
                contentEmailScreen(navController = navController)
                translateSettingsScreen(navController = navController)
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun DefaultAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
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
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
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
