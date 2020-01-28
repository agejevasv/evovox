package com.github.agejevasv.evovox.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.agejevasv.evovox.R
import com.github.agejevasv.evovox.db.Db
import com.github.agejevasv.evovox.entity.AudiobookDir
import kotlinx.android.synthetic.main.dir_list_content.view.*
import org.jetbrains.anko.doAsync

class AudiobookDirListAdapter :
    ListAdapter<AudiobookDir, AudiobookDirListAdapter.ItemViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder =
        ItemViewholder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.dir_list_content, parent, false)
        )

    override fun getItemId(position: Int): Long {
        return getItem(position).id.toLong()
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) {
        holder.dirView.text = getItem(position).dir

        with(holder.dirDeleteButton) {
            tag = getItem(position)
            setOnClickListener { v: View ->
                doAsync {
                    val dir = v.tag as AudiobookDir
                    Db.getInstance(v.context).db().audiobookDirDao().delete(dir)
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
