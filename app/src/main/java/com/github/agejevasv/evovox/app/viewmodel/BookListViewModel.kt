package com.github.agejevasv.evovox.app.viewmodel


import androidx.lifecycle.ViewModel
import com.github.agejevasv.evovox.db.AppDatabase
import javax.inject.Inject


class BookListViewModel @Inject constructor(var db: AppDatabase) : ViewModel() {

    fun getDirs() = db.audiobookDirDao().getAllLive()

    fun getBooks() = db.bookDao().getAllBooksLive()
}