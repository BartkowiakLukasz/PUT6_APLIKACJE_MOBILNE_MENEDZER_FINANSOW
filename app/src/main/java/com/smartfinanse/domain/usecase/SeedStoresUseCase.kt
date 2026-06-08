package com.smartfinanse.domain.usecase

import com.smartfinanse.data.local.entity.StoreEntity
import com.smartfinanse.domain.repository.StoreRepository
import javax.inject.Inject

class SeedStoresUseCase @Inject constructor(
    private val storeRepository: StoreRepository
) {
    suspend operator fun invoke() {
        storeRepository.seedStoresIfEmpty()
    }
}
