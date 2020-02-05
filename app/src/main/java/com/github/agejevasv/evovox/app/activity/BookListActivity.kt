package com.github.agejevasv.evovox.app.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.Observer
import com.github.agejevasv.evovox.EvovoxApplication
import com.github.agejevasv.evovox.R
import com.github.agejevasv.evovox.db.AppDatabase
import com.github.agejevasv.evovox.io.BookIndexer
import com.github.agejevasv.evovox.app.adapter.BookListAdapter
import com.github.agejevasv.evovox.app.viewmodel.BookListViewModel
import kotlinx.android.synthetic.main.activity_book_list.*
import kotlinx.android.synthetic.main.book_list.*
import javax.inject.Inject


class BookListActivity : AppCompatActivity() {
    @Inject lateinit var db: AppDatabase
    @Inject lateinit var model: BookListViewModel
    @Inject lateinit var adapter: BookListAdapter
    @Inject lateinit var bookIndexer: BookIndexer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)
        setSupportActionBar(toolbar)
        toolbar.title = title

        EvovoxApplication.appComponent.inject(this)

        bookIndexer.scanBooks()

        model.getBooks().observe(this, Observer {
            adapter.submitList(it.sortedBy { it.progressInPercent() }.reversed())
        })
        book_list.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.book_list_appbar_menu, menu)

        menu.children.forEach {
            it.icon.setTint(resources.getColor(R.color.color_on_surface))
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.manage_dirs -> {
                startActivity(Intent(this, DirectoryManagementActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onRestart() {
        super.onRestart()
        bookIndexer.scanBooks()
    }
}
