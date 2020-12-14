package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.util.NetworkResponse
import com.caldeirasoft.outcast.domain.util.suspendCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single

interface UseCase<in Param, out Result>

interface UseCaseWithoutParams<out Result>

interface FlowUseCase<in Param, out Result> : UseCase<Param, Flow<Result>> {
    fun execute(param: Param): Flow<Result>
    fun executeNow(param: Param): Result = suspendCall {
        execute(param).single()
    }
    operator fun invoke(param: Param) = execute(param)
}

interface FlowUseCaseWithoutParams<out Result> : UseCaseWithoutParams<Flow<Result>> {
    fun execute(): Flow<Result>
    fun executeNow(): Result = suspendCall {
        execute().single()
    }
    operator fun invoke() = execute()
}

interface SuspendableUseCase<in Param, out Result : Any> : UseCase<Param, Result> {
    suspend fun execute(param: Param): Result
    operator suspend fun invoke(param: Param) = execute(param)
}

interface SuspendableUseCaseWithoutParams<in Param, out Result : Any> : UseCase<Param, Result> {
    suspend fun execute(): Result
    operator suspend fun invoke() = execute()
}

interface NormalUseCase<in Param, out Result : Any> : UseCase<Param, Result> {
    fun execute(param: Param): Result
    operator fun invoke(param: Param) = execute(param)
}

interface NetworkUseCase<in Param, out Result : Any> : SuspendableUseCase<Param, Result> {
    suspend fun networkCall(param: Param): NetworkResponse<Result>

    override suspend fun execute(param: Param): Result =
        when (val response = networkCall(param))
        {
            is NetworkResponse.Success -> {
                val storeDataGrouping = response.body
                storeDataGrouping
            }
            is NetworkResponse.NetworkError ->
                throw Exception(response.error.message)
            is NetworkResponse.ServerError ->
                throw Exception(response.code.toString())
            is NetworkResponse.UnknownError ->
                throw Exception(response.error)
            else ->
                throw Exception()
        }
}
