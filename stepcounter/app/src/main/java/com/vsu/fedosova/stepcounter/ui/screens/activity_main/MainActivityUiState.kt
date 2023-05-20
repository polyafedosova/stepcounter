package com.vsu.fedosova.stepcounter.ui.screens.activity_main

import com.vsu.fedosova.stepcounter.ui.screens.activity_main.model.AdapterStepsUiModel

sealed class MainActivityUiState {
    object YesUserData : MainActivityUiState()
    object NoUserData : MainActivityUiState()
    object NoStatisticsData : MainActivityUiState()
    class YesStatisticsData(val data: List<AdapterStepsUiModel>) : MainActivityUiState()
}
