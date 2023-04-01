package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.util.scope.DefaultDispatcher
import com.thomaskioko.tvmaniac.core.util.scope.IoDispatcher
import com.thomaskioko.tvmaniac.tmdb.api.ShowImageCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbRepository
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.shows.api.cache.ShowsCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {

    @Singleton
    @Provides
    fun provideShowImageCache(
        database: TvManiacDatabase,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): ShowImageCache = ShowImageCacheImpl(database, ioDispatcher)

    @Singleton
    @Provides
    fun provideTmdbRepository(
        tmdbService: TmdbService,
        showsCache: ShowsCache,
        imageCache: ShowImageCache,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): TmdbRepository = TmdbRepositoryImpl(
            apiService = tmdbService,
            showsCache = showsCache,
            imageCache = imageCache,
            dispatcher = ioDispatcher
        )

}