package com.example.pocketguru.levels;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pocketguru.R;

public class LevelCompleteFragment extends Fragment {

    public static final String ARG_CHAPTER_NAME = "chapterName";
    public static final String ARG_LEVEL_NAME = "levelName";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level_complete, container, false);

        TextView textChapter = view.findViewById(R.id.text_chapter_name);
        TextView textLevel = view.findViewById(R.id.text_level_name);

        if (getArguments() != null) {
            textChapter.setText(getArguments().getString(ARG_CHAPTER_NAME));
            textLevel.setText(getArguments().getString(ARG_LEVEL_NAME));
        }

        view.findViewById(R.id.btn_continue).setOnClickListener(v -> {
            Navigation.findNavController(requireView()).popBackStack(R.id.LevelMapFragment, false);
        });

        return view;
    }
}
