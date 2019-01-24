package com.atherton.tmdb.data.preferences

import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalStorage @Inject constructor(
        private val sharedPreferences: SharedPreferences,
        private val moshi: Moshi
) : Storage {

    //todo use this class for sharedpreferences storage

//    override fun getSessionToken(): String? = sharedPreferences[PREFERENCE_SESSION_TOKEN]
//
//    override fun saveSessionToken(sessionToken: String) {
//        sharedPreferences[PREFERENCE_SESSION_TOKEN] = sessionToken
//    }
//
//    override fun getCategories(): List<CategoryEntity>? {
//        val categoriesJson: String? = sharedPreferences[PREFERENCE_CATEGORIES]
//        return if (categoriesJson != null) {
//            val type: Type = Types.newParameterizedType(List::class.java, CategoryEntity::class.java)
//            val adapter: JsonAdapter<List<CategoryEntity>> = moshi.adapter(type)
//            adapter.fromJson(categoriesJson)
//        }
//        else {
//            null
//        }
//    }
//
//    override fun saveCategories(categories: List<CategoryEntity>) {
//        val type: Type = Types.newParameterizedType(List::class.java, CategoryEntity::class.java)
//        val adapter: JsonAdapter<List<CategoryEntity>> = moshi.adapter(type)
//        sharedPreferences[PREFERENCE_CATEGORIES] = adapter.toJson(categories)
//    }
//
//    companion object {
//        private const val PREFERENCE_SESSION_TOKEN = "preference_session_token"
//        private const val PREFERENCE_CATEGORIES = "preference_categories"
//    }
}
