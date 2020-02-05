package com.github.agejevasv.evovox.db.entity

import androidx.room.Embedded
import androidx.room.Relation


data class BookWithFiles(
    @Embedded val book: Book,

    @Relation(
        parentColumn = "id",
        entityColumn = "bookId"
    )
    val files: List<BookFile>,

    @Relation(
        parentColumn = "id",
        entityColumn = "bookId"
    )
    val settings: BookSettings
) {

    fun sortedFiles() = files.sortedBy { it.id }

    fun progressInSeconds(): Long {
        val fileNumber = settings.filePositionNumber
        val fileProgress = settings.timeInFileMs / 1000

        return sortedFiles().take(fileNumber).sumBy { it.durationMs.toInt() / 1000 } + fileProgress
    }

    fun progressInPercent() =
        ((progressInSeconds() / book.durationInSeconds.toDouble()) * 100.0).toInt()

    fun durationInSeconds() =
        book.durationInSeconds
}