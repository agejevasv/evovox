package com.github.agejevasv.evovox.dagger

import android.content.Context
import com.github.agejevasv.evovox.view.model.BookDirViewModel
import dagger.Module
import dagger.Provides

@Module
class BookDirViewModelModule {

    @Provides
    fun provideBookDirViewModel(app: Context): BookDirViewModel = BookDirViewModel(app)
}