package com.github.agejevasv.evovox.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = arrayOf(
        Index(value = ["dir"], unique = true)
    )
)
data class AudiobookDir(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "dir") val dir: String?
)