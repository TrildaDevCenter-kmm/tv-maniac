package com.thomaskioko.tvmaniac.data.showdetails.api

import com.thomaskioko.tvmaniac.core.db.TvshowDetails
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface ShowDetailsRepository {

    suspend fun getShowDetails(traktId: Long): TvshowDetails

    fun observeShowDetails(traktId: Long): Flow<Either<Failure, TvshowDetails>>
}