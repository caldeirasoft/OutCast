package com.caldeirasoft.outcast.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KProperty1

@OptIn(InternalCoroutinesApi::class)
abstract class BaseViewModel<State: Any>(
    val initialState: State
) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    private val stateMutex = Mutex()

    val state: StateFlow<State>
        get() = _state.asStateFlow()

    @OptIn(InternalCoroutinesApi::class)
    protected fun <T> Flow<T>.setOnEach(reducer: State.(T) -> State) {
        onEach {
            setState {
                reducer(it)
            }
        }
            .launchIn(viewModelScope)
    }

    protected suspend fun setState(reducer: State.() -> State) {
        stateMutex.withLock {
            _state.value = reducer(_state.value)
        }
    }

    protected suspend fun withState(block: (State) -> Unit) {
        stateMutex.withLock {
            block(_state.value)
        }
    }

    protected fun CoroutineScope.setState(reducer: State.() -> State) {
        launch { this@BaseViewModel.setState(reducer) }
    }

    protected fun CoroutineScope.withState(block: (State) -> Unit) {
        launch { this@BaseViewModel.withState(block) }
    }

    protected fun <A> selectSubscribe(prop1: KProperty1<State, A>, block: (A) -> Unit) {
        viewModelScope.launch {
            selectSubscribe(prop1).collect { block(it) }
        }
    }

    protected fun <A> selectSubscribe(prop1: KProperty1<State, A>): Flow<A> {
        return _state.map { prop1.get(it) }.distinctUntilChanged()
    }
}

@OptIn(InternalCoroutinesApi::class)
abstract class BaseViewModelEvents<State: Any, Event: Any>(
    initialState: State
) : BaseViewModel<State>(initialState) {
    private val _events = MutableSharedFlow<Event>()

    val events: SharedFlow<Event>
        get() = _events.asSharedFlow()

    fun emitEvent(event: Event) {
        viewModelScope.launch { _events.emit(event) }
    }
}