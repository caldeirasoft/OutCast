package com.caldeirasoft.outcast.domain.usecase

import kotlinx.coroutines.flow.Flow

interface UseCase<in Input, out Output> {
    fun invoke(params: Input): Flow<Output>
}

interface UseCaseWithoutInput<out Output> {
    fun invoke(): Flow<Output>
}