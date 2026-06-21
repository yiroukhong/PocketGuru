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
import com.example.pocketguru.utils.DataPreloader;
import com.example.pocketguru.utils.SessionManager;

public class SplashFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionManager sessionManager = new SessionManager(requireContext());

        if (sessionManager.isLoggedIn()) {
            // Preload data, then navigate
            DataPreloader.preload(requireContext(), () -> {
                navigateAfterSplash(sessionManager);
            });

            // Safety timeout — navigate after 5 seconds max even if preload hangs
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                navigateAfterSplash(sessionManager);
            }, 5000);
        } else {
            // Not logged in, just wait 2 seconds then go to Welcome
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                navigateAfterSplash(sessionManager);
            }, 2000);
        }
    }

    private boolean hasNavigated = false;

    private void navigateAfterSplash(SessionManager sessionManager) {
        if (hasNavigated || !isAdded()) return;
        hasNavigated = true;

        if (sessionManager.isLoggedIn()) {
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_SplashFragment_to_LevelMapFragment);
        } else {
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_SplashFragment_to_WelcomeFragment);
        }
    }
}
