package com.example.pocketguru.levels;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.pocketguru.R;
import com.example.pocketguru.supabase.SupabaseManager;
import com.example.pocketguru.utils.SessionManager;

import kotlin.Unit;

public class LevelMapFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level_map, container, false);

        view.findViewById(R.id.text_title).setOnClickListener(v -> handleLogout());

        return view;
    }

    private void handleLogout() {
        SessionManager sessionManager = new SessionManager(requireContext());
        SupabaseManager.INSTANCE.signOut(new SupabaseManager.SupabaseCallback<Unit>() {
            @Override
            public void onSuccess(Unit result) {
                sessionManager.clearSession();
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.nav_graph, true)
                        .build();
                Navigation.findNavController(requireView()).navigate(R.id.WelcomeFragment, null, navOptions);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Logout error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
