package com.github.agejevasv.evovox.app.fragment


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.github.agejevasv.evovox.EvovoxApplication
import com.github.agejevasv.evovox.R
import com.github.agejevasv.evovox.app.ui.Spinner
import com.github.agejevasv.evovox.db.AppDatabase
import com.github.agejevasv.evovox.db.entity.BookSettings
import com.github.agejevasv.evovox.db.entity.BookWithFiles
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.book_detail.view.*
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class BookDetailFragment : Fragment() {
    @Inject lateinit var db: AppDatabase
    @Inject lateinit var player: SimpleExoPlayer
    @Inject lateinit var dataSourceFactory: DefaultDataSourceFactory

    lateinit var id: String

    private var book: BookWithFiles? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        ACTIVITY = context as Activity

        EvovoxApplication.appComponent.inject(this)

        arguments?.let {
            if (it.containsKey(ARG_KEY_BOOK_ID)) {
                id = it.getString(ARG_KEY_BOOK_ID)!!
            }
        }
    }

    private fun saveProgress(bookId: String?, fileNumber: Int, timeMs: Long, speed: Float) {
        if (bookId == null) {
            return
        }

        GlobalScope.launch(Dispatchers.Main) {
            val book: BookWithFiles? = getBook(bookId)

            if (book == null) {
                Timber.i("Canceling saveProgress because book is null")
                coroutineContext.cancel()
            }

            val settings = book?.settings

            settings?.filePositionNumber = fileNumber
            settings?.timeInFileMs = timeMs
            settings?.speed = speed

            if (settings != null) {
                updateSettings(settings)
            }
        }
     }

    private suspend fun getBook(id: String) =
        withContext(Dispatchers.IO) {
            db.bookDao().getWithFiles(id)
        }

    private suspend fun updateSettings(settings: BookSettings) {
        withContext(Dispatchers.IO) {
            Timber.i("Storing settings: $settings")
            db.bookDao().updateBookSettings(settings)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.book_detail, container, false)
        val bookFiles = ArrayList<String>()
        val adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, bookFiles)
        rootView.book_files_list.adapter = adapter

        GlobalScope.launch(Dispatchers.Main) {
            book = getBook(id)

            book?.let {
                preparePlayer(it)

                val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
                toolbar?.title = it.book.title

                bookFiles.addAll(it.sortedFiles().map { File(it.fileName!!).name })
                adapter.notifyDataSetChanged()

                val chapters = activity?.findViewById<Spinner>(R.id.book_files_list)
                chapters?.setOnItemSelectedListener(itemSelectedListener())
                chapters?.setSelection(it.settings.filePositionNumber)

                val playerView: PlayerControlView? = view?.findViewById(R.id.player)
                playerView?.setShowMultiWindowTimeBar(true)
                playerView?.player = player
            }
        }

        return rootView
    }

    private fun preparePlayer(book: BookWithFiles) {
        if (CURRENT_BOOK_ID != id) {
            player.playWhenReady = false

            val listener = playerEventListener()
            player.removeListener(listener)
            player.addListener(listener)

            CURRENT_BOOK_ID = id

            player.prepare(ConcatenatingMediaSource(*mediaSource(book).toTypedArray()), true, true)
            player.setPlaybackParameters(PlaybackParameters(book.settings.speed))
            player.seekTo(book.settings.filePositionNumber, book.settings.timeInFileMs)
        }
    }

    private fun mediaSource(book: BookWithFiles): List<MediaSource> =
        book.sortedFiles().map {
            ProgressiveMediaSource
                .Factory(dataSourceFactory, DefaultExtractorsFactory().setConstantBitrateSeekingEnabled(true))
                .createMediaSource(Uri.fromFile(File(it.fileName!!)))
        }

    private fun itemSelectedListener() = object: Spinner.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long,
            userSelected: Boolean
        ) {
            if (userSelected && player.currentPeriodIndex != position) {
                player.seekTo(position, 0L)
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }

    private fun playerEventListener() = object: Player.EventListener {
        private var positionDisposable: Disposable? = null

        override fun onPositionDiscontinuity(reason: Int) {
            super.onPositionDiscontinuity(reason)
            saveProgress(
                id,
                player.currentWindowIndex,
                player.currentPosition,
                player.playbackParameters.speed
            )
            ACTIVITY
                ?.findViewById<Spinner>(R.id.book_files_list)
                ?.setPosition(player.currentWindowIndex, false, false)

        }

        @SuppressLint("CheckResult")
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)

            if (isPlaying && (positionDisposable == null || positionDisposable?.isDisposed == true)) {
                positionDisposable = Observable
                    .interval(444L, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .subscribe {
                        saveProgress(
                            id,
                            player.currentWindowIndex,
                            player.currentPosition,
                            player.playbackParameters.speed
                        )
                    }
            } else {
                positionDisposable?.dispose()
                positionDisposable = null
            }
        }

        override fun equals(other: Any?): Boolean {
            return hashCode() == other.hashCode()
        }

        override fun hashCode(): Int {
            return 42
        }
    }

    override fun onDetach() {
        super.onDetach()
        book = null
        ACTIVITY = null
    }

    companion object {
        const val ARG_KEY_BOOK_ID = "bookId"
        var CURRENT_BOOK_ID: String? = null
        var ACTIVITY: Activity? = null
    }
}
