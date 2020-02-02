package com.github.agejevasv.evovox.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.agejevasv.evovox.db.dao.AudiobookDirDao
import com.github.agejevasv.evovox.db.dao.BookDao
import com.github.agejevasv.evovox.db.entity.AudiobookDir
import com.github.agejevasv.evovox.db.entity.Book
import com.github.agejevasv.evovox.db.entity.BookFile

@Database(
    entities = [
        AudiobookDir::class,
        Book::class,
        BookFile::class
    ],
    version = 12
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun audiobookDirDao(): AudiobookDirDao
    abstract fun bookDao(): BookDao
}
