package com.smartfinanse.domain.usecase

import com.smartfinanse.domain.repository.CategoryRepository
import javax.inject.Inject

class SeedCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke() {
        categoryRepository.seedCategoriesIfEmpty()
        categoryRepository.seedIncomeCategoriesIfMissing()
    }
}
