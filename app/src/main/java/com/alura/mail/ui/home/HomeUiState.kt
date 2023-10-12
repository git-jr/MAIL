package com.alura.mail.ui.home

data class HomeUiState(
    val emails: List<Email>,
    val showEmailsList: Boolean = true,
    val selectedTab: Int = 0,
)


data class Email(
    val id: Int,
    val subject: String,
    val content: String,
    val time: Long,
    val color: Long,
    val user: User,
)

data class User(
    val name: String,
    val avatar: String,
)
