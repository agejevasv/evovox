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
import com.github.agejevasv.evovox.db.entity.BookWithFiles
import kotlinx.android.synthetic.main.book_list_content.view.*
import javax.inject.Inject

class BookListAdapter @Inject constructor (val db: AppDatabase, val ctx: Context) :
ListAdapter<BookWithFiles, BookListAdapter.ViewHolder>(DiffCallback()) {
    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as BookWithFiles
            val intent = Intent(v.context, BookDetailActivity::class.java).apply {
                putExtra(BookDetailFragment.ARG_KEY_BOOK_ID, item.book.id.toString())
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
        holder.bookAuthorView.text = item.book.author
        holder.bookTitleView.text = item.book.title

        if (item.progressInSeconds() > 0) {
            holder.durationView.text =
                ctx.getString(
                    R.string.book_length_with_progress,
                    DateUtils.formatElapsedTime(item.progressInSeconds()),
                    DateUtils.formatElapsedTime(item.durationInSeconds())
                )
        } else {
            holder.durationView.text =
                ctx.getString(
                    R.string.book_length,
                    DateUtils.formatElapsedTime(item.durationInSeconds()))
        }

        holder.progressView.text = ctx.getString(R.string.book_progress, item.progressInPercent())

        if (item.progressInPercent() == 0)
            holder.progressView.setTextColor(ctx.resources.getColor(R.color.color_on_background))
        else
            holder.progressView.setTextColor(ctx.resources.getColor(R.color.color_on_surface))

        holder.determinateBarView.progress = item.progressInPercent()


        holder.bookTitleView.text = item.book.title

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
        val determinateBarView: ProgressBar = view.determinateBar
    }

    class DiffCallback : DiffUtil.ItemCallback<BookWithFiles>() {
        override fun areItemsTheSame(oldItem: BookWithFiles, newItem: BookWithFiles): Boolean {
            return oldItem.book.id == newItem.book.id
        }

        override fun areContentsTheSame(oldItem: BookWithFiles, newItem: BookWithFiles): Boolean {
            return oldItem == newItem
        }
    }

}
