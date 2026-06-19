package com.example.pocketguru.auth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.pocketguru.R;
import com.example.pocketguru.utils.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginFragment extends Fragment {

    private EditText editUsername, editPassword;
    private ProgressBar progressBar;
    private View btnLogin;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        editUsername = view.findViewById(R.id.edit_username);
        editPassword = view.findViewById(R.id.edit_password);
        progressBar = view.findViewById(R.id.progress_login);
        btnLogin = view.findViewById(R.id.btn_login);
        sessionManager = new SessionManager(requireContext());

        btnLogin.setOnClickListener(v -> handleLogin());

        view.findViewById(R.id.text_register_link).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_LoginFragment_to_RegisterFragment));

        return view;
    }

    private void handleLogin() {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        executorService.execute(() -> {
            try {
                // Mocking Supabase Auth signInWithPassword
                Thread.sleep(1500);

                mainHandler.post(() -> {
                    setLoading(false);
                    
                    // Mock validation: simple check for demo purposes
                    if (username.length() > 2 && password.length() > 5) {
                        sessionManager.saveSession("mock_token_for_" + username);
                        
                        // Navigate to LevelMap and clear backstack
                        NavOptions navOptions = new NavOptions.Builder()
                                .setPopUpTo(R.id.nav_graph, true)
                                .build();
                        
                        Navigation.findNavController(btnLogin).navigate(R.id.LevelMapFragment, null, navOptions);
                        Toast.makeText(getContext(), "Login successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Incorrect username or password", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (InterruptedException e) {
                mainHandler.post(() -> setLoading(false));
            }
        });
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!isLoading);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
