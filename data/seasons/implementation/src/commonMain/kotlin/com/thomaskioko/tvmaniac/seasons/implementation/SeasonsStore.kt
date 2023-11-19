package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.core.db.SeasonsByShowId
import com.thomaskioko.tvmaniac.resourcemanager.api.LastRequest
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.model.ApiResponse
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

@Inject
class SeasonsStore(
    private val remoteDataSource: TraktShowsRemoteDataSource,
    private val requestManagerRepository: RequestManagerRepository,
    private val seasonsDao: SeasonsDao,
    private val scope: AppCoroutineScope,
    private val logger: KermitLogger,
) : Store<Long, List<SeasonsByShowId>> by StoreBuilder.from(
    fetcher = Fetcher.of { id ->
        when (val response = remoteDataSource.getShowSeasons(id)) {
            is ApiResponse.Success -> response.body.toSeasonCacheList(id)
            is ApiResponse.Error.GenericError -> {
                logger.error("SeasonsStore GenericError", "$response")
                throw Throwable("${response.errorMessage}")
            }

            is ApiResponse.Error.HttpError -> {
                logger.error("SeasonsStore HttpError", "$response")
                throw Throwable("${response.code} - ${response.errorMessage}")
            }

            is ApiResponse.Error.SerializationError -> {
                logger.error("SeasonsStore SerializationError", "${response.errorMessage}")
                throw Throwable("${response.errorMessage}")
            }
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = seasonsDao::observeSeasonsByShowId,
        writer = { id, list ->

            seasonsDao.upsert(list)

            requestManagerRepository.insert(
                LastRequest(
                    id = list.first().id.id,
                    entityId = id,
                    requestType = "SEASON",
                    timestamp = Clock.System.now(),
                ),
            )
        },
        delete = seasonsDao::delete,
        deleteAll = seasonsDao::deleteAll,
    ),
)
    .scope(scope.io)
    .build()
