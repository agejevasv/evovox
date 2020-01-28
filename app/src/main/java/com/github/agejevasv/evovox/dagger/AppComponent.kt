package com.github.agejevasv.evovox.dagger

import com.github.agejevasv.evovox.activity.BookListActivity
import com.github.agejevasv.evovox.activity.DirectoryManagementActivity
import com.github.agejevasv.evovox.db.AppDatabase
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(target: BookListActivity)
    fun inject(target: DirectoryManagementActivity)
    fun inject(target: AppDatabase)
}