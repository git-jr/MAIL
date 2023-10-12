package com.alura.mail.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun HomeBottomBar(
    modifier: Modifier = Modifier,
    currentTab: Int,
    onItemSelected: (Int) -> Unit,
) {
    Box(modifier = modifier) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
        ) {
            screenItems.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            if (currentTab == index) item.second.first else item.second.second,
                            contentDescription = item.first,
                        )
                    },
                    label = { Text(item.first) },
                    selected = currentTab == index,
                    onClick = { onItemSelected(index) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                        selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                    )
                )
            }
        }
    }
}

val screenItems = listOf(
    Pair("Home", Pair(Icons.Filled.Home, Icons.Outlined.Home)),
    Pair("Ajustes", Pair(Icons.Filled.Settings, Icons.Outlined.Settings)),
)