package com.alura.mail.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alura.mail.dao.EmailDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        HomeUiState(emails = emptyList())
    )
    var uiState = _uiState.asStateFlow()

    init {
        loadEmails()
    }

    private fun loadEmails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(emails = EmailDao().getEmails())
        }
    }

    fun changeSelectedTab(indexTab: Int) {
        _uiState.value = _uiState.value.copy(
            showEmailsList = indexTab == 0,
            selectedTab = indexTab
        )
    }
}