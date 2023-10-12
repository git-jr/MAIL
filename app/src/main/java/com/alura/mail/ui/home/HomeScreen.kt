@file:OptIn(ExperimentalMaterial3Api::class)

package com.alura.mail.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alura.mail.ui.components.HomeAppBar
import com.alura.mail.ui.components.HomeBottomBar
import com.alura.mail.ui.components.HomeFAB
import com.alura.mail.ui.settingsScreen.SettingsScreen

@Composable
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
            Crossfade(targetState = showEmailsList, label = "") { showFeed ->
                if (showFeed) {
                    ListPosts(state.emails)
                } else {
                    SettingsScreen()
                }
            }
        }
    }
}


@Composable
fun ListPosts(emails: List<Email>) {
    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
    ) {
        items(emails) { email ->
            Text(
                email.subject,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
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