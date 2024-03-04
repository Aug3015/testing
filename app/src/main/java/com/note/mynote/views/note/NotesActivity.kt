package com.note.mynote.views.note

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.note.mynote.BaseActivity
import com.note.mynote.data.model.Note
import com.note.mynote.data.sqlite.DBHelper.Companion.DEFAULT_FAIL_ROW
import com.note.mynote.databinding.ActivityNotesBinding
import com.note.mynote.util.Constants
import com.note.mynote.util.Constants.KEY_ID_NOTE
import com.note.mynote.util.Constants.KEY_TYPE
import com.note.mynote.util.Constants.RESULT_NOTE_CODE
import com.note.mynote.views.create.TakeNoteActivity
import com.note.mynote.views.create.TakeNoteVM
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class NotesActivity : BaseActivity<ActivityNotesBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityNotesBinding
        get() = ActivityNotesBinding::inflate

    private var takeNoteLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                fetchNewestData(result.data)
            }
        }

    private val onTextChangeListener by lazy {
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
    }
    private val noteAdapter by lazy {
        NotesAdapter(this) {
            getNoteAndNavigateToTakeNote(Constants.TypeNote.EDIT, it)
        }
    }

    private val viewModel: TakeNoteVM by viewModels()

    override fun setupView() {
        configNotesAdapter()
        eventView()
    }

    override fun observeData() {
        fetchData()
        viewModel.notes.observe(this) {
            noteAdapter.addData(it)
        }

        viewModel.newItemInsertState.observe(this) {
            noteAdapter.addNewData(it)
        }

        lifecycleScope.launch {
            viewModel.searchStateUI.collect {
                when (it.state) {
                    Constants.SearchState.EMPTY_KEYWORD -> {
                        fetchData()
                    }

                    Constants.SearchState.DONE -> {
                        noteAdapter.addData(it.notes)
                    }
                }
            }
        }
    }

    private fun fetchData() {
        viewModel.getAllNotes()
    }

    private fun fetchNewestData(data: Intent?) {
        val resultID: Int? = data?.getIntExtra(RESULT_NOTE_CODE, DEFAULT_FAIL_ROW)
        viewModel.getNoteByID(resultID)
    }

    private fun getNoteAndNavigateToTakeNote(
        typeNote: Int = Constants.TypeNote.CREATE,
        note: Note? = null
    ) {
        takeNoteLauncher.launch(Intent(this, TakeNoteActivity::class.java).apply {
            putExtra(KEY_TYPE, typeNote)
            putExtra(KEY_ID_NOTE, note?.id)
        })
    }

    private fun configNotesAdapter() {
        with(binding.rvNotes) {
            layoutManager = LinearLayoutManager(
                this@NotesActivity,
                RecyclerView.VERTICAL,
                false
            )
            adapter = noteAdapter
        }
    }

    private fun eventView() {
        binding.tvCreate.setOnClickListener {
            getNoteAndNavigateToTakeNote()
        }

        binding.edtSearch.addTextChangedListener(onTextChangeListener)
    }
}