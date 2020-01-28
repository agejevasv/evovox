package com.github.agejevasv.evovox.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.github.agejevasv.evovox.entity.AudiobookDir

@Dao
interface AudiobookDirDao {
    @Query("SELECT * FROM audiobookdir")
    fun getAllLive(): LiveData<List<AudiobookDir>>

    @Query("SELECT * FROM audiobookdir WHERE id IN (:ids)")
    fun loadAllByIdsLive(ids: IntArray): LiveData<List<AudiobookDir>>

    @Query("SELECT * FROM audiobookdir WHERE dir LIKE :dir LIMIT 1")
    fun findByNameLive(dir: String): LiveData<AudiobookDir>

    @Query("SELECT * FROM audiobookdir")
    fun getAll(): List<AudiobookDir>

    @Query("SELECT * FROM audiobookdir WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<AudiobookDir>

    @Query("SELECT * FROM audiobookdir WHERE dir LIKE :dir LIMIT 1")
    fun findByName(dir: String): AudiobookDir

    @Insert
    fun insertAll(vararg audiobookDirs: AudiobookDir)

    @Delete
    fun delete(audiobookDir: AudiobookDir)
}