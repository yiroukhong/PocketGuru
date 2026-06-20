package com.example.pocketguru;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.pocketguru.databinding.ActivityMainBinding;
import com.example.pocketguru.supabase.SupabaseManager;
import com.example.pocketguru.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            BottomNavigationView navView = binding.bottomNavigation;
            NavigationUI.setupWithNavController(navView, navController);

            // Hide bottom nav on auth screens
            Set<Integer> authScreens = new HashSet<>(Arrays.asList(
                    R.id.SplashFragment,
                    R.id.WelcomeFragment,
                    R.id.RegisterFragment,
                    R.id.LoginFragment
            ));

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (authScreens.contains(destination.getId())) {
                    navView.setVisibility(View.GONE);
                } else {
                    navView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public void checkSessionAndNavigate() {
        // Bypassing auth for now
        if (navController != null) {
            navController.navigate(R.id.action_SplashFragment_to_LevelMapFragment);
        }
    }
}
