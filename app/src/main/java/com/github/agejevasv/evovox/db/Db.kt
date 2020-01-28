package com.github.agejevasv.evovox.db

import android.content.Context
import androidx.room.Room
import com.github.agejevasv.evovox.SingletonHolder

class Db private constructor(context: Context) {
    private val db: AppDatabase

    init {
        db = Room
            .databaseBuilder(context, AppDatabase::class.java, "evovox")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun db(): AppDatabase {
        return db
    }

    companion object : SingletonHolder<Db, Context>(::Db)
}