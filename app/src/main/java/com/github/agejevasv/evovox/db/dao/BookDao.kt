package com.github.agejevasv.evovox.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.github.agejevasv.evovox.db.entity.Book
import com.github.agejevasv.evovox.db.entity.BookFile
import com.github.agejevasv.evovox.db.entity.BookSettings
import com.github.agejevasv.evovox.db.entity.BookWithFiles

@Dao
interface BookDao {
    /*
     * Book
     */
    @Query("SELECT * FROM book WHERE id = :id")
    fun get(id: String): Book?

    @Transaction
    @Query("SELECT * FROM book WHERE id = :id")
    fun getWithFiles(id: String): BookWithFiles?

    @Query("SELECT * FROM book")
    fun getAllBooks(): List<Book>

    @Query("SELECT * FROM book where visible = :visible ORDER BY createdAt DESC")
    fun getAllBooksLive(visible: Boolean = true): LiveData<List<BookWithFiles>>

    @Query("SELECT * FROM book WHERE id IN (:ids)")
    fun loadAllBooksByIds(ids: LongArray): List<Book>

    @Query("SELECT * FROM book WHERE dir LIKE :dir LIMIT 1")
    fun findBookByDir(dir: String): Book?

    @Insert
    fun insertBook(book: Book): Long

    @Delete
    fun deleteBook(book: Book)

    @Query("DELETE FROM book WHERE parentDir = :dir")
    fun deleteBooksByParentDir(dir: String)

    @Query("DELETE FROM book WHERE id IN (:ids)")
    fun deleteBooksByIds(ids: List<Long>)

    @Update
    fun updateBooks(vararg bookFile: Book)



    /*
     * BookFile
     */

    @Query("SELECT * FROM book_file ORDER BY book_file.id ASC")
    fun getAllBookFiles(): List<BookFile>

    @Query("SELECT * FROM book_file where bookId = :bookId ORDER BY book_file.id ASC")
    fun getBookFiles(bookId: Long): List<BookFile>

    @Query("SELECT * FROM book_file WHERE id IN (:ids)")
    fun loadAllBookFilesByIds(ids: LongArray): List<BookFile>

    @Query("SELECT * FROM book_file WHERE fileName LIKE :fileName LIMIT 1")
    fun findBookFilesByFileName(fileName: String): BookFile?

    @Insert
    fun insertBookFile(bookFile: BookFile): Long

    @Delete
    fun deleteBookFile(bookFile: BookFile)

    @Query("DELETE FROM book_file WHERE id IN (:ids)")
    fun deleteBookFilesByIds(ids: List<Long>)

    @Update
    fun updateBookFiles(bookFiles: List<BookFile>)


    /*
     * BookSettings
     */
    @Query("SELECT * FROM book_settings WHERE bookId = :bookId")
    fun getBookSettings(bookId: Long): BookSettings?

    @Insert
    fun insertBookSettings(bookSettings: BookSettings)

    @Update
    fun updateBookSettings(bookSettings: BookSettings)
}