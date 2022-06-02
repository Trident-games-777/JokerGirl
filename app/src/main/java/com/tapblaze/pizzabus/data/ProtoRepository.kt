package com.tapblaze.pizzabus.data

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ProtoRepository(
    private val jokerGirlPreferences: DataStore<JokerGirlPreferences>
) {
    val jokerGirlPreferencesFlow: Flow<JokerGirlPreferences> = jokerGirlPreferences.data

    suspend fun updateLink(link: String) {
        val saved = jokerGirlPreferencesFlow.map { it.saved }.first()
        jokerGirlPreferences.updateData { preferences ->
            when (saved) {
                0 -> preferences.toBuilder().setLink(link).setSaved(1).build()
                1 -> preferences.toBuilder().setLink(link).setSaved(2).build()
                else -> preferences
            }
        }
    }

    suspend fun setLaunchedEarlier() {
        jokerGirlPreferences.updateData { preferences ->
            preferences.toBuilder().setIsLaunchedEarlier(true).build()
        }
    }
}