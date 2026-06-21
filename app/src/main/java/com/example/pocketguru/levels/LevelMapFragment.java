package com.example.pocketguru.levels;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pocketguru.R;
import com.example.pocketguru.utils.DataPreloader;

public class LevelMapFragment extends Fragment {

    private ImageButton btnLevel1, btnLevel2, btnLevel3, btnLevel4, btnLevel5, btnLevel6, btnAssessment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level_map, container, false);

        btnLevel1 = view.findViewById(R.id.btn_level_1);
        btnLevel2 = view.findViewById(R.id.btn_level_2);
        btnLevel3 = view.findViewById(R.id.btn_level_3);
        btnLevel4 = view.findViewById(R.id.btn_level_4);
        btnLevel5 = view.findViewById(R.id.btn_level_5);
        btnLevel6 = view.findViewById(R.id.btn_level_6);
        btnAssessment = view.findViewById(R.id.btn_assessment);

        // Navigation logic for levels
        btnLevel1.setOnClickListener(v -> navigateToLevel(R.id.action_LevelMapFragment_to_LevelOneFragment));
        btnLevel2.setOnClickListener(v -> navigateToLevel(R.id.action_LevelMapFragment_to_LevelTwoFragment));
        btnLevel3.setOnClickListener(v -> navigateToLevel(R.id.action_LevelMapFragment_to_LevelThreeFragment));
        btnLevel4.setOnClickListener(v -> navigateToLevel(R.id.action_LevelMapFragment_to_LevelFourFragment));
        btnLevel5.setOnClickListener(v -> navigateToLevel(R.id.action_LevelMapFragment_to_LevelFiveFragment));
        btnLevel6.setOnClickListener(v -> navigateToLevel(R.id.action_LevelMapFragment_to_LevelSixFragment));
        btnAssessment.setOnClickListener(v -> navigateToLevel(R.id.AssessmentFragment));

        // Navigation for mini-games
        view.findViewById(R.id.btn_flashcards).setOnClickListener(v -> navigateToLevel(R.id.FlashcardsFragment));
        view.findViewById(R.id.btn_mix_match).setOnClickListener(v -> navigateToLevel(R.id.MixAndMatchFragment));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLevelNodes();
    }

    private void updateLevelNodes() {
        int currentLevel = DataPreloader.getCachedCurrentLevel();

        // List of all level node ImageViews in order
        ImageButton[] levelNodes = {
                btnLevel1, btnLevel2, btnLevel3,
                btnLevel4, btnLevel5, btnLevel6, btnAssessment
        };

        for (int i = 0; i < levelNodes.length; i++) {
            if (i < currentLevel) {
                // Completed or Current — yellow, clickable
                levelNodes[i].setColorFilter(
                        android.graphics.Color.parseColor("#FFD93D"),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                levelNodes[i].setAlpha(1.0f);
                levelNodes[i].setEnabled(true);
            } else {
                // Locked — grey, not clickable
                levelNodes[i].setColorFilter(
                        android.graphics.Color.parseColor("#AAAAAA"),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                levelNodes[i].setAlpha(0.5f);
                levelNodes[i].setEnabled(false);
            }
        }
    }

    private void navigateToLevel(int actionId) {
        Navigation.findNavController(requireView()).navigate(actionId);
    }
}
