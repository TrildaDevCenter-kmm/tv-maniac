package com.thomaskioko.tvmaniac.domain.following

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class FollowingStateMachine constructor(
    private val repository: ShowsRepository,
) : FlowReduxStateMachine<FollowingState, FollowingAction>(initialState = LoadingShows) {

    init {
        spec {
            inState<LoadingShows> {
                onEnter { state -> loadFollowedShows(state) }
            }

            inState<FollowingContent> {
                collectWhileInState(repository.observeFollowedShows()) { result, state ->
                    result.fold(
                        { state.override { ErrorLoadingShows(it.errorMessage) } },
                        { state.mutate { copy(list = it.followedShowList()) } }
                    )
                }
            }

            inState<ErrorLoadingShows> {
                on<ReloadFollowedShows> { _, state ->
                    state.override { LoadingShows }
                }
            }
        }
    }

    private  fun loadFollowedShows(state: State<LoadingShows>): ChangedState<FollowingState> {

        val result = repository.getFollowedShows()

        return state.override { FollowingContent(result.followedShowList()) }
    }
}

/**
 * A wrapper class around [FollowingStateMachine] handling `Flow` and suspend functions on iOS.
 */
class FollowingStateMachineWrapper(
    dispatcher: MainCoroutineDispatcher,
    private val stateMachine: FollowingStateMachine,
) {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + dispatcher)


    fun start(stateChangeListener: (FollowingState) -> Unit) {
        scope.launch {
            stateMachine.state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun dispatch(action: FollowingAction) {
        scope.launch {
            stateMachine.dispatch(action)
        }
    }

    fun cancel() {
        job.cancelChildren()
    }
}
