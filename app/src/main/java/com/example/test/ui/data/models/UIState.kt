package com.example.test.ui.data.models


sealed class UIState {
    object Default: UIState()
    object Success: UIState()
    class Error(val error: String, val type: kotlin.Error): UIState()
}