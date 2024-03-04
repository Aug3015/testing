package com.note.mynote.views.note

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.note.mynote.data.model.Note
import com.note.mynote.databinding.LayoutItemNoteBinding

class NotesAdapter(
    private val context: Context,
    private val onClickItem: (Note) -> Unit
) : Adapter<NotesAdapter.NoteHolder>() {

    private var notes: MutableList<Note> = ArrayList()

    inner class NoteHolder(private var binding: LayoutItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val item = notes[layoutPosition]
                onClickItem.invoke(item)
            }
        }

        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            val item = notes[position]
            binding.tvContent.text = "Id: ${item.id} +---+ ${item.content}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        return NoteHolder(
            LayoutItemNoteBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.onBind(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(newData: MutableList<Note>) {
        notes.clear()
        notes.addAll(newData)
        notifyDataSetChanged()
    }

    fun addNewData(newData: Note) {
        val index = hasIndexData(newData)
        if (index != -1) {
            notes.removeAt(index)
            notifyItemRemoved(index)
        }
        notes.add(0, newData)
        notifyItemInserted(0)
    }

    private fun hasIndexData(newData: Note): Int {
        return notes.indexOfFirst { newData.id == it.id }
    }
}