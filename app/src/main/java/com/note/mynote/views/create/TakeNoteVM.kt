package com.note.mynote.views.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.note.mynote.App
import com.note.mynote.data.model.Note
import com.note.mynote.data.sqlite.DBHelper.Companion.DEFAULT_FAIL_ROW
import com.note.mynote.util.Constants
import com.note.mynote.util.Constants.SearchState.Companion.DONE
import com.note.mynote.util.Constants.SearchState.Companion.EMPTY_KEYWORD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TakeNoteVM : ViewModel() {

    var savedState: MutableLiveData<Int> = MutableLiveData()
        private set

    var newItemInsertState: MutableLiveData<Note> = MutableLiveData()
        private set

    var notes: MutableLiveData<MutableList<Note>> = MutableLiveData()
        private set

    private var _searchStateUI: MutableStateFlow<SearchStateUI> =
        MutableStateFlow(SearchStateUI(Constants.SearchState.IDE, ArrayList()))
    var searchStateUI = _searchStateUI.asStateFlow()

    fun saveContent(noteId: Int, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = App.dbHelper?.update(noteId, content)
            savedState.postValue(result)
        }
    }

    fun getAllNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = App.dbHelper?.getAllNotes()
            notes.postValue(data)
        }
    }

    fun getNoteByID(resultID: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            resultID?.let { id ->
                if (id != DEFAULT_FAIL_ROW) {
                    App.dbHelper?.getNoteByID(id)?.let {
                        newItemInsertState.postValue(it)
                    }
                }
            }
        }
    }

    fun search(keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            delay(100)
            if (keyword.isEmpty()) {
                _searchStateUI.update {
                    it.copy(state = EMPTY_KEYWORD)
                }
                return@launch
            }
            val list = App.dbHelper?.search(keyword)
            list?.let { data ->
                _searchStateUI.update {
                    it.copy(
                        state = DONE,
                        notes = data
                    )
                }
            }
        }
    }
}

data class SearchStateUI(val state: Int, val notes: MutableList<Note>)