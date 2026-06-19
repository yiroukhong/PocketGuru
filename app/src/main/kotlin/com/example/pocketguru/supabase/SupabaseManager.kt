package com.example.pocketguru.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
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
                    withContext(Dispatchers.Main) { callback.onSuccess(session ?: "Success") }
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
                
                val session = client.auth.currentSessionOrNull()?.accessToken
                withContext(Dispatchers.Main) { 
                    if (session != null) callback.onSuccess(session)
                    else callback.onError("Session not found")
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
}
