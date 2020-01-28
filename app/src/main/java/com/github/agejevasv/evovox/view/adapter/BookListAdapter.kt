package com.github.agejevasv.evovox.view.adapter

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.agejevasv.evovox.R
import com.github.agejevasv.evovox.activity.BookDetailActivity
import com.github.agejevasv.evovox.data.DummyContent
import com.github.agejevasv.evovox.fragment.BookDetailFragment
import kotlinx.android.synthetic.main.book_list_content.view.*

class BookListAdapter(
    private val applicationContext: Context,
    private val values: List<DummyContent.DummyItem>
): RecyclerView.Adapter<BookListAdapter.ViewHolder>() {
    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as DummyContent.DummyItem
            val intent = Intent(v.context, BookDetailActivity::class.java).apply {
                putExtra(BookDetailFragment.ARG_ITEM_ID, item.id)
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
        val item = values[position]
        holder.bookAuthorView.text = item.author
        holder.bookTitleView.text = item.title

        if (item.progress == 0) {
            holder.durationView.text =
                applicationContext.getString(
                    R.string.book_length,
                    DateUtils.formatElapsedTime(item.duration.toLong()))
        } else {
            holder.durationView.text =
                applicationContext.getString(
                    R.string.book_length_with_progress,
                    DateUtils.formatElapsedTime(item.duration.toLong()),
                    DateUtils.formatElapsedTime(item.duration.toLong()))
        }

        holder.progressView.text = applicationContext.getString(R.string.book_progress, item.progress)
        holder.determinateBarView.progress = item.progress

        holder.imageView.setImageDrawable(null)
        holder.initialsView.visibility = View.VISIBLE
        holder.initialsView.text = item.author.split(" ").map { s -> s.take(1) }.joinToString("").toUpperCase().take(3)
        holder.bookTitleView.text = item.title

        with(holder.itemView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val bookAuthorView: TextView = view.bookAuthor
        val bookTitleView: TextView = view.bookTitle
        val durationView: TextView = view.duration
        val progressView: TextView = view.progress
        val initialsView: TextView = view.initials
        val determinateBarView: ProgressBar = view.determinateBar
        val imageView: ImageView = view.imageView
    }
}