package com.example.pocketguru.supabase;

// Scaffolding Supabase integration
public class SupabaseClient {
    private static Object instance;

    // TODO: Fill in your Supabase credentials
    private static final String SUPABASE_URL = "https://ctzcbnnmkhqltbikjorz.supabase.co";
    private static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN0emNibm5ta2hxbHRiaWtqb3J6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODE4Mzc4NjMsImV4cCI6MjA5NzQxMzg2M30.0_5CmF3sMWotjLzKSezEecsmQRDzt6oXxYtXu2MjMt4";

    public static synchronized Object getInstance() {
        if (instance == null) {
            // TODO: Initialize Supabase client
            // Because supabase-kt is a Kotlin library with suspend functions,
            // it is recommended to create a Kotlin wrapper or use a bridge
            // to access it from Java.
            instance = new Object();
        }
        return instance;
    }
}
