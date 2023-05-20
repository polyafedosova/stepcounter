package com.vsu.fedosova.stepcounter.domain

import com.vsu.fedosova.stepcounter.domain.usecase.statistic.StatisticsUseCaseModel
import com.vsu.fedosova.stepcounter.domain.usecase.step_counter.TickerUseCaseModel
import com.vsu.fedosova.stepcounter.domain.usecase.user_data.UserDataUseCaseModel
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getAllTimeData(): Flow<List<StatisticsUseCaseModel>>

    fun getUserData(): Flow<UserDataUseCaseModel?>

    suspend fun saveUserData(userData: UserDataUseCaseModel)

    suspend fun deleteAllData(): List<StatisticsUseCaseModel>

    suspend fun getDataForSpecificTime(howManyDays: Int): List<StatisticsUseCaseModel>

    suspend fun restoreToDayData(): TickerUseCaseModel?

    suspend fun saveDataForNow(data: TickerUseCaseModel?)

    suspend fun saveDataForTheDay(data: TickerUseCaseModel?)

}