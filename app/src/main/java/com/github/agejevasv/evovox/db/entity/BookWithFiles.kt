package com.github.agejevasv.evovox.db.entity

import androidx.room.Embedded
import androidx.room.Relation
import java.io.File


data class BookWithFiles(
    @Embedded val book: Book,
    @Relation(
        parentColumn = "id",
        entityColumn = "bookId"
    )
    val files: List<BookFile>
)
