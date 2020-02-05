package com.github.agejevasv.evovox.app.activity

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.github.agejevasv.evovox.EvovoxApplication
import com.github.agejevasv.evovox.R
import com.github.agejevasv.evovox.app.fragment.BookDetailFragment
import com.github.agejevasv.evovox.db.AppDatabase
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.activity_book_detail.*
import java.math.BigDecimal
import javax.inject.Inject


class BookDetailActivity : AppCompatActivity() {

    @Inject lateinit var db: AppDatabase

    @Inject lateinit var fragment: BookDetailFragment

    @Inject lateinit var player: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)
        setSupportActionBar(toolbar)

        EvovoxApplication.appComponent.inject(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            fragment.apply {
                arguments = Bundle().apply {
                    putString(
                        BookDetailFragment.ARG_KEY_BOOK_ID,
                        intent.getStringExtra(BookDetailFragment.ARG_KEY_BOOK_ID)

                    )
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.book_detail_container, fragment)
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.top_app_bar_menu, menu)

        menu.children.forEach { m ->
            m.icon.setTint(resources.getColor(R.color.color_on_surface))
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                navigateUpTo(Intent(this, BookListActivity::class.java))
                true
            }
            R.id.menu_speed_settings -> {
                val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.dialog))
                builder.setView(layoutInflater.inflate(R.layout.speed_dialog, null))
                builder.setPositiveButton("OK") { _, _ -> }
                val dialog = builder.create()
                dialog.show()

                val layoutParams = WindowManager.LayoutParams()
                layoutParams.copyFrom(dialog.window?.attributes)
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                dialog.window?.attributes = layoutParams

                val seekBar = dialog.findViewById<SeekBar>(R.id.speedSeekBar)
                val speedText = dialog.findViewById<TextView>(R.id.speedTextView)

                val storedSpeed = player.playbackParameters.speed

                seekBar?.progress = (storedSpeed * 10).toInt() - 5
                speedText?.text = String.format("%.1f x", storedSpeed)

                seekBar?.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        val speed = BigDecimal((progress + 5) / 10.0).setScale(2, BigDecimal.ROUND_HALF_EVEN)
                        speedText?.text = String.format("%.1fx", speed)
                        player.setPlaybackParameters(PlaybackParameters(speed.toFloat()))
                    }
                })

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
