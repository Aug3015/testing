package com.note.mynote.data.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.android.material.tabs.TabLayout.Tab
import com.note.mynote.data.model.Note


class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "notes.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "NOTE"
        private const val NOTE_ID = "NOTE_ID"
        const val CONTENT = "CONTENT"
        const val DEFAULT_FAIL_ROW = -1

        private const val SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CONTENT + " TEXT NOT NULL)"

        private const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_TABLE)
        onCreate(db)
    }

    fun getAllNotes(): MutableList<Note> {
        val columns = arrayOf(NOTE_ID, CONTENT)
        val noteList: MutableList<Note> = ArrayList()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            columns,
            null,
            null,
            null,
            null,
            "NOTE_ID DESC"
        )
        if (cursor.moveToFirst()) {
            do {
                val contentIndex = cursor.getColumnIndex(CONTENT)
                val idIndex = cursor.getColumnIndex(NOTE_ID)
                if (contentIndex != DEFAULT_FAIL_ROW && idIndex != DEFAULT_FAIL_ROW) {
                    val item = Note(
                        id = cursor.getInt(idIndex),
                        content = cursor.getString(contentIndex)
                    )
                    noteList.add(item)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return noteList
    }

    fun update(id: Int, content: String): Int {
        var idRow = id
        try {
            val hasRow = hasItemByID(idRow)
            val db = writableDatabase
            val values = ContentValues()
            values.put(CONTENT, content)
            if (hasRow) {
                db.update(TABLE_NAME, values, "$NOTE_ID=$idRow", null)
            } else {
                idRow = db.insertWithOnConflict(
                    TABLE_NAME,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
                ).toInt()
            }
            db.close()
            return idRow
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return idRow
    }

    private fun hasItemByID(id: Int): Boolean {
        if (id == DEFAULT_FAIL_ROW)
            return false
        val db = writableDatabase
        val selectString = "SELECT * FROM $TABLE_NAME WHERE $NOTE_ID=$id"
        val cursor = db.rawQuery(selectString, null)
        var hasObject = false
        if (cursor.moveToFirst()) {
            hasObject = true
            var count = 0
            while (cursor.moveToNext()) {
                count++
            }
        }
        cursor.close()
        db.close()
        return hasObject
    }

    fun getNoteByID(id: Int): Note? {
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT  * FROM $TABLE_NAME WHERE NOTE_ID=$id", null)

        if (cursor.moveToLast()) {
            val index = cursor.getColumnIndex(CONTENT)
            if (index != DEFAULT_FAIL_ROW) {
                val content = cursor.getString(index)
                return Note(id, content)
            }
        }
        cursor.close()
        db.close()
        return null
    }

    fun search(keyword: String): MutableList<Note> {
        val notes: MutableList<Note> = ArrayList()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_NAME, null, "$CONTENT LIKE '%$keyword%'", null, null, null, null)

        cursor.use { c ->
            while (c.moveToNext()) {
                val idIndex = cursor.getColumnIndex(NOTE_ID)
                val contentIndex = cursor.getColumnIndex(CONTENT)
                val id = c.getLong(idIndex)
                val content = c.getString(contentIndex)
                if (idIndex != DEFAULT_FAIL_ROW && contentIndex != DEFAULT_FAIL_ROW) {
                    val note = Note(id.toInt(), content)
                    notes.add(note)
                }

            }
            c.close()
        }
        db.close()
        return notes
    }
}