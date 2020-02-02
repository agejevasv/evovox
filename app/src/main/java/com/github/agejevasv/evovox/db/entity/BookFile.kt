package com.github.agejevasv.evovox.db.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.io.File


@Entity(
    indices = [
        Index(value = ["fileName"], unique = true),
        Index(value = ["bookId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("bookId"),
            onDelete = CASCADE
        )
    ]
)
data class BookFile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "bookId") val bookId: Long?,
    @ColumnInfo(name = "fileName") val fileName: String?
) {
    fun file() = if (fileName != null) File(fileName) else null
}