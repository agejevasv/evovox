package com.github.agejevasv.evovox.io

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.agejevasv.evovox.db.AppDatabase
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class BookIndexerTest {
//    val dir = File("/storage/emulated/0/AudioBooks")
    val dir = File("/storage/AB9B-1DF4/AudioBooks")
    val ctx = InstrumentationRegistry.getInstrumentation().targetContext
    var db = Room
        .databaseBuilder(ctx, AppDatabase::class.java, "evovox-test")
        .fallbackToDestructiveMigration()
        .build()

    @Test
    fun findBooks() {
        BookIndexer(db).scanBooks(dir)

        println("Books " + db.bookDao().getAllBooks() )
    }
}
