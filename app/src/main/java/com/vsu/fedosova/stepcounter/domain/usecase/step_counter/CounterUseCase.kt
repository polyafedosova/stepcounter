package com.vsu.fedosova.stepcounter.domain.usecase.step_counter

import com.vsu.fedosova.stepcounter.domain.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CounterUseCase @Inject constructor(
    private val repository: Repository,
    private val coroutineScope: CoroutineScope,
) {

    private var shagi = 0
    private var kkal = 0.0
    private var km = 0.0
    private var progress = 0

    private var rost = 0
    private var ves = 0
    private var stepsPlane = 0
    private var stepSize =
        (rost / 400.0) + 0.37

    private var date = Date()

    private val _tickerData: MutableStateFlow<TickerUseCaseModel?> = MutableStateFlow(null)
    val tickerData: StateFlow<TickerUseCaseModel?> = _tickerData.asStateFlow()

    init {
        coroutineScope.launch {
            restoreCounterData()
            getUserData()
        }
    }

    fun doStep(): TickerUseCaseModel {
        shagi++
        km += stepSize
        kkal = (shagi * stepSize) / 1000 * 0.5 * ves
        progress = (shagi * 100) / stepsPlane
        _tickerData.value = TickerUseCaseModel(date, shagi, km, kkal, progress, stepsPlane)
        return TickerUseCaseModel(date, shagi, km, kkal, progress, stepsPlane)
    }


    private suspend fun restoreCounterData() {
        repository.restoreToDayData()?.let {
            shagi = it.steps
            kkal = it.kkal
            km = it.km
            progress = it.progress
            date = it.date
        }
    }

    private suspend fun getUserData() {
        repository.getUserData().let { flow ->
            flow.collect { data ->
                if (data != null) {
                    rost = data.height
                    ves = data.weight
                    stepsPlane = data.stepPlane
                    stepSize = (rost / 400.0) + 0.37
                    progress = (shagi * 100) / stepsPlane
                    _tickerData.value =
                        TickerUseCaseModel(date, shagi, km, kkal, progress, stepsPlane)
                }
            }
        }
    }

    suspend fun saveDataForNow() {
        repository.saveDataForNow(tickerData.value)
    }

    suspend fun saveDataForTheDay() {
        repository.saveDataForTheDay(tickerData.value)
        resetCounter()
    }

    private fun resetCounter() {
        shagi = 0
        km = 0.0
        kkal = 0.0
        progress = 0
        _tickerData.value = TickerUseCaseModel(date, shagi, km, kkal, progress, stepsPlane)
    }
}

