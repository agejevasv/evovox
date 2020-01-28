package com.github.agejevasv.evovox.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.github.agejevasv.evovox.EvovoxApplication
import com.github.agejevasv.evovox.R
import com.github.agejevasv.evovox.db.AppDatabase
import com.github.agejevasv.evovox.entity.AudiobookDir
import com.github.agejevasv.evovox.view.adapter.AudiobookDirListAdapter
import com.github.agejevasv.evovox.view.model.BookDirViewModel
import com.obsez.android.lib.filechooser.ChooserDialog
import com.obsez.android.lib.filechooser.tool.DirAdapter
import kotlinx.android.synthetic.main.activity_book_detail.toolbar
import kotlinx.android.synthetic.main.activity_manage_dirs.*
import org.jetbrains.anko.doAsync
import javax.inject.Inject


class DirectoryManagementActivity : AppCompatActivity() {
    @Inject
    lateinit var model: BookDirViewModel

    @Inject
    lateinit var db: AppDatabase

    @Inject
    lateinit var adapter: AudiobookDirListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_dirs)
        setSupportActionBar(toolbar)

        EvovoxApplication.appComponent.inject(this)

        val cd = chooserDialog().build()
        val recyclerView = findViewById<RecyclerView>(R.id.dirList)

        model.getDirs().observe(this, Observer { t: List<AudiobookDir> -> adapter.submitList(t) })
        recyclerView.adapter = adapter

        addBookDir.setOnClickListener { v: View? ->
            progressBar.visibility = View.VISIBLE
            cd.show()
        }
    }

    private fun chooserDialog(): ChooserDialog {
        return ChooserDialog(this)
            .withFilter(true, false)
            .withNegativeButtonListener { dialog, which ->
                progressBar.visibility = View.GONE
            }
            .withChosenListener { path, pathFile ->
                progressBar.visibility = View.GONE
                doAsync {
                    db.audiobookDirDao().insertAll(AudiobookDir(dir = path))
                }
            }
            .withAdapterSetter { adapter: DirAdapter? ->
                adapter?.overrideGetView { file, isSelected, isFocused, convertView, parent, inflater ->
                    val view = inflater.inflate(com.obsez.android.lib.filechooser.R.layout.li_row_textview, parent, false) as ViewGroup
                    val name = view.findViewById(com.obsez.android.lib.filechooser.R.id.text) as TextView
                    name.text = file.name

                    val size = view.findViewById(com.obsez.android.lib.filechooser.R.id.txt_size) as TextView
                    size.visibility = View.GONE
                    val date = view.findViewById(com.obsez.android.lib.filechooser.R.id.txt_date) as TextView
                    date.visibility = View.GONE

                    val iv = view.findViewById(com.obsez.android.lib.filechooser.R.id.icon) as ImageView
                    iv.setImageResource(R.drawable.ic_folder_black_24dp)
                    iv.layoutParams.height = 60
                    iv.layoutParams.width = 60
                    iv.visibility = View.VISIBLE

                    view
                }
            }
    }
}
