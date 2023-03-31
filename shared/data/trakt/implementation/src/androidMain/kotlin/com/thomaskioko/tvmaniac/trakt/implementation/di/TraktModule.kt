package com.thomaskioko.tvmaniac.trakt.implementation.di

import org.koin.core.module.Module as KoinModule
import com.thomaskioko.tvmaniac.category.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.util.helper.DateUtilHelper
import com.thomaskioko.tvmaniac.core.util.scope.DefaultDispatcher
import com.thomaskioko.tvmaniac.core.util.scope.IoDispatcher
import com.thomaskioko.tvmaniac.trakt.api.TraktShowRepository
import com.thomaskioko.tvmaniac.trakt.api.cache.TraktFollowedCache
import com.thomaskioko.tvmaniac.trakt.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.trakt.implementation.TraktShowRepositoryImpl
import com.thomaskioko.tvmaniac.trakt.implementation.cache.TraktFollowedCacheImpl
import com.thomaskioko.tvmaniac.trakt.implementation.cache.TraktShowCacheImpl
import com.thomaskioko.tvmaniac.trakt.service.api.TraktService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.dsl.module
import javax.inject.Singleton

actual fun traktModule(): KoinModule = module {}

@Module
@InstallIn(SingletonComponent::class)
object TraktModule {

    @Singleton
    @Provides
    fun provideTraktFollowedCache(
        database: TvManiacDatabase,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): TraktFollowedCache = TraktFollowedCacheImpl(database, ioDispatcher)


    @Singleton
    @Provides
    fun provideTvShowCache(
        database: TvManiacDatabase,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): TvShowCache = TraktShowCacheImpl(database, ioDispatcher)

    @Singleton
    @Provides
    fun provideTraktRepository(
        tvShowCache: TvShowCache,
        followedCache: TraktFollowedCache,
        categoryCache: CategoryCache,
        dateUtilHelper: DateUtilHelper,
        traktService: TraktService,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): TraktShowRepository = TraktShowRepositoryImpl(
        tvShowCache,
        followedCache,
        categoryCache,
        traktService,
        dateUtilHelper,
        ioDispatcher,
    )

}
