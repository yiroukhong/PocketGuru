package com.example.pocketguru.levels;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pocketguru.R;

public class LevelMapFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level_map, container, false);

        // Navigation logic for levels
        view.findViewById(R.id.btn_level_1).setOnClickListener(v -> navigateToLevel(R.id.action_LevelMapFragment_to_LevelOneFragment));
        view.findViewById(R.id.btn_level_2).setOnClickListener(v -> navigateToLevel(R.id.action_LevelMapFragment_to_LevelTwoFragment));
        view.findViewById(R.id.btn_level_3).setOnClickListener(v -> navigateToLevel(R.id.action_LevelMapFragment_to_LevelThreeFragment));
        view.findViewById(R.id.btn_level_4).setOnClickListener(v -> navigateToLevel(R.id.action_LevelMapFragment_to_LevelFourFragment));
        view.findViewById(R.id.btn_level_5).setOnClickListener(v -> navigateToLevel(R.id.action_LevelMapFragment_to_LevelFiveFragment));
        view.findViewById(R.id.btn_level_6).setOnClickListener(v -> navigateToLevel(R.id.action_LevelMapFragment_to_LevelSixFragment));

        // Navigation for mini-games
        view.findViewById(R.id.btn_flashcards).setOnClickListener(v -> navigateToLevel(R.id.FlashcardsFragment));
        view.findViewById(R.id.btn_mix_match).setOnClickListener(v -> navigateToLevel(R.id.MixAndMatchFragment));

        return view;
    }

    private void navigateToLevel(int actionId) {
        Navigation.findNavController(requireView()).navigate(actionId);
    }
}
