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
import com.example.pocketguru.MainActivity;
import com.example.pocketguru.R;

public class SplashFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded() && getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).checkSessionAndNavigate();
            }
        }, 2000);
        
        return view;
    }
}
