package org.example.wan.android.data.model

data class LoginFormState(
    val usernameError: String? = null,
    val passwordError: String? = null,
    val isDataValid: Boolean = false
)