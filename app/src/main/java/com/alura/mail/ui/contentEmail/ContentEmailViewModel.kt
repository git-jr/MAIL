package com.alura.mail.ui.contentEmail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alura.mail.dao.EmailDao
import com.alura.mail.model.Email
import com.alura.mail.ui.navigation.emailIdArgument
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContentEmailViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val emailId: String = checkNotNull(savedStateHandle[emailIdArgument])

    private val _uiState = MutableStateFlow(ContentEmailUiState())
    var uiState = _uiState.asStateFlow()

    init {
        loadEmail()
    }

    private fun loadEmail() {
        viewModelScope.launch {
            val email = EmailDao().getEmails().firstOrNull { it.id == emailId }
            _uiState.value = _uiState.value.copy(
                selectedEmail = email
            )
        }
    }
}

data class ContentEmailUiState(
    val selectedEmail: Email? = null
)