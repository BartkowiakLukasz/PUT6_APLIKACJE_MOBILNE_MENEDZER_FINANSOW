package com.smartfinanse.di

import android.content.Context
import androidx.room.Room
import com.smartfinanse.data.local.SmartFinanseDatabase
import com.smartfinanse.data.local.dao.CategoryDao
import com.smartfinanse.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SmartFinanseDatabase =
        Room.databaseBuilder(
            context,
            SmartFinanseDatabase::class.java,
            SmartFinanseDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideCategoryDao(database: SmartFinanseDatabase): CategoryDao =
        database.categoryDao

    @Provides
    fun provideTransactionDao(database: SmartFinanseDatabase): TransactionDao =
        database.transactionDao
}
