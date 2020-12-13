package com.thelumierguy.astroadventures.data

import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

object DataStoreHelper {

    private val HAS_COMPLETED_LEVEL_ZERO = preferencesKey<Boolean>("HAS_COMPLETED_LEVEL_ZERO")

    private val HIGH_SCORE = preferencesKey<Long>("HIGH_SCORE")

    private val MAX_LEVELS = preferencesKey<Int>("MAX_LEVELS")

    var dataStore: DataStore<Preferences>? = null

    fun initDataStore(activity: AppCompatActivity) {
        dataStore = activity.createDataStore(
            name = "app_settings"
        )

        activity.lifecycleScope.launchWhenCreated {
            getHasCompletedTutorial()?.collect {
                LevelInfo.hasPlayedTutorial = it
            }
        }
    }

    suspend fun setHasCompletedTutorial() {
        dataStore?.edit {
            it[HAS_COMPLETED_LEVEL_ZERO] = true
        }
    }

    private fun getHasCompletedTutorial(): Flow<Boolean> {
        return dataStore?.data?.map { preference ->
            preference[HAS_COMPLETED_LEVEL_ZERO] ?: false
        } ?: flowOf(false)
    }


    suspend fun setHighScore(score: Long) {
        dataStore?.edit {
            it[HIGH_SCORE] = score
            it[MAX_LEVELS] = LevelInfo.level
        }
    }

    internal fun getHighScore(): Flow<Long> = dataStore?.data?.map { preference ->
        preference[HIGH_SCORE] ?: 0
    } ?: flowOf(0)

    internal fun getMaxLevels(): Flow<Int> = dataStore?.data?.map { preference ->
        preference[MAX_LEVELS] ?: 0
    } ?: flowOf(0)

}
