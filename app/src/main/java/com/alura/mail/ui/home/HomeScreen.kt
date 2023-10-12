package com.alura.mail.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alura.mail.ui.components.HomeAppBar
import com.alura.mail.ui.components.HomeBottomBar
import com.alura.mail.ui.components.HomeFAB
import com.alura.mail.ui.contentEmail.ContentEmailScreen
import com.alura.mail.ui.settingsScreen.SettingsScreen

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreen(onBack: () -> Unit) {
    val homeViewModel = viewModel<HomeViewModel>()
    val state by homeViewModel.uiState.collectAsState()
    val showEmailsList = state.showEmailsList

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    SetupOnBackPress(
        showEmailsList = showEmailsList,
        onBack = onBack,
        onChangeSelectedTab = {
            homeViewModel.changeSelectedTab(it)
        }
    )

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (showEmailsList) {
                HomeAppBar(scrollBehavior)
            }
        },
        floatingActionButton = {
            if (showEmailsList) {
                HomeFAB()
            }
        },
        bottomBar = {
            HomeBottomBar(
                currentTab = state.selectedTab,
                onItemSelected = { tab ->
                    homeViewModel.changeSelectedTab(tab)
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            var selectedEmail: Email? by remember { mutableStateOf(null) }
            selectedEmail?.let {
                ContentEmailScreen(
                    email = it,
                    textTranslateFor = "Traduzir para o português"
                )
            }

            Crossfade(targetState = showEmailsList, label = "") { showFeed ->
                if (showFeed) {
                    ListPosts(state.emails) {
                        selectedEmail = it
                    }
                } else {
                    SettingsScreen()
                }
            }
        }
    }
}

@Composable
private fun SetupOnBackPress(
    showEmailsList: Boolean,
    onChangeSelectedTab: (Int) -> Unit,
    onBack: () -> Unit
) {
    BackHandler {
        if (showEmailsList) {
            onBack()
        } else {
            onChangeSelectedTab(0)
        }
    }
}