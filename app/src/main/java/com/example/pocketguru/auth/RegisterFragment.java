package com.example.pocketguru.auth;

import android.os.Bundle;
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
import com.example.pocketguru.supabase.SupabaseManager;
import com.example.pocketguru.utils.SessionManager;
import com.example.pocketguru.utils.ToastHelper;

import kotlin.Unit;

public class RegisterFragment extends Fragment {

    private EditText editUsername, editPassword;
    private TextView textUsernameError;
    private ProgressBar progressBar;
    private View btnRegister;
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
            ToastHelper.show(getContext(),"Username and password cannot be empty", ToastHelper.ToastType.ERROR);
            return;
        }

        setLoading(true);
        textUsernameError.setVisibility(View.GONE);

        // 1. Check if username exists
        SupabaseManager.INSTANCE.checkUsernameUnique(username, new SupabaseManager.SupabaseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean isUnique) {
                if (isUnique) {
                    // 2. Perform Sign Up
                    performSignUp(username, password);
                } else {
                    setLoading(false);
                    textUsernameError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                setLoading(false);
                ToastHelper.show(getContext(),"Failed to register", ToastHelper.ToastType.ERROR);
            }
        });
    }

    private void performSignUp(String username, String password) {
        SupabaseManager.INSTANCE.signUp(username, password, new SupabaseManager.SupabaseCallback<String>() {
            @Override
            public void onSuccess(String sessionToken) {
                setLoading(false);
                
                String[] parts = sessionToken.split("\\|");
                String token = parts[0];
                String userId = parts.length > 1 ? parts[1] : null;
                sessionManager.saveSession(token, userId);

                ToastHelper.show(getContext(),"Registration successfull!", ToastHelper.ToastType.SUCCESS);
                Navigation.findNavController(btnRegister).navigate(R.id.LevelMapFragment);
            }

            @Override
            public void onError(String error) {
                setLoading(false);
                ToastHelper.show(getContext(),error, ToastHelper.ToastType.ERROR);
            }
        });
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!isLoading);
    }
}
