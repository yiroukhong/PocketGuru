package com.example.pocketguru.supabase;

import android.util.Log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SupabaseClient {
    private static final String TAG = "SupabaseClient";
    private static SupabaseClient instance;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    private SupabaseClient() {}

    public static synchronized SupabaseClient getInstance() {
        if (instance == null) {
            instance = new SupabaseClient();
        }
        return instance;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void performQuery(String table, String userId, DatabaseCallback callback) {
        executor.execute(() -> {
            try {
                Thread.sleep(800);
                callback.onSuccess(new java.util.ArrayList<>()); 
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public interface DatabaseCallback {
        void onSuccess(Object data);
        void onError(String message);
    }
}
