package com.example.pocketguru.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.pocketguru.supabase.SupabaseManager;

public class LevelProgressManager {

    public interface ProgressCallback {
        void onSuccess();
        void onError(String message);
    }

    public static void completeLevel(Context context, int completedLevel, 
                                     Runnable onSuccess, Runnable onError) {
        String userId = new SessionManager(context).getUserId();

        if (userId == null) {
            // Fallback: get from Supabase auth directly
            SupabaseManager.INSTANCE.getCurrentUserId(new SupabaseManager.SupabaseCallback<String>() {
                @Override
                public void onSuccess(String uid) {
                    performUpdate(uid, completedLevel, context, onSuccess, onError);
                }
                @Override
                public void onError(String error) {
                    if (onError != null) new Handler(Looper.getMainLooper()).post(onError);
                }
            });
        } else {
            performUpdate(userId, completedLevel, context, onSuccess, onError);
        }
    }

    private static void performUpdate(String userId, int completedLevel, 
                                      Context context, Runnable onSuccess, Runnable onError) {
        int newLevel = completedLevel + 1;
        SupabaseManager.INSTANCE.updateLevel(userId, newLevel, new SupabaseManager.SupabaseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                // Update the cache too
                DataPreloader.setCachedCurrentLevel(newLevel);
                if (onSuccess != null) new Handler(Looper.getMainLooper()).post(onSuccess);
            }
            @Override
            public void onError(String error) {
                Log.e("PocketGuru", "Failed to update level: " + error);
                if (onError != null) new Handler(Looper.getMainLooper()).post(onError);
            }
        });
    }
}
