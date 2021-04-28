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
abstract class MviViewModel<State: Any, Action: Any>(
    val initialState: State
) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    private val stateMutex = Mutex()
    private val _pendingActions = MutableSharedFlow<Action>()

    val state: StateFlow<State>
        get() = _state.asStateFlow()

    val pendingActions: SharedFlow<Action>
        get() = _pendingActions.asSharedFlow()

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
        launch { this@MviViewModel.setState(reducer) }
    }

    protected fun CoroutineScope.withState(block: (State) -> Unit) {
        launch { this@MviViewModel.withState(block) }
    }

    protected fun <A> selectSubscribe(prop1: KProperty1<State, A>, block: (A) -> Unit) {
        viewModelScope.launch {
            selectSubscribe(prop1).collect { block(it) }
        }
    }

    private fun <A> selectSubscribe(prop1: KProperty1<State, A>): Flow<A> {
        return _state.map { prop1.get(it) }.distinctUntilChanged()
    }

    abstract suspend fun performAction(action: Action)

    fun submitAction(action: Action) {
        viewModelScope.launch { _pendingActions.emit(action) }
    }

    init {
        _pendingActions
            .onEach(::performAction)
            .launchIn(viewModelScope)
    }
}

@OptIn(InternalCoroutinesApi::class)
abstract class MvieViewModel<State: Any, Event: Any, Action: Any>(
    initialState: State
) : MviViewModel<State, Action>(initialState) {
    private val _events = MutableSharedFlow<Event>()

    val events: SharedFlow<Event>
        get() = _events.asSharedFlow()

    fun emitEvent(event: Event) {
        viewModelScope.launch { _events.emit(event) }
    }
}