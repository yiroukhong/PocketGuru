package com.example.pocketguru.supabase;

import io.github.jan.supabase.SupabaseClientBuilder;
import io.github.jan.supabase.auth.Auth;
import io.github.jan.supabase.postgrest.Postgrest;
import kotlin.Unit;

public class SupabaseClient {
    private static io.github.jan.supabase.SupabaseClient instance;

    // TODO: Fill in your Supabase credentials
    private static final String SUPABASE_URL = "https://ctzcbnnmkhqltbikjorz.supabase.co";
    private static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN0emNibm5ta2hxbHRiaWtqb3J6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODE4Mzc4NjMsImV4cCI6MjA5NzQxMzg2M30.0_5CmF3sMWotjLzKSezEecsmQRDzt6oXxYtXu2MjMt4";

    public static synchronized io.github.jan.supabase.SupabaseClient getInstance() {
        if (instance == null) {
            SupabaseClientBuilder builder = new SupabaseClientBuilder(SUPABASE_URL, SUPABASE_ANON_KEY);
            builder.install(Auth.Companion, config -> Unit.INSTANCE);
            builder.install(Postgrest.Companion, config -> Unit.INSTANCE);
            instance = builder.build();
        }
        return instance;
    }
}
