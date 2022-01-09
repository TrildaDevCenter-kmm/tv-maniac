package com.thomaskioko.tvmaniac.injection

import android.content.Context
import com.thomaskioko.tvmaniac.core.db.DriverFactory
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabaseFactory
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.datasource.cache.episode.EpisodesCache
import com.thomaskioko.tvmaniac.datasource.cache.episode.EpisodesCacheImpl
import com.thomaskioko.tvmaniac.datasource.cache.genre.GenreCache
import com.thomaskioko.tvmaniac.datasource.cache.genre.GenreCacheImpl
import com.thomaskioko.tvmaniac.datasource.cache.seasons.SeasonsCache
import com.thomaskioko.tvmaniac.datasource.cache.seasons.SeasonsCacheImpl
import com.thomaskioko.tvmaniac.datasource.cache.trailers.TrailerCache
import com.thomaskioko.tvmaniac.datasource.cache.trailers.TrailerCacheImpl
import com.thomaskioko.tvmaniac.discover.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.discover.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.discover.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.discover.implementation.cache.CategoryCacheImpl
import com.thomaskioko.tvmaniac.discover.implementation.cache.ShowCategoryCacheImpl
import com.thomaskioko.tvmaniac.discover.implementation.cache.TvShowCacheImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDriverFactory(@ApplicationContext context: Context): DriverFactory {
        return DriverFactory(context = context)
    }

    @Singleton
    @Provides
    fun provideTvShowDatabase(driverFactory: DriverFactory): TvManiacDatabase {
        return TvManiacDatabaseFactory(driverFactory).createDatabase()
    }

    @Singleton
    @Provides
    fun provideTvShowCache(database: TvManiacDatabase): TvShowCache {
        return TvShowCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideTvShowSeasonCache(database: TvManiacDatabase): SeasonsCache {
        return SeasonsCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideEpisodesCache(database: TvManiacDatabase): EpisodesCache {
        return EpisodesCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideGenreCache(database: TvManiacDatabase): GenreCache {
        return GenreCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideTrailerCache(database: TvManiacDatabase): TrailerCache {
        return TrailerCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideCategoryCache(database: TvManiacDatabase): CategoryCache {
        return CategoryCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideShowCategoryCache(database: TvManiacDatabase): ShowCategoryCache {
        return ShowCategoryCacheImpl(database)
    }
}
