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
import com.example.pocketguru.utils.LevelProgressManager;

public class Level6Page3Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level6_page3, container, false);

        view.findViewById(R.id.indicator1).setBackgroundResource(R.color.salmon_pink);
        view.findViewById(R.id.indicator2).setBackgroundResource(R.color.salmon_pink);
        view.findViewById(R.id.indicator3).setBackgroundResource(R.color.salmon_pink);

        view.findViewById(R.id.btn_complete).setOnClickListener(v -> {
            LevelProgressManager.completeLevel(requireContext(), 6, () -> {
                if (isAdded()) {
                    Navigation.findNavController(v).navigateUp();
                }
            }, () -> {
                // Handle error
            });
        });

        return view;
    }
}
