package com.github.agejevasv.evovox.dagger

import com.github.agejevasv.evovox.app.activity.BookDetailActivity
import com.github.agejevasv.evovox.app.activity.BookListActivity
import com.github.agejevasv.evovox.app.activity.DirectoryManagementActivity
import com.github.agejevasv.evovox.db.AppDatabase
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(target: BookListActivity)
    fun inject(target: DirectoryManagementActivity)
    fun inject(target: BookDetailActivity)
    fun inject(target: AppDatabase)
}