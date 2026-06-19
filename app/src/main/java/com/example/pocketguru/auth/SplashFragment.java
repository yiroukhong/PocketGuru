package com.example.pocketguru.auth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.pocketguru.R;

public class SplashFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded()) {
                // TODO: Replace with real session check
                Navigation.findNavController(view).navigate(R.id.action_SplashFragment_to_WelcomeFragment);
            }
        }, 2000);
        
        return view;
    }
}
