package com.alura.mail.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alura.mail.ui.components.HomeAppBar
import com.alura.mail.ui.components.HomeBottomBar
import com.alura.mail.ui.components.HomeFAB
import com.alura.mail.ui.contentEmail.ContentEmailScreen
import com.alura.mail.ui.settings.TranslateSettingsScreen

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreen(onBack: () -> Unit) {
    val homeViewModel = viewModel<HomeViewModel>()
    val state by homeViewModel.uiState.collectAsState()
    val showEmailsList = state.showEmailsList

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    SetupOnBackPress(
        showEmailList = showEmailsList,
        onBack = onBack,
        onChangeSelectedTab = {
            homeViewModel.changeSelectedTab(it)
        }
    )

    var selectedEmail: Email? by remember { mutableStateOf(null) }


    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (showEmailsList) {
                HomeAppBar(scrollBehavior)
            } else {
                DefaultAppBar(
                    title = selectedEmail?.subject ?: "Configurações de idioma",
                    scrollBehavior = scrollBehavior,
                    onBack = onBack
                )
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
            Crossfade(targetState = state.selectedTab, label = "") { selectedTab ->

                when (selectedTab) {
                    0 -> {
                        ListPosts(state.emails) {
                            selectedEmail = it
                            homeViewModel.changeSelectedTab(2)
                        }
                    }

                    1 -> {
                        TranslateSettingsScreen()
                    }

                    2 -> {
                        selectedEmail?.let {
                            ContentEmailScreen(
                                email = it,
                                textTranslateFor = "Traduzir para o {idioma}"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SetupOnBackPress(
    showEmailList: Boolean,
    onChangeSelectedTab: (Int) -> Unit,
    onBack: () -> Unit
) {
    BackHandler {
        if (showEmailList) {
            onBack()
        } else {
            onChangeSelectedTab(0)
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