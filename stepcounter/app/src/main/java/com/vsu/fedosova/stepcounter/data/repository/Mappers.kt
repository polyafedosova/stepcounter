package com.vsu.fedosova.stepcounter.data.repository

import com.vsu.fedosova.stepcounter.data.room.entity.StepAllTimeDataEntity
import com.vsu.fedosova.stepcounter.data.room.entity.StepForNowDataEntity
import com.vsu.fedosova.stepcounter.data.room.entity.UserDataEntity
import com.vsu.fedosova.stepcounter.domain.usecase.statistic.StatisticsUseCaseModel
import com.vsu.fedosova.stepcounter.domain.usecase.step_counter.TickerUseCaseModel
import com.vsu.fedosova.stepcounter.domain.usecase.user_data.UserDataUseCaseModel

fun StepForNowDataEntity.mapToDomainModel(): TickerUseCaseModel {
    return TickerUseCaseModel(
        steps = this.steps,
        km = this.km,
        progress = this.progress,
        kkal = this.kkal,
        stepsPlane = this.stepPlane,
        date = this.date
    )
}

fun UserDataEntity.mapToDomainModel(): UserDataUseCaseModel {
    return UserDataUseCaseModel(
        height = this.height,
        weight = this.weight,
        stepPlane = this.stepPlane
    )
}

fun StepAllTimeDataEntity.mapToDomainModel(): StatisticsUseCaseModel {
    return StatisticsUseCaseModel(
        date = this.date,
        steps = this.steps,
        kkal = this.kkal,
        meters = this.km,
        stepPlane = this.stepPlane,
        progress = this.progress,
    )
}

fun UserDataUseCaseModel.mapToEntity(): UserDataEntity {
    return UserDataEntity(
        height = this.height,
        weight = this.weight,
        stepPlane = this.stepPlane
    )
}

fun TickerUseCaseModel.mapToStepForNowDataEntity(): StepForNowDataEntity {
    return StepForNowDataEntity(
        steps = this.steps,
        km = this.km,
        progress = this.progress,
        kkal = this.kkal,
        stepPlane = this.stepsPlane,
    )
}

fun TickerUseCaseModel.mapToAllTimeDataEntity(): StepAllTimeDataEntity {
    return StepAllTimeDataEntity(
        steps = this.steps,
        km = this.km,
        progress = this.progress,
        kkal = this.kkal,
        stepPlane = this.stepsPlane
    )
}