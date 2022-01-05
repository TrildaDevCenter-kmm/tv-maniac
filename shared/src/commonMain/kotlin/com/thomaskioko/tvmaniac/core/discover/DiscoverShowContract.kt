package com.thomaskioko.tvmaniac.core.discover

import com.thomaskioko.tvmaniac.core.Action
import com.thomaskioko.tvmaniac.core.Effect
import com.thomaskioko.tvmaniac.core.State
import com.thomaskioko.tvmaniac.datasource.enums.ShowCategory
import com.thomaskioko.tvmaniac.presentation.model.ShowUiModel

data class DiscoverShowState(
    val isLoading: Boolean,
    val showData: DiscoverShowResult,
) : State {
    companion object {
        val Empty = DiscoverShowState(
            isLoading = true,
            showData = DiscoverShowResult.EMPTY,
        )
    }
}

sealed class DiscoverShowAction : Action {
    object LoadTvShows : DiscoverShowAction()
    data class Error(val message: String = "") : DiscoverShowAction()
}

sealed class DiscoverShowEffect : Effect {
    data class Error(val message: String = "") : DiscoverShowEffect()
}

data class DiscoverShowResult(
    val featuredShows: DiscoverShowsData,
    val trendingShows: DiscoverShowsData,
    val topRatedShows: DiscoverShowsData,
    val popularShows: DiscoverShowsData,
) {
    companion object {
        val EMPTY = DiscoverShowResult(
            featuredShows = DiscoverShowsData.EMPTY,
            trendingShows = DiscoverShowsData.EMPTY,
            topRatedShows = DiscoverShowsData.EMPTY,
            popularShows = DiscoverShowsData.EMPTY
        )
    }

    data class DiscoverShowsData(
        val isLoading: Boolean,
        val category: ShowCategory,
        val showUiModels: List<ShowUiModel>,
        val errorMessage: String? = null
    ) {

        companion object {
            val EMPTY = DiscoverShowsData(
                isLoading = true,
                category = ShowCategory.TOP_RATED,
                showUiModels = emptyList(),
                errorMessage = null
            )
        }
    }
}
