package com.github.agejevasv.evovox.io

import android.content.Context
import android.net.Uri
import com.github.agejevasv.evovox.db.AppDatabase
import com.github.agejevasv.evovox.db.entity.Book
import com.github.agejevasv.evovox.db.entity.BookFile
import com.github.agejevasv.evovox.db.entity.BookSettings
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import java.io.FileFilter
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.measureTimeMillis


@Singleton
class BookIndexer @Inject constructor(val db: AppDatabase, private val ctx: Context, private val dataSourceFactory: DefaultDataSourceFactory) {
    private val bookIndexerContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val bookMetadataContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private var bookIndexing: Job? = null
    private var metadataScan: Job? = null

    fun scanBooks() {
        GlobalScope.launch {
            bookIndexing?.cancelAndJoin()
            bookIndexing = launch(bookIndexerContext) {
                Timber.i("Scanning books")

                measureTimeMillis {
                    val dirs = bookDirs()
                    clearOrphans(dirs.map { it.absolutePath })

                    for (dir in dirs) {
                        if (!dir.isDirectory) {
                            Timber.i("$dir is not a directory or does not exist")
                            continue
                        }

                        dir.files(FileFilters.folderOrAudio).sortedArrayWith(NaturalFileNameComparator).forEach {
                            val book = db.bookDao().findBookByDir(it.absolutePath)

                            val info = guessAuthorAndTitle(it.nameWithoutExtension)

                            val bookId = if (book == null) {
                                val id = db.bookDao().insertBook(
                                    Book(
                                        dir = it.absolutePath,
                                        parentDir = it.parent,
                                        title = info[1],
                                        author = info[0]
                                    )
                                )

                                db.bookDao().insertBookSettings(
                                    BookSettings(bookId = id)
                                )

                                id
                            } else {
                                book.id
                            }

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

                            scanDuration(bookId)
                        }
                    }
                }.also {
                    Timber.i("Scanning took %d ms", it)
                }
            }
        }
    }

    private fun scanDuration(bookId: Long) {
        GlobalScope.launch {
            metadataScan = launch(bookMetadataContext) {
                Timber.i("Duration scan for book: $bookId")

                val book = db.bookDao().get(bookId.toString())!!

                if (book.durationInSeconds != 0L) {
                    Timber.i("Duration is already scanned for $book")
                    coroutineContext.cancel()
                }

                val files = db.bookDao().getBookFiles(book.id)

                val list: Array<MediaSource> = files.map {
                    ProgressiveMediaSource
                        .Factory(dataSourceFactory)
                        .createMediaSource(Uri.fromFile(File(it.fileName!!)))
                }.toTypedArray()

                launch(Dispatchers.Main) {
                    val player = SimpleExoPlayer
                        .Builder(ctx)
                        .setTrackSelector(DefaultTrackSelector(ctx))
                        .build()
                    player.prepare(ConcatenatingMediaSource(*list), true, true)

                    var idx = 0
                    player.addListener(object : Player.EventListener {
                        override fun onPlayerStateChanged(
                            playWhenReady: Boolean,
                            playbackState: Int
                        ) {
                            super.onPlayerStateChanged(playWhenReady, playbackState)
                            if (playbackState == Player.STATE_READY) {
                                if (player.duration != C.TIME_UNSET) {
                                    files[idx].durationMs = player.duration
                                }

                                if (player.hasNext()) {
                                    player.seekTo(++idx, 0)
                                } else {
                                    Timber.i("Duration scan completed for: $bookId")
                                    storeDuration(book, files)
                                    player.release()
                                }
                            }
                        }
                    })
                }
            }
        }
    }

    private fun storeDuration(book: Book, files: List<BookFile>) {
        GlobalScope.launch {
            launch(Dispatchers.IO) {
                Timber.i("Saving book durations: $book.id")
                db.bookDao().updateBookFiles(files)

                book.durationInSeconds = files.sumBy {
                    it.durationMs.toInt() / 1000
                }.toLong()
                book.visible = true

                db.bookDao().updateBooks(book)
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

        file.files(FileFilters.folderOrAudio).sortedArrayWith(NaturalFileNameComparator).forEach {
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