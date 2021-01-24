package com.koc.touchnotes.preferenceManager

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import com.koc.touchnotes.enums.NoteSort
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
Created by kelvin_clark on 1/24/2021 8:50 PM
 */

@Singleton
class PreferenceManager @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.createDataStore(PREFERENCE_NAME)

    val sortPreferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val sortOrder = NoteSort.valueOf(
                    preferences[PreferencesKeys.NOTE_SORT_ORDER] ?: NoteSort.BY_CREATED_TIME.name
                )
            sortOrder
        }

    suspend fun updateSortOrder(sortOrder: NoteSort) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTE_SORT_ORDER] = sortOrder.name
        }
    }

    private object PreferencesKeys {
        val NOTE_SORT_ORDER = stringPreferencesKey(SORT_ORDER)
    }

    companion object {
        private const val PREFERENCE_NAME = "user_preference"
        private const val SORT_ORDER = "sort_order"
    }
}