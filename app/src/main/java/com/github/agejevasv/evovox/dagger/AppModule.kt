package com.github.agejevasv.evovox.dagger

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.github.agejevasv.evovox.app.adapter.AudiobookDirListAdapter
import com.github.agejevasv.evovox.app.adapter.BookListAdapter
import com.github.agejevasv.evovox.app.fragment.BookDetailFragment
import com.github.agejevasv.evovox.app.viewmodel.BookListViewModel
import com.github.agejevasv.evovox.db.AppDatabase
import com.github.agejevasv.evovox.io.BookIndexer
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
    fun provideAppDatabase() =
        Room
            .databaseBuilder(app, AppDatabase::class.java, AppDatabase.NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideAudiobookDirListAdapter(db: AppDatabase, indexer: BookIndexer) =
        AudiobookDirListAdapter(db, indexer)

    @Provides
    fun provideBookListAdapter(db: AppDatabase) =
        BookListAdapter(db, app)

    @Provides
    @Singleton
    fun providesBookIndexer(db: AppDatabase, dsf: DefaultDataSourceFactory) =
        BookIndexer(db, app, dsf)

    @Provides
    @Singleton
    fun providesSimpleExoPlayer() =
        SimpleExoPlayer
            .Builder(app)
            .setTrackSelector(DefaultTrackSelector(app))
            .build()

    @Provides
    @Singleton
    fun providesDefaultDataSourceFactory() =
        DefaultDataSourceFactory(app, Util.getUserAgent(app, "EvoVox"))

    @Provides
    fun providesBookDetailFragment() = BookDetailFragment()

}