package com.vsu.fedosova.stepcounter.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.vsu.fedosova.stepcounter.data.repository.RepositoryImpl
import com.vsu.fedosova.stepcounter.data.room.StepsDataBase
import com.vsu.fedosova.stepcounter.data.room.dao.DaoAllTimeData
import com.vsu.fedosova.stepcounter.data.room.dao.DaoDataForNow
import com.vsu.fedosova.stepcounter.data.room.dao.DaoDataUser
import com.vsu.fedosova.stepcounter.di.anatations.DispatcherIo
import com.vsu.fedosova.stepcounter.domain.Repository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    //  REPOSITORY
    @Provides
    @Singleton
    fun provideRepository(
        daoAllTime: DaoAllTimeData,
        daoDataForNow: DaoDataForNow,
        daoUserData: DaoDataUser,
        @DispatcherIo dispatcherIo: CoroutineDispatcher,
    ): Repository {
        return RepositoryImpl(daoDataForNow, daoAllTime, daoUserData, dispatcherIo)
    }

    @Provides
    @Singleton
    fun provideStepsDataBase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, StepsDataBase::class.java, StepsDataBase.dataBaseName)
            .build()

    @Provides
    @Singleton
    fun provideDaoDataForNow(dataBase: StepsDataBase) =
        dataBase.getDaoDataForNow()

    @Provides
    @Singleton
    fun provideDaoAllTimeData(dataBase: StepsDataBase) =
        dataBase.getDaoAllTimeData()

    @Provides
    @Singleton
    fun provideDaoUserData(dataBase: StepsDataBase) =
        dataBase.getDaoUserData()
}
