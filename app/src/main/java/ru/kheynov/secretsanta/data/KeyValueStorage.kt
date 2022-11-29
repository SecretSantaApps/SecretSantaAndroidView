package ru.kheynov.secretsanta.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val PREF_NAME = "KEY_VALUE_STORAGE"

enum class KEYS {
    IS_AUTHORIZED,
}

@Singleton
class KeyValueStorage @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var isAuthorized: Boolean = false
        set(value) {
            field = value
            saveToPreferences(value, KEYS.IS_AUTHORIZED)
        }
        get() = prefs.getBoolean(KEYS.IS_AUTHORIZED.name, false)

    private fun <T> saveToPreferences(value: T?, key: KEYS) {
        val editor: SharedPreferences.Editor = prefs.edit()
        when (value) {
            is Boolean -> editor.putBoolean(key.name, value)
            is String -> editor.putString(key.name, value)
        }
        editor.apply()
    }
}