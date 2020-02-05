package com.github.agejevasv.evovox.app.fragment


import android.annotation.SuppressLint
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
import com.github.agejevasv.evovox.app.GlobalStore
import com.github.agejevasv.evovox.app.Spinner
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
        EvovoxApplication.appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                id = it.getString(ARG_ITEM_ID)!!
            }
        }
    }

    private fun saveProgress(fileNumber: Int, timeMs: Long, speed: Float) {
        GlobalScope.launch(Dispatchers.Main) {
            val bookId = GlobalStore.get<String?>("fragmentIsPreparedForId")
            var book: BookWithFiles? = null

            if (bookId != null) {
                book = getBook(bookId)
            }

            if (book == null) {
                Timber.i("Canceling saveProgress because book is null")
                coroutineContext.cancel()
            }

            val settings = book!!.settings

            settings.filePositionNumber = fileNumber
            settings.timeInFileMs = timeMs
            settings.speed = speed

            updateSettings(settings)
        }
     }

    private suspend fun getBook(id: String) =
        withContext(Dispatchers.IO) {
            db.bookDao().getWithFiles(id)
        }

    private suspend fun updateSettings(settings: BookSettings) {
        withContext(Dispatchers.IO) {
            Timber.i("Storing settings: ${settings}")
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
                preparePlayer(rootView, it)

                val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
                toolbar?.title = it.book.title

                bookFiles.addAll(it.sortedFiles().map { File(it.fileName!!).name })
                adapter?.notifyDataSetChanged()
                rootView.book_files_list.setOnItemSelectedListener(itemSelectedListener())
                rootView.book_files_list.setSelection(it.settings.filePositionNumber)

                val playerView: PlayerControlView? = view?.findViewById(R.id.player)
                playerView?.setShowMultiWindowTimeBar(true)
                playerView?.player = player
            }
        }

        return rootView
    }

    private fun preparePlayer(rootView: View, book: BookWithFiles) {
        if (id != GlobalStore.get<String?>("fragmentIsPreparedForId")) {
            player.playWhenReady = false
            player.prepare(ConcatenatingMediaSource(*mediaSource(book).toTypedArray()), true, true)
            player.setPlaybackParameters(PlaybackParameters(book.settings.speed))
            player.seekTo(book.settings.filePositionNumber, book.settings.timeInFileMs)

            val listener = playerEventListener(rootView)
            player.removeListener(listener)
            player.addListener(listener)

            GlobalStore.put("fragmentIsPreparedForId", id)
        }
    }

    private fun mediaSource(book: BookWithFiles): List<MediaSource> =
        book.sortedFiles().map {
            val source = ProgressiveMediaSource
                .Factory(dataSourceFactory, DefaultExtractorsFactory().setConstantBitrateSeekingEnabled(true))
                .createMediaSource(Uri.fromFile(File(it.fileName!!)))
            if (it.durationMs > 0) {
                ClippingMediaSource(source, 0, it.durationMs * 1000)
            } else {
                source
            }
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

    private fun playerEventListener(rootView: View) = object: Player.EventListener {
        private var positionDisposable: Disposable? = null

        override fun onPositionDiscontinuity(reason: Int) {
            super.onPositionDiscontinuity(reason)
            saveProgress(player.currentWindowIndex, player.currentPosition, player.playbackParameters.speed)
            rootView.book_files_list.setPosition(player.currentWindowIndex, false, false)

        }

        @SuppressLint("CheckResult")
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)

            if (isPlaying && (positionDisposable == null || positionDisposable?.isDisposed == true)) {
                positionDisposable = Observable
                    .interval(444L, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .subscribe {
                        saveProgress(player.currentWindowIndex, player.currentPosition, player.playbackParameters.speed)
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
    }

    companion object {
        const val ARG_ITEM_ID = "item_id"
    }
}
