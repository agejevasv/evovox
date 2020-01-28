package com.github.agejevasv.evovox.dagger

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.github.agejevasv.evovox.db.AppDatabase
import com.github.agejevasv.evovox.view.adapter.AudiobookDirListAdapter
import com.github.agejevasv.evovox.view.model.BookDirViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {
    @Provides
    @Singleton
    fun provideContext(): Context = app

    @Provides
    fun provideBookDirViewModel(db: AppDatabase): BookDirViewModel = BookDirViewModel(db)

    @Provides
    @Singleton
    fun provideAppDatabase(): AppDatabase {
        return Room
            .databaseBuilder(app, AppDatabase::class.java, "evovox")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAudiobookDirListAdapter(db: AppDatabase): AudiobookDirListAdapter = AudiobookDirListAdapter(db)
}