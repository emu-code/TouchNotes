package com.koc.touchnotes.view.extensions

import android.content.Intent
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.koc.touchnotes.R
import com.koc.touchnotes.view.NoteEditFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
Created by kelvin_clark on 1/29/2021 3:04 AM
 */

fun NoteEditFragment.saveNoteState() {
    binding.apply {
        noteTitle.addTextChangedListener {
            noteEditViewModel.title = it.toString()
        }

        noteBody.addTextChangedListener {
            noteEditViewModel.body = it.toString()
        }
    }
}

fun NoteEditFragment.populateViews() = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
    binding.apply {
        noteTitle.setText(noteEditViewModel.title)
        noteBody.setText(noteEditViewModel.body)
        noteId = noteEditViewModel.note?.id
        createdTime = noteEditViewModel.note?._createdTime
        modifiedTime = noteEditViewModel.note?._modifiedTime
    }
}

fun NoteEditFragment.saveNote() {
    val time = System.currentTimeMillis()
    lifecycleScope.launch(Dispatchers.IO) {
        if (noteId != null) {
            if (isModified) {
                noteEditViewModel.updateNote(
                    noteId!!, binding.noteTitle.text.toString(),
                    noteBody = binding.noteBody.text.toString(),
                    createdTime = createdTime!!, time
                )
            }
        } else {
            noteEditViewModel.saveNote(
                binding.noteTitle.text.toString(),
                binding.noteBody.text.toString(), time, time
            )
        }
    }
}

fun NoteEditFragment.shareNote() {
    val title = binding.noteTitle.text.toString()
    val body = binding.noteBody.text.toString()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, "Share note")
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, body)
    }

    startActivity(Intent.createChooser(sendIntent, resources.getString(R.string.share)))
    Toast.makeText(context, "Sharing", Toast.LENGTH_SHORT).show()
}

fun NoteEditFragment.showDeleteDialogue() {
    val deleteAlert = MaterialAlertDialogBuilder(requireContext())

    deleteAlert.apply {
        setTitle(getString(R.string.delete_note))
        setMessage(getString(R.string.delete_confirmation))
        setIcon(R.drawable.ic_delete_dialogue)
        setPositiveButton(getString(R.string.yes)){ dialogue, _ ->
            noteEditViewModel.deleteNote(noteId)
            dialogue.dismiss()
            Toast.makeText(requireContext(), getString(R.string.note_delete_success), Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_edit_list)
        }
        setNegativeButton(getString(R.string.no)){ dialogue, _ ->
            dialogue.dismiss()
        }
        setCancelable(false)
    }

    deleteAlert.create().show()
}