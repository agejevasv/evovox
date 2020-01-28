package com.github.agejevasv.evovox.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.agejevasv.evovox.dao.AudiobookDirDao
import com.github.agejevasv.evovox.entity.AudiobookDir

@Database(entities = arrayOf(AudiobookDir::class), version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun audiobookDirDao(): AudiobookDirDao
}
