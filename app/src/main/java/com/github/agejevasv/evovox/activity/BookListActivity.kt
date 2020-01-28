package com.github.agejevasv.evovox.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.github.agejevasv.evovox.EvovoxApplication
import com.github.agejevasv.evovox.R
import com.github.agejevasv.evovox.data.DummyContent
import com.github.agejevasv.evovox.entity.AudiobookDir
import com.github.agejevasv.evovox.view.adapter.BookListAdapter
import com.github.agejevasv.evovox.view.model.BookDirViewModel
import kotlinx.android.synthetic.main.activity_book_list.*
import kotlinx.android.synthetic.main.book_list.*
import javax.inject.Inject


class BookListActivity : AppCompatActivity() {
    @Inject
    lateinit var model: BookDirViewModel

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.book_list_appbar_menu, menu)

        menu.children.forEach { m ->
            m.icon.setTint(resources.getColor(R.color.color_on_surface))
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)
        setSupportActionBar(toolbar)
        toolbar.title = title

        EvovoxApplication.appComponent.inject(this)

        model.getDirs().observe(this, Observer { t: List<AudiobookDir> ->
            if (t.isEmpty()) {
                startActivity(
                    Intent(applicationContext, DirectoryManagementActivity::class.java)
                )
            } else {
                setupRecyclerView(book_list)
            }
        })
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = BookListAdapter(applicationContext, DummyContent.ITEMS)
    }
}
