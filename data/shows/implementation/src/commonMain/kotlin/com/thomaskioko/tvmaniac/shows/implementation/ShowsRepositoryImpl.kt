package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.category.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.category.api.model.Category.ANTICIPATED
import com.thomaskioko.tvmaniac.category.api.model.Category.FEATURED
import com.thomaskioko.tvmaniac.category.api.model.Category.POPULAR
import com.thomaskioko.tvmaniac.category.api.model.Category.TRENDING
import com.thomaskioko.tvmaniac.category.api.model.getCategory
import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.DefaultError
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.core.networkutil.networkBoundResult
import com.thomaskioko.tvmaniac.shows.api.ShowsDao
import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.ErrorResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.util.ExceptionHandler
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
class ShowsRepositoryImpl constructor(
    private val showsDao: ShowsDao,
    private val categoryCache: CategoryCache,
    private val traktRemoteDataSource: TraktRemoteDataSource,
    private val mapper: ShowsResponseMapper,
    private val exceptionHandler: ExceptionHandler,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: KermitLogger,
) : ShowsRepository {

    override fun observeShow(traktId: Long): Flow<Either<Failure, SelectByShowId>> =
        networkBoundResult(
            query = { showsDao.observeTvShow(traktId) },
            shouldFetch = { it == null },
            fetch = { traktRemoteDataSource.getSeasonDetails(traktId) },
            saveFetchResult = { mapAndCache(it) },
            exceptionHandler = exceptionHandler,
            coroutineDispatcher = dispatchers.io,
        )

    override fun observeCachedShows(categoryId: Long): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        showsDao.observeCachedShows(categoryId.getCategory().id)
            .map { Either.Right(it) }
            .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }

    override fun fetchTrendingShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        networkBoundResult(
            query = { showsDao.observeCachedShows(TRENDING.id) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { mapper.showsResponseToCacheList(traktRemoteDataSource.getTrendingShows()) },
            saveFetchResult = { cacheResult(it, TRENDING.id) },
            exceptionHandler = exceptionHandler,
            coroutineDispatcher = dispatchers.io,
        )

    override fun observeTrendingCachedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        showsDao.observeCachedShows(TRENDING.id)
            .map { Either.Right(it) }
            .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }

    override fun fetchPopularShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        networkBoundResult(
            query = { showsDao.observeCachedShows(POPULAR.id) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { mapper.showResponseToCacheList(traktRemoteDataSource.getPopularShows()) },
            saveFetchResult = { cacheResult(it, POPULAR.id) },
            exceptionHandler = exceptionHandler,
            coroutineDispatcher = dispatchers.io,
        )

    override fun observePopularCachedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        showsDao.observeCachedShows(POPULAR.id)
            .map { Either.Right(it) }
            .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }

    override fun fetchAnticipatedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        networkBoundResult(
            query = { showsDao.observeCachedShows(ANTICIPATED.id) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { mapper.showsResponseToCacheList(traktRemoteDataSource.getAnticipatedShows()) },
            saveFetchResult = { cacheResult(it, ANTICIPATED.id) },
            exceptionHandler = exceptionHandler,
            coroutineDispatcher = dispatchers.io,
        )

    override fun observeAnticipatedCachedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        showsDao.observeCachedShows(ANTICIPATED.id)
            .map { Either.Right(it) }
            .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }

    override fun fetchFeaturedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        networkBoundResult(
            query = { showsDao.observeCachedShows(FEATURED.id) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { mapper.showsResponseToCacheList(traktRemoteDataSource.getRecommendedShows(period = "daily")) },
            saveFetchResult = { cacheResult(it, FEATURED.id) },
            exceptionHandler = exceptionHandler,
            coroutineDispatcher = dispatchers.io,
        )

    override fun observeFeaturedCachedShows(): Flow<Either<Failure, List<SelectShowsByCategory>>> =
        showsDao.observeCachedShows(FEATURED.id)
            .map { Either.Right(it) }
            .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }

    override suspend fun fetchShows() {
        val categories = listOf(TRENDING, POPULAR, ANTICIPATED, FEATURED)

        categories.forEach {
            val mappedResult = fetchShowsAndMapResult(it.id)

            showsDao.insert(mappedResult)
            categoryCache.insert(mapper.toCategoryCache(mappedResult, it.id))
        }
    }

    private suspend fun fetchShowsAndMapResult(categoryId: Long): List<Show> =
        when (categoryId) {
            POPULAR.id -> mapper.showResponseToCacheList(traktRemoteDataSource.getPopularShows())
            TRENDING.id -> mapper.showsResponseToCacheList(traktRemoteDataSource.getTrendingShows())
            ANTICIPATED.id -> mapper.showsResponseToCacheList(traktRemoteDataSource.getAnticipatedShows())
            FEATURED.id -> mapper.showsResponseToCacheList(traktRemoteDataSource.getRecommendedShows(period = "daily"))

            else -> throw Throwable("Unsupported type sunny")
        }

    private fun cacheResult(result: List<Show>, categoryId: Long) {
        showsDao.insert(result)

        categoryCache.insert(mapper.toCategoryCache(result, categoryId))
    }

    private fun mapAndCache(response: ApiResponse<TraktShowResponse, ErrorResponse>) {
        when (response) {
            is ApiResponse.Success -> {
                showsDao.insert(mapper.responseToCache(response.body))
            }

            is ApiResponse.Error.GenericError -> {
                logger.error("observeShow", "$this")
                throw Throwable("${response.errorMessage}")
            }

            is ApiResponse.Error.HttpError -> {
                logger.error("observeShow", "$this")
                throw Throwable("${response.code} - ${response.errorBody?.message}")
            }

            is ApiResponse.Error.SerializationError -> {
                logger.error("observeShow", "$this")
                throw Throwable("$response")
            }
        }
    }
}