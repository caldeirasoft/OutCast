package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.StoreGroupingData
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import java.security.InvalidParameterException
import javax.inject.Inject

interface UseCase<in Param, out Result>

interface UseCaseWithoutParams<out Result>

interface FlowUseCase<in Param, out Result> : UseCase<Param, Flow<Result>> {
    fun execute(param: Param): Flow<Result>
    fun executeNow(param: Param): Result = runBlocking {
        execute(param).single()
    }
    operator fun invoke(param: Param) = execute(param)
}

interface FlowUseCaseWithoutParams<out Result> : UseCaseWithoutParams<Flow<Result>> {
    fun execute(): Flow<Result>
    fun executeNow(): Result = runBlocking {
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

abstract class NetworkUseCase<in Param, out Result : Any>(
    private val fetchNetworkCall: suspend (Param) -> NetworkResponse<Result>,
) : SuspendableUseCase<Param, Result> {
    override suspend fun execute(param: Param): Result =
        when (val response = fetchNetworkCall.invoke(param))
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
                throw InvalidParameterException()
        }
}