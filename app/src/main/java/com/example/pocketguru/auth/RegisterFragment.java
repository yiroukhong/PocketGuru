package com.example.pocketguru.auth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pocketguru.R;
import com.example.pocketguru.supabase.SupabaseClient;
import com.example.pocketguru.utils.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterFragment extends Fragment {

    private EditText editUsername, editPassword;
    private TextView textUsernameError;
    private ProgressBar progressBar;
    private View btnRegister;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        editUsername = view.findViewById(R.id.edit_username);
        editPassword = view.findViewById(R.id.edit_password);
        textUsernameError = view.findViewById(R.id.text_username_error);
        progressBar = view.findViewById(R.id.progress_register);
        btnRegister = view.findViewById(R.id.btn_register);
        sessionManager = new SessionManager(requireContext());

        btnRegister.setOnClickListener(v -> handleRegistration());

        view.findViewById(R.id.text_login_link).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_RegisterFragment_to_LoginFragment));

        return view;
    }

    private void handleRegistration() {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        textUsernameError.setVisibility(View.GONE);

        executorService.execute(() -> {
            // Note: Since io.github.jan-tennert.supabase is Kotlin-first 
            // and heavily uses suspend functions and Kotlin DSLs, 
            // full implementation in Java requires a Kotlin bridge.
            // This scaffold demonstrates the intended flow.
            
            try {
                // Mocking the Supabase process for now to allow for project flow testing
                Thread.sleep(1500); 

                mainHandler.post(() -> {
                    setLoading(false);
                    // Mock success
                    sessionManager.saveSession("mock_token_for_" + username);
                    Toast.makeText(getContext(), "Registration successful (Mock)", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(btnRegister).navigate(R.id.LevelMapFragment);
                });

            } catch (InterruptedException e) {
                mainHandler.post(() -> setLoading(false));
            }
        });
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!isLoading);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
