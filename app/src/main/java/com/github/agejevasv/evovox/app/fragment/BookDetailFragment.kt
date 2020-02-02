package com.github.agejevasv.evovox.app.fragment


import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.github.agejevasv.evovox.R
import com.github.agejevasv.evovox.db.AppDatabase
import com.github.agejevasv.evovox.db.entity.BookWithFiles
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.book_detail.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import javax.inject.Inject


class BookDetailFragment @Inject constructor (
    val db: AppDatabase,
    val player: SimpleExoPlayer,
    val dataSourceFactory: DefaultDataSourceFactory
) : Fragment() {

    private var id: String? = null
    var adapter: ArrayAdapter<String>? = null
    var bookFiles = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                id = it.getString(ARG_ITEM_ID)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.book_detail, container, false)
        adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, bookFiles)
        rootView.book_files_list.adapter = adapter

        var item: BookWithFiles? = null


        doAsync {
            if (id != null) {
                item = db.bookDao().getWithFiles(id!!)
            }
            uiThread {
                item?.let {
                    bookFiles.addAll(it.files.map { File(it.fileName!!).name })
                    adapter?.notifyDataSetChanged()

                    val list: Array<MediaSource> = it.files.map {
                        ProgressiveMediaSource
                            .Factory(dataSourceFactory)
                            .createMediaSource(Uri.fromFile(File(it.fileName!!))
                        )
                    }.toTypedArray()

                    val playerView: PlayerView? = view?.findViewById(R.id.player)
                    playerView?.player = player
                    player.prepare(ConcatenatingMediaSource(*list))
                    player.playWhenReady = true
                }
            }
        }

        return rootView
    }

    override fun onDetach() {
        super.onDetach()
        player.stop()
    }

    companion object {
        const val ARG_ITEM_ID = "item_id"
    }
}
