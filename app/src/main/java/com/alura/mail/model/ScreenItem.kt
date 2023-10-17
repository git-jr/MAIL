package com.alura.mail.model

import androidx.compose.ui.graphics.vector.ImageVector

data class ScreenItem(
    val title: String,
    val route: String,
    val resourceId: Pair<ImageVector, ImageVector>,
)