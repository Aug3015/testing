package com.note.mynote.util

import androidx.annotation.IntDef

object Constants {

    const val RESULT_NOTE_CODE = "RESULT_NOTE_CODE"
    const val KEY_TYPE = "KEY_TYPE"
    const val KEY_ID_NOTE = "KEY_ID_NOTE"

    @IntDef(TypeNote.CREATE, TypeNote.EDIT)
    annotation class TypeNote {
        companion object {
            const val CREATE = 1
            const val EDIT = 2
        }
    }

    @IntDef(
        SearchState.IDE,
        SearchState.DONE,
        SearchState.EMPTY_KEYWORD
    )
    annotation class SearchState {
        companion object {
            const val IDE = -1
            const val DONE = 1
            const val EMPTY_KEYWORD = 2
        }
    }
}