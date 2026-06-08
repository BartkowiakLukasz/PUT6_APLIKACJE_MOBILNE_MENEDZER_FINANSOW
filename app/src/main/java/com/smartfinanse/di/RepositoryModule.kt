package com.smartfinanse.di

import com.smartfinanse.data.repository.CategoryRepositoryImpl
import com.smartfinanse.data.repository.TransactionRepositoryImpl
import com.smartfinanse.domain.repository.CategoryRepository
import com.smartfinanse.data.repository.StoreRepositoryImpl
import com.smartfinanse.domain.repository.StoreRepository
import com.smartfinanse.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindStoreRepository(impl: StoreRepositoryImpl): StoreRepository
}
