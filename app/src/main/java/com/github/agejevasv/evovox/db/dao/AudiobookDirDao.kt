package com.github.agejevasv.evovox.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.github.agejevasv.evovox.db.entity.AudiobookDir

@Dao
interface AudiobookDirDao {
    @Query("SELECT * FROM book_dirs")
    fun getAllLive(): LiveData<List<AudiobookDir>>

    @Query("SELECT * FROM book_dirs WHERE id IN (:ids)")
    fun loadAllByIdsLive(ids: LongArray): LiveData<List<AudiobookDir>>

    @Query("SELECT * FROM book_dirs WHERE dir LIKE :dir LIMIT 1")
    fun findByNameLive(dir: String): LiveData<AudiobookDir>

    @Query("SELECT * FROM book_dirs")
    fun getAll(): List<AudiobookDir>

    @Query("SELECT * FROM book_dirs WHERE id IN (:ids)")
    fun loadAllByIds(ids: LongArray): List<AudiobookDir>

    @Query("SELECT * FROM book_dirs WHERE dir LIKE :dir LIMIT 1")
    fun findByName(dir: String): AudiobookDir

    @Insert
    fun insertAll(vararg audiobookDirs: AudiobookDir)

    @Delete
    fun delete(audiobookDir: AudiobookDir)
}