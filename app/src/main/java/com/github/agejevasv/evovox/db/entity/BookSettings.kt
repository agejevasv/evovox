package com.github.agejevasv.evovox.db.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE


@Entity(
    indices = [
        Index(value = ["bookId"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("bookId"),
            onDelete = CASCADE
        )
    ],
    tableName = "book_settings"
)
data class BookSettings(
    @PrimaryKey val bookId: Long,
    @ColumnInfo(name = "timeInFileMs") var timeInFileMs: Long = 0L,
    @ColumnInfo(name = "filePositionNumber") var filePositionNumber: Int = 0,
    @ColumnInfo(name = "speed") var speed: Float = 1F
)