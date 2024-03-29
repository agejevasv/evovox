package com.github.agejevasv.evovox.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.agejevasv.evovox.R
import com.github.agejevasv.evovox.db.AppDatabase
import com.github.agejevasv.evovox.db.entity.AudiobookDir
import com.github.agejevasv.evovox.io.BookIndexer
import kotlinx.android.synthetic.main.dir_list_content.view.*
import org.jetbrains.anko.doAsync
import javax.inject.Inject

class AudiobookDirListAdapter @Inject constructor (val db: AppDatabase, val indexer: BookIndexer) :
    ListAdapter<AudiobookDir, AudiobookDirListAdapter.ItemViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder =
        ItemViewholder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.dir_list_content, parent, false)
        )

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) {
        holder.dirView.text = getItem(position).dir

        with(holder.dirDeleteButton) {
            tag = getItem(position)
            setOnClickListener { v: View ->
                doAsync {
                    val dir = v.tag as AudiobookDir
                    db.audiobookDirDao().delete(dir)
                }
            }
        }
    }

    inner class ItemViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val dirView: TextView = view.dir
        val dirDeleteButton: AppCompatImageView = view.dirDelete
    }
}

class DiffCallback : DiffUtil.ItemCallback<AudiobookDir>() {
    override fun areItemsTheSame(oldItem: AudiobookDir, newItem: AudiobookDir): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AudiobookDir, newItem: AudiobookDir): Boolean {
        return oldItem == newItem
    }
}
