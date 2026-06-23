package com.example.pocketguru.levels;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pocketguru.R;
import com.example.pocketguru.utils.LevelProgressManager;
import com.example.pocketguru.utils.ToastHelper;

public class Level6Page4Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level6_page4, container, false);

        view.findViewById(R.id.btn_complete).setOnClickListener(v -> {
            LevelProgressManager.completeLevel(requireContext(), 6, () -> {
                if (isAdded()) {
                    Bundle args = new Bundle();
                    args.putString(LevelCompleteFragment.ARG_CHAPTER_NAME, "Photosynthesis");
                    args.putString(LevelCompleteFragment.ARG_LEVEL_NAME, "Level 6: What's next?");
                    Navigation.findNavController(v).navigate(R.id.LevelCompleteFragment, args);
                }
            }, () -> {
                ToastHelper.show(getContext(),"Failed to save progress", ToastHelper.ToastType.ERROR);
            });
        });

        return view;
    }
}
