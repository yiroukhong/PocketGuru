package com.example.pocketguru.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.pocketguru.models.KeywordItem;
import com.example.pocketguru.supabase.SupabaseManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DataPreloader {
    
    public interface PreloadCallback {
        void onPreloadComplete();
    }
    
    // Singleton cache
    private static List<KeywordItem> cachedKeywords = null;
    private static int cachedCurrentLevel = 1;
    
    public static List<KeywordItem> getCachedKeywords() { return cachedKeywords; }
    public static int getCachedCurrentLevel() { return cachedCurrentLevel; }
    
    public static void setCachedKeywords(List<KeywordItem> keywords) { 
        cachedKeywords = keywords; 
    }

    public static void setCachedCurrentLevel(int level) {
        cachedCurrentLevel = level;
    }
    
    public static void preload(Context context, PreloadCallback callback) {
        String userId = new SessionManager(context).getUserId();
        
        if (userId == null) {
            // Not logged in, nothing to preload
            callback.onPreloadComplete();
            return;
        }
        
        // Use a CountDownLatch-style counter for two parallel fetches
        AtomicInteger pendingCount = new AtomicInteger(2);
        Runnable checkDone = () -> {
            if (pendingCount.decrementAndGet() == 0) {
                new Handler(Looper.getMainLooper()).post(callback::onPreloadComplete);
            }
        };
        
        // Fetch keywords
        SupabaseManager.INSTANCE.getKeywords(userId, new SupabaseManager.SupabaseCallback<List<KeywordItem>>() {
            @Override
            public void onSuccess(List<KeywordItem> result) {
                cachedKeywords = result;
                checkDone.run();
            }
            @Override
            public void onError(String error) {
                cachedKeywords = new ArrayList<>(); // empty on error
                checkDone.run();
            }
        });
        
        // Fetch current level
        SupabaseManager.INSTANCE.getCurrentLevel(userId, new SupabaseManager.SupabaseCallback<Integer>() {
            @Override
            public void onSuccess(Integer level) {
                cachedCurrentLevel = level;
                checkDone.run();
            }
            @Override
            public void onError(String error) {
                cachedCurrentLevel = 1;
                checkDone.run();
            }
        });
    }
}
