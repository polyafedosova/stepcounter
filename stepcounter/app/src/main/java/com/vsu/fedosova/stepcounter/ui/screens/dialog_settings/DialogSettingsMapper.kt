package com.vsu.fedosova.stepcounter.ui.screens.dialog_settings

import com.vsu.fedosova.stepcounter.domain.usecase.user_data.UserDataUseCaseModel

fun UserDataUseCaseModel.mapToiModel(): DialogSettingsUiModel {
    return DialogSettingsUiModel(
        height = this.height.toString(),
        weight = this.weight.toString(),
        stepPlane = this.stepPlane.toString()
    )
}