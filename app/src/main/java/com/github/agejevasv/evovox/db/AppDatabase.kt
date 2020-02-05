package com.github.agejevasv.evovox.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.agejevasv.evovox.db.dao.AudiobookDirDao
import com.github.agejevasv.evovox.db.dao.BookDao
import com.github.agejevasv.evovox.db.entity.AudiobookDir
import com.github.agejevasv.evovox.db.entity.Book
import com.github.agejevasv.evovox.db.entity.BookFile
import com.github.agejevasv.evovox.db.entity.BookSettings

@Database(
    entities = [
        AudiobookDir::class,
        Book::class,
        BookFile::class,
        BookSettings::class
    ],
    version = AppDatabase.VERSION
)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val VERSION = 8
        const val NAME = "evovox_books"
    }

    abstract fun audiobookDirDao(): AudiobookDirDao
    abstract fun bookDao(): BookDao
}
