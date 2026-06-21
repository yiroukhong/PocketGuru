package com.example.pocketguru.supabase

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import com.example.pocketguru.models.KeywordItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

object SupabaseManager {
    private const val SUPABASE_URL = "https://ctzcbnnmkhqltbikjorz.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN0emNibm5ta2hxbHRiaWtqb3J6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODE4Mzc4NjMsImV4cCI6MjA5NzQxMzg2M30.0_5CmF3sMWotjLzKSezEecsmQRDzt6oXxYtXu2MjMt4"

    val client: SupabaseClient by lazy {
        createSupabaseClient(SUPABASE_URL, SUPABASE_ANON_KEY) {
            install(Auth)
            install(Postgrest)
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    interface SupabaseCallback<T> {
        fun onSuccess(result: T)
        fun onError(error: String)
    }

    @Serializable
    data class UserProfile(val id: String, val username: String)

    @Serializable
    data class LevelProgress(val user_id: String, val current_level: Int)

    @Serializable
    data class LevelProgressResponse(val current_level: Int)

    @Serializable
    data class Keyword(val user_id: String, val word: String, val definition: String, val id: String? = null, val created_at: String? = null)

    fun checkUsernameUnique(username: String, callback: SupabaseCallback<Boolean>) {
        scope.launch {
            try {
                val result = client.postgrest.from("users")
                    .select(Columns.list("username")) {
                        filter {
                            eq("username", username)
                        }
                    }
                val isUnique = result.data == "[]"
                withContext(Dispatchers.Main) { callback.onSuccess(isUnique) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Unknown error") }
            }
        }
    }

    fun signUp(username: String, password: String, callback: SupabaseCallback<String>) {
        scope.launch {
            try {
                val fakeEmail = "$username@pocketguru.app"
                client.auth.signUpWith(Email) {
                    email = fakeEmail
                    this.password = password
                }
                
                val user = client.auth.currentUserOrNull()
                if (user != null) {
                    // Insert into users table
                    client.postgrest.from("users").insert(UserProfile(user.id, username))
                    
                    // Insert into level_progress table
                    client.postgrest.from("level_progress").insert(LevelProgress(user.id, 1))
                    
                    val session = client.auth.currentSessionOrNull()?.accessToken
                    withContext(Dispatchers.Main) { 
                        if (session != null) {
                            callback.onSuccess("$session|${user.id}")
                        } else {
                            callback.onSuccess("Success|${user.id}")
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) { callback.onError("Failed to get user after signup") }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Signup error") }
            }
        }
    }

    fun signIn(username: String, password: String, callback: SupabaseCallback<String>) {
        scope.launch {
            try {
                val fakeEmail = "$username@pocketguru.app"
                client.auth.signInWith(Email) {
                    email = fakeEmail
                    this.password = password
                }

                val user = client.auth.currentUserOrNull()
                val session = client.auth.currentSessionOrNull()?.accessToken

                withContext(Dispatchers.Main) {
                    if (session != null && user != null) {
                        // Return both token and userId separated by a delimiter
                        callback.onSuccess("$session|${user.id}")
                    } else {
                        callback.onError("Session not found")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError("Incorrect username or password") }
            }
        }
    }

    fun restoreSession(token: String, callback: SupabaseCallback<Boolean>) {
        scope.launch {
            try {
                // Restore session logic - supabase-kt handles session persistence usually,
                // but we can verify if the current session is valid.
                val session = client.auth.currentSessionOrNull()
                withContext(Dispatchers.Main) { callback.onSuccess(session != null) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Restore error") }
            }
        }
    }

    fun signOut(callback: SupabaseCallback<Unit>) {
        scope.launch {
            try {
                client.auth.signOut()
                withContext(Dispatchers.Main) { callback.onSuccess(Unit) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Signout error") }
            }
        }
    }

    fun getCurrentUserId(callback: SupabaseCallback<String>) {
        scope.launch {
            val uid = client.auth.currentUserOrNull()?.id
            withContext(Dispatchers.Main) {
                if (uid != null) callback.onSuccess(uid)
                else callback.onError("No user logged in")
            }
        }
    }

    fun saveKeyword(userId: String, word: String, definition: String, callback: SupabaseCallback<Boolean>) {
        scope.launch {
            try {
                // Get userId directly from active session, ignore passed-in value
                val currentUserId = client.auth.currentUserOrNull()?.id
                    ?: run {
                        withContext(Dispatchers.Main) { callback.onError("User not logged in") }
                        return@launch
                    }

                val existing = client.postgrest.from("keywords")
                    .select {
                        filter {
                            eq("user_id", currentUserId)
                            eq("word", word)
                        }
                    }

                if (existing.data != "[]") {
                    withContext(Dispatchers.Main) { callback.onSuccess(false) }
                    return@launch
                }

                client.postgrest.from("keywords").insert(Keyword(currentUserId, word, definition))
                withContext(Dispatchers.Main) { callback.onSuccess(true) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Failed to save keyword") }
            }
        }
    }

    fun getKeywords(userId: String, callback: SupabaseCallback<List<KeywordItem>>) {
        scope.launch {
            try {
                val result = client.postgrest.from("keywords")
                    .select {
                        filter { eq("user_id", userId) }
                        order("created_at", Order.DESCENDING)
                    }
                val keywords = result.decodeList<Keyword>()
                val items = keywords.map {
                    KeywordItem(it.id ?: "", it.word, it.definition, it.created_at ?: "")
                }
                withContext(Dispatchers.Main) { callback.onSuccess(items) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Failed to load keywords") }
            }
        }
    }

    fun getCurrentLevel(userId: String, callback: SupabaseCallback<Int>) {
        scope.launch {
            try {
                val response = client.postgrest.from("level_progress")
                    .select {
                        filter { eq("user_id", userId) }
                    }
                
                if (response.data == "[]") {
                    Log.d("SupabaseManager", "No level progress record found for user $userId, defaulting to 1")
                    withContext(Dispatchers.Main) { callback.onSuccess(1) }
                    return@launch
                }

                val result = response.decodeSingle<LevelProgressResponse>()
                Log.d("SupabaseManager", "Fetched current level for user $userId: ${result.current_level}")
                withContext(Dispatchers.Main) { callback.onSuccess(result.current_level) }
            } catch (e: Exception) {
                Log.e("SupabaseManager", "getCurrentLevel error: ${e.message}")
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Failed to get level") }
            }
        }
    }

    fun updateLevel(userId: String, newLevel: Int, callback: SupabaseCallback<Boolean>) {
        scope.launch {
            try {
                Log.d("SupabaseManager", "Updating level for user $userId to $newLevel")
                client.postgrest.from("level_progress")
                    .update({ set("current_level", newLevel) }) {
                        filter { eq("user_id", userId) }
                    }
                Log.d("SupabaseManager", "Level updated successfully to $newLevel")
                withContext(Dispatchers.Main) { callback.onSuccess(true) }
            } catch (e: Exception) {
                Log.e("SupabaseManager", "updateLevel error: ${e.message}")
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Failed to update level") }
            }
        }
    }

    fun checkKeywordExists(userId: String, word: String, callback: SupabaseCallback<Boolean>) {
        scope.launch {
            try {
                val result = client.postgrest.from("keywords")
                    .select {
                        filter {
                            eq("user_id", userId)
                            eq("word", word)
                        }
                    }
                withContext(Dispatchers.Main) {
                    callback.onSuccess(result.data != "[]")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Check failed") }
            }
        }
    }

    fun deleteKeyword(keywordId: String, callback: SupabaseCallback<Unit>) {
        scope.launch {
            try {
                client.postgrest.from("keywords").delete {
                    filter {
                        eq("id", keywordId)
                    }
                }
                withContext(Dispatchers.Main) { callback.onSuccess(Unit) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { callback.onError(e.message ?: "Failed to delete keyword") }
            }
        }
    }
}
