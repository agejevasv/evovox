package com.github.agejevasv.evovox.view.model


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.github.agejevasv.evovox.db.AppDatabase
import com.github.agejevasv.evovox.entity.AudiobookDir
import javax.inject.Inject


class BookDirViewModel @Inject constructor(var db: AppDatabase) : ViewModel() {

    fun getDirs(): LiveData<List<AudiobookDir>> {
        return db.audiobookDirDao().getAllLive()
    }
}