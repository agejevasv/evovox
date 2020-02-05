package com.github.agejevasv.evovox.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

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
    @ColumnInfo(name = "author") var author: String = "Unknown",
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "createdAt") var createdAt: Long = Date().time,
    @ColumnInfo(name = "durationInSeconds") var durationInSeconds: Long = 0,
    @ColumnInfo(name = "visible") var visible: Boolean = false
)