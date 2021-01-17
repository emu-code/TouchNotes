package com.koc.touchnotes.model

import androidx.room.Database
import androidx.room.RoomDatabase

/**
Created by kelvin_clark on 12/6/2020
 */
@Database(entities = [Note::class], version = 1, exportSchema = true)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun getNotesDao() : NotesDao
}