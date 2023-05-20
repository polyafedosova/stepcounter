package com.vsu.fedosova.stepcounter.ui.screens.dialog_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsu.fedosova.stepcounter.R
import dagger.hilt.android.lifecycle.HiltViewModel
import com.vsu.fedosova.stepcounter.domain.DomainDataState
import com.vsu.fedosova.stepcounter.domain.usecase.user_data.InvalidateStatus
import com.vsu.fedosova.stepcounter.domain.usecase.user_data.UserDataUpdateState
import com.vsu.fedosova.stepcounter.domain.usecase.user_data.UserDataUseCase
import com.vsu.fedosova.stepcounter.ui.util.resource_provider.ResourceProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DialogSettingsViewModel @Inject constructor(
    private val userDataUseCase: UserDataUseCase,
    private val resource: ResourceProvider,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<DialogSettingsState>(DialogSettingsState.NoData(resource.getString(R.string.no_data)))
    val uiState: StateFlow<DialogSettingsState> get() = _uiState.asStateFlow()

    init {
        getUserData()
    }

    private fun getUserData() = viewModelScope.launch {
        userDataUseCase.getUserData().collect { dataState ->
            when (dataState) {
                is DomainDataState.YesData ->
                    _uiState.value = DialogSettingsState.YesData(dataState.data.mapToiModel())
                is DomainDataState.NoData ->
                    _uiState.value =
                        DialogSettingsState.NoData(resource.getString(R.string.no_data))
            }
        }
    }

    fun saveUserData(weight: String, height: String, stepPlane: String) = viewModelScope.launch {
        val resultUpdate = userDataUseCase.updateUserData(weight, height, stepPlane)

        when (resultUpdate) {
            is UserDataUpdateState.Invalidate -> {
                when (resultUpdate.status) {
                    InvalidateStatus.WRITE_NOT_A_NUMBER ->
                        _uiState.value =
                            DialogSettingsState.InvalidData(resource.getString(R.string.write_only_numers))
                    InvalidateStatus.WRITE_NIL ->
                        _uiState.value =
                            DialogSettingsState.InvalidData(resource.getString(R.string.write_null))
                }
            }
            is UserDataUpdateState.NoData -> _uiState.value =
                DialogSettingsState.NoData(resource.getString(R.string.no_data))
            is UserDataUpdateState.Success -> _uiState.value = DialogSettingsState.Close
        }
    }
}
