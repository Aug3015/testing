package com.note.mynote

import android.app.Application
import com.note.mynote.data.sqlite.DBHelper

class App : Application() {

    companion object {
        var dbHelper: DBHelper? = null
    }

    override fun onCreate() {
        super.onCreate()
        dbHelper = DBHelper(this)
    }
}