package com.github.agejevasv.evovox.dagger

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.github.agejevasv.evovox.db.AppDatabase
import com.github.agejevasv.evovox.io.BookIndexer
import com.github.agejevasv.evovox.app.adapter.AudiobookDirListAdapter
import com.github.agejevasv.evovox.app.adapter.BookListAdapter
import com.github.agejevasv.evovox.app.viewmodel.BookListViewModel
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {
    @Provides
    @Singleton
    fun provideContext(): Context = app

    @Provides
    fun provideBookDirViewModel(db: AppDatabase): BookListViewModel = BookListViewModel(db)

    @Provides
    @Singleton
    fun provideAppDatabase(): AppDatabase {
        return Room
            .databaseBuilder(app, AppDatabase::class.java, "evovox")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAudiobookDirListAdapter(db: AppDatabase, indexer: BookIndexer) =
        AudiobookDirListAdapter(db, indexer)

    @Provides
    fun provideBookListAdapter(db: AppDatabase, ctx: Context) = BookListAdapter(db, ctx)

    @Provides
    @Singleton
    fun providesBookIndexer(db: AppDatabase) = BookIndexer(db)

    @Provides
    @Singleton
    fun providesSimpleExoPlayer(): SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(app, DefaultTrackSelector())

    @Provides
    @Singleton
    fun providesDefaultDataSourceFactory() = DefaultDataSourceFactory(app, Util.getUserAgent(app, "EvoVox"))
}