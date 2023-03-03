package com.thomaskioko.tvmaniac.following

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.domain.following.FollowingAction
import com.thomaskioko.tvmaniac.domain.following.FollowingState
import com.thomaskioko.tvmaniac.domain.following.FollowingStateMachine
import com.thomaskioko.tvmaniac.domain.following.LoadingShows
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowingViewModel @Inject constructor(
    private val stateMachine: FollowingStateMachine,
) : ViewModel() {

    val state: MutableStateFlow<FollowingState> = MutableStateFlow(LoadingShows)

    init {
        viewModelScope.launch {
            stateMachine.state.collect {
                    state.value = it
                }
        }
    }

    fun dispatch(action : FollowingAction) {
        viewModelScope.launch {
            stateMachine.dispatch(action)
        }
    }
}
