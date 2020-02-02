package com.github.agejevasv.evovox.io

import com.github.agejevasv.evovox.db.AppDatabase
import com.github.agejevasv.evovox.db.entity.Book
import com.github.agejevasv.evovox.db.entity.BookFile
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import java.io.FileFilter
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.measureTimeMillis


@Singleton
class BookIndexer @Inject constructor(val db: AppDatabase) {
    private val bookIndexerContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private var job: Job? = null

    fun scanBooks() {
        GlobalScope.launch {
            job?.cancelAndJoin()
            job = launch(bookIndexerContext) {
                Timber.i("Scanning books")

                measureTimeMillis {
                    val dirs = bookDirs()
                    clearOrphans(dirs.map { it.absolutePath })

                    for (dir in dirs) {
                        if (!dir.isDirectory) {
                            Timber.i("$dir is not a directory or does not exist")
                            continue
                        }

                        dir.files(FileFilters.folderOrAudio).forEach {
                            val book = db.bookDao().findBookByDir(it.absolutePath)

                            val info = guessAuthorAndTitle(it.nameWithoutExtension)
                            val bookId = book?.id ?: db.bookDao().insertBook(
                                Book(
                                    dir = it.absolutePath,
                                    parentDir = it.parent,
                                    title = info[1],
                                    author = info[0]
                                )
                            )

                            val files = if (it.isDirectory) flattenFiles(it) else listOf(it)

                            files.forEach {
                                val bookFile = db.bookDao().findBookFilesByFileName(it.absolutePath)

                                if (bookFile == null) {
                                    db.bookDao().insertBookFile(
                                        BookFile(
                                            bookId = bookId,
                                            fileName = it.absolutePath
                                        )
                                    )
                                }
                            }
                        }
                    }
                }.also {
                    Timber.i("Scanning took %d ms", it)
                }
            }
        }
    }

    private fun clearOrphans(dirs: List<String>) {
        db.bookDao().deleteBooksByIds(
            db.bookDao().getAllBooks()
                .filter {
                    !File(it.dir!!).exists() || !dirs.contains(it.parentDir!!)
                }.map {
                    it.id
                }
        )

        db.bookDao().deleteBookFilesByIds(
            db.bookDao().getAllBookFiles()
                .filter {
                    !File(it.fileName).exists()
                }.map {
                    it.id
                }
        )
    }

    private fun bookDirs() = db.audiobookDirDao().getAll().map { File(it.dir!!) }

    private fun guessAuthorAndTitle(fileName: String): List<String> {
        val guess =
            if (fileName.contains("-"))
                fileName.split("-", limit = 2)
            else
                listOf("Unknown", fileName)

        return guess.map { it.trim() }
    }

    private fun flattenFiles(file: File, files: List<File> = emptyList()): List<File> {
        val mutableFiles = files.toMutableList()

        file.files(FileFilters.folderOrAudio).forEach {
            if (it.isDirectory)
                mutableFiles.addAll(flattenFiles(it, emptyList()))
            else
                mutableFiles.add(it)
        }

        return mutableFiles
    }

    private fun File.files(filter: FileFilter? = null): Array<File> =
        (if (filter == null) listFiles() else listFiles(filter)) ?: emptyArray()
}