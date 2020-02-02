package com.github.agejevasv.evovox.app.adapter

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.agejevasv.evovox.R
import com.github.agejevasv.evovox.app.activity.BookDetailActivity
import com.github.agejevasv.evovox.app.fragment.BookDetailFragment
import com.github.agejevasv.evovox.db.AppDatabase
import com.github.agejevasv.evovox.db.entity.Book
import kotlinx.android.synthetic.main.book_list_content.view.*
import javax.inject.Inject

class BookListAdapter @Inject constructor (val db: AppDatabase, val ctx: Context) :
ListAdapter<Book, BookListAdapter.ViewHolder>(DiffCallback()) {
    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as Book
            val intent = Intent(v.context, BookDetailActivity::class.java).apply {
                putExtra(BookDetailFragment.ARG_ITEM_ID, item.id.toString())
            }
            v.context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bookAuthorView.text = item.author
        holder.bookTitleView.text = item.title

        if (item.progress == 0L) {
            holder.durationView.text =
                ctx.getString(
                    R.string.book_length,
                    DateUtils.formatElapsedTime(item.duration))
        } else {
            holder.durationView.text =
                ctx.getString(
                    R.string.book_length_with_progress,
                    DateUtils.formatElapsedTime(item.duration - item.progress),
                    DateUtils.formatElapsedTime(item.duration))
        }

        holder.progressView.text = ctx.getString(R.string.book_progress, item.progress)
        holder.determinateBarView.progress = item.progress.toInt()

        holder.imageView.setImageDrawable(null)
        holder.initialsView.visibility = View.VISIBLE
        holder.initialsView.text = item.author.split(" ").map { it.take(1) }.joinToString("").toUpperCase().take(3)
        holder.bookTitleView.text = item.title

        with(holder.itemView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val bookAuthorView: TextView = view.bookAuthor
        val bookTitleView: TextView = view.bookTitle
        val durationView: TextView = view.duration
        val progressView: TextView = view.progress
        val initialsView: TextView = view.initials
        val determinateBarView: ProgressBar = view.determinateBar
        val imageView: ImageView = view.imageView
    }

    class DiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }

}
