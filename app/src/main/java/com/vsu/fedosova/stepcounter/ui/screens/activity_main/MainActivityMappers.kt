package com.vsu.fedosova.stepcounter.ui.screens.activity_main

import com.vsu.fedosova.stepcounter.domain.usecase.statistic.StatisticsUseCaseModel
import com.vsu.fedosova.stepcounter.domain.usecase.step_counter.TickerUseCaseModel
import com.vsu.fedosova.stepcounter.ui.screens.activity_main.model.AdapterStepsUiModel
import com.vsu.fedosova.stepcounter.ui.screens.activity_main.model.MainActivityUiModel
import java.text.DecimalFormat

fun TickerUseCaseModel.mapToUiModel(): MainActivityUiModel {
    return MainActivityUiModel(
        progress = this.progress,
        steps = this.steps.toString(),
        kkal = DecimalFormat("#0.00").format(this.kkal).toString(),
        km = DecimalFormat("#0.00").format(this.km).toString(),
        stepPlane = this.stepsPlane.toString()
    )
}


fun StatisticsUseCaseModel.mapToUiModel(): AdapterStepsUiModel {
    return AdapterStepsUiModel(
        date = this.date,
        progress = this.progress,
        steps = this.steps.toString(),
        km = DecimalFormat("#0.00").format(this.meters / 1000).toString(),
        kkal = DecimalFormat("#0.0").format(this.kkal),
        stepPlane = this.stepPlane.toString()
    )
}