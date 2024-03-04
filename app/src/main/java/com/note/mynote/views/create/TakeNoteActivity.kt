package com.note.mynote.views.create

import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.note.mynote.BaseActivity
import com.note.mynote.R
import com.note.mynote.data.sqlite.DBHelper.Companion.DEFAULT_FAIL_ROW
import com.note.mynote.databinding.ActivityTakeNoteBinding
import com.note.mynote.util.Constants
import com.note.mynote.util.Constants.RESULT_NOTE_CODE
import com.note.mynote.util.showSoftKeyboard

class TakeNoteActivity : BaseActivity<ActivityTakeNoteBinding>() {

    private var noteId: Int = DEFAULT_FAIL_ROW
    private val viewModel: TakeNoteVM by viewModels()

    override val bindingInflater: (LayoutInflater) -> ActivityTakeNoteBinding
        get() = ActivityTakeNoteBinding::inflate

    override fun setupView() {
        getDataAndSetTypeFromIntent()
        eventClick()
    }

    override fun observeData() {
        viewModel.savedState.observe(this) {
            if (it != DEFAULT_FAIL_ROW) {
                val intent = Intent().apply {
                    putExtra(RESULT_NOTE_CODE, it)
                }
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        viewModel.newItemInsertState.observe(this) {
            binding.edtText.setText(it.content)
            binding.edtText.requestFocus()
            binding.edtText.showSoftKeyboard()
            binding.edtText.setSelection(it.content.length)
        }
    }

    private fun getDataAndSetTypeFromIntent() {
        val type = intent.getIntExtra(Constants.KEY_TYPE, Constants.TypeNote.CREATE)
        if (type == Constants.TypeNote.CREATE) {
            binding.btnSave.text = getString(R.string.create)
        } else {
            binding.btnSave.text = getString(R.string.save)
            noteId = intent.getIntExtra(Constants.KEY_ID_NOTE, DEFAULT_FAIL_ROW)
            getNote()
        }
    }

    private fun eventClick() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnSave.setOnClickListener {
            saveNote()
        }
    }

    private fun saveNote() {
        val content = binding.edtText.text.toString()
        if (content.isEmpty())
            return
        viewModel.saveContent(noteId, content)
    }

    private fun getNote() {
        viewModel.getNoteByID(noteId)
    }
}