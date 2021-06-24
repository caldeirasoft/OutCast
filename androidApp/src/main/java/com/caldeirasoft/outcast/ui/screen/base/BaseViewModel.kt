package com.caldeirasoft.outcast.ui.screen.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KProperty1

/**
 * @param State The type which is emitted from this viewmodel's [state] flow
 * @param Event The type which is emitted from this viewmodel's [events] flow
 * @param Action The type which is accepted by this viewmodel's [performAction] function
 */
@OptIn(InternalCoroutinesApi::class)
abstract class BaseViewModel<State: Any, Event: Any, Action: Any>(
    val initialState: State
) : ViewModel() {
    private val stateMutex = Mutex()

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State>
        get() = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event>
        get() = _events.asSharedFlow()

    abstract suspend fun performAction(action: Action)

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

    protected suspend fun withState(block: suspend (State) -> Unit) {
        stateMutex.withLock {
            block(_state.value)
        }
    }

    protected fun CoroutineScope.setState(reducer: State.() -> State) {
        launch { this@BaseViewModel.setState(reducer) }
    }

    protected fun CoroutineScope.withState(block: suspend (State) -> Unit) {
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

    fun emitEvent(event: Event) {
        viewModelScope.launch { _events.emit(event) }
    }

    abstract fun activate()
}
