package com.example.pocketguru.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "pocketguru_prefs";
    private static final String KEY_SUPABASE_SESSION = "supabase_session";
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(String sessionToken) {
        saveSession(sessionToken, null);
    }

    public void saveSession(String sessionToken, String userId) {
        editor.putString(KEY_SUPABASE_SESSION, sessionToken);
        if (userId != null) {
            editor.putString("user_id", userId);
        }
        editor.apply();
    }

    public String getUserId() {
        return sharedPreferences.getString("user_id", null);
    }

    public String getSessionToken() {
        return sharedPreferences.getString(KEY_SUPABASE_SESSION, null);
    }

    public void clearSession() {
        editor.remove(KEY_SUPABASE_SESSION);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return getSessionToken() != null;
    }
}
