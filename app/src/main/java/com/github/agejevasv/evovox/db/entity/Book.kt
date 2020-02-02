package com.github.agejevasv.evovox.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = arrayOf(
        Index(value = ["dir"], unique = true),
        Index(value = ["parentDir"])
    )
)
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "parentDir") val parentDir: String?,
    @ColumnInfo(name = "dir") val dir: String?,
    @ColumnInfo(name = "author") val author: String = "Unknown",
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "duration") val duration: Long = 0,
    @ColumnInfo(name = "progress") val progress: Long = 0
)