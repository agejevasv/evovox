package com.github.agejevasv.evovox.dagger

import com.github.agejevasv.evovox.activity.BookListActivity
import com.github.agejevasv.evovox.activity.DirectoryManagementActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, BookDirViewModelModule::class])
interface AppComponent {
    fun inject(target: BookListActivity)
    fun inject(target: DirectoryManagementActivity)
}