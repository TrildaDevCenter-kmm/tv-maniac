package com.thomaskioko.tvmaniac.episodeimages.implementation

import com.thomaskioko.tvmaniac.core.db.Episode_image
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.episodeimages.api.EpisodeImageDao
import com.thomaskioko.tvmaniac.episodeimages.api.EpisodeImageRepository
import com.thomaskioko.tvmaniac.tmdb.api.TmdbNetworkDataSource
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.NetworkExceptionHandler
import com.thomaskioko.tvmaniac.util.model.ApiResponse
import com.thomaskioko.tvmaniac.util.model.DefaultError
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
class EpisodeImageRepositoryImpl(
    private val tmdbNetworkDataSource: TmdbNetworkDataSource,
    private val episodeImageDao: EpisodeImageDao,
    private val formatterUtil: FormatterUtil,
    private val logger: KermitLogger,
    private val exceptionHandler: NetworkExceptionHandler,
) : EpisodeImageRepository {

    override fun updateEpisodeImage(): Flow<Either<Failure, Unit>> =
        episodeImageDao.observeEpisodeImage()
            .map { episode ->
                episode.forEach { episodeArt ->
                    episodeArt.tmdb_id?.let { tmdbId ->
                        val response = tmdbNetworkDataSource.getEpisodeDetails(
                            tmdbShow = tmdbId,
                            ssnNumber = episodeArt.season_number!!,
                            epNumber = episodeArt.episode_number.toLong(),
                        )

                        when (response) {
                            is ApiResponse.Success -> {
                                episodeImageDao.upsert(
                                    Episode_image(
                                        id = Id(id = response.body.id.toLong()),
                                        tmdb_id = Id(id = tmdbId),
                                        image_url = response.body.imageUrl?.let {
                                            formatterUtil.formatTmdbPosterPath(it)
                                        },
                                    ),
                                )
                            }

                            is ApiResponse.Error -> {
                                logger.error("updateEpisodeArtWork", "$response")
                            }
                        }
                    }
                }

                Either.Right(Unit)
            }
            .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }
}
