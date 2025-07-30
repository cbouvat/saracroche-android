package com.cbouvat.android.saracroche.ui.report

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbouvat.android.saracroche.network.NetworkError
import com.cbouvat.android.saracroche.network.NetworkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern

data class ReportUiState(
    val phoneNumber: String = "",
    val isLoading: Boolean = false,
    val showAlert: Boolean = false,
    val alertMessage: String = "",
    val alertType: AlertType = AlertType.INFO
)

enum class AlertType(val title: String) {
    SUCCESS("Succès"),
    ERROR("Erreur"),
    INFO("Information")
}

class ReportViewModel(private val context: Context) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    private val networkService = NetworkService(context)

    fun updatePhoneNumber(number: String) {
        val formattedNumber = formatPhoneNumber(number)
        _uiState.value = _uiState.value.copy(phoneNumber = formattedNumber)
    }

    fun submitPhoneNumber() {
        if (!validatePhoneNumber()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                networkService.reportPhoneNumber(_uiState.value.phoneNumber)
                handleSuccess()
            } catch (e: NetworkError) {
                handleNetworkError(e)
            } catch (e: Exception) {
                handleError(e)
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun dismissAlert() {
        _uiState.value = _uiState.value.copy(showAlert = false)
    }

    private fun validatePhoneNumber(): Boolean {
        val trimmedNumber = _uiState.value.phoneNumber.trim()

        // Validation for E.164 format
        val e164Pattern = Pattern.compile("^\\+[1-9]\\d{7,14}$")
        val isValidFormat = e164Pattern.matcher(trimmedNumber).matches()

        when {
            trimmedNumber.isEmpty() -> {
                showError("Veuillez saisir un numéro de téléphone.")
                return false
            }

            !isValidFormat -> {
                showError("Le numéro doit être au format E.164 (ex: +33612345678).")
                return false
            }
            // Validation for French numbers
            trimmedNumber.startsWith("+33") && trimmedNumber.length != 12 -> {
                showError("Les numéros français doivent contenir 12 caractères au total.")
                return false
            }
        }

        return true
    }

    private fun handleSuccess() {
        _uiState.value = _uiState.value.copy(
            phoneNumber = "",
            alertType = AlertType.SUCCESS,
            alertMessage = "Numéro signalé avec succès ! Merci de votre contribution 😊",
            showAlert = true
        )
    }

    private fun handleNetworkError(error: NetworkError) {
        _uiState.value = _uiState.value.copy(
            alertType = AlertType.ERROR,
            alertMessage = error.userMessage,
            showAlert = true
        )
    }

    private fun handleError(error: Exception) {
        _uiState.value = _uiState.value.copy(
            alertType = AlertType.ERROR,
            alertMessage = "Une erreur inattendue s'est produite. Veuillez réessayer.",
            showAlert = true
        )
    }

    private fun showError(message: String) {
        _uiState.value = _uiState.value.copy(
            alertType = AlertType.ERROR,
            alertMessage = message,
            showAlert = true
        )
    }

    private fun formatPhoneNumber(input: String): String {
        return input.replace(" ", "").filter { it.isDigit() || it == '+' }
    }
}
