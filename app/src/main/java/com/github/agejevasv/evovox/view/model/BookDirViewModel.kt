package com.github.agejevasv.evovox.view.model


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.github.agejevasv.evovox.db.Db
import com.github.agejevasv.evovox.entity.AudiobookDir
import javax.inject.Inject


class BookDirViewModel @Inject constructor(var app: Context) : ViewModel() {

    fun getDirs(): LiveData<List<AudiobookDir>> {
        return Db.getInstance(app).db().audiobookDirDao().getAllLive()
    }
}