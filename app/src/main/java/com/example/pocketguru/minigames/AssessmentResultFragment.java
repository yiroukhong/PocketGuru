package com.example.pocketguru.minigames;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pocketguru.R;
import com.example.pocketguru.utils.LevelProgressManager;

import java.io.Serializable;

public class AssessmentResultFragment extends Fragment {

    public static final String ARG_SCORE = "score";
    public static final String ARG_USER_ANSWERS = "userAnswers";
    public static final String ARG_IS_CORRECT = "isCorrect";

    private int score;
    private int[][] userAnswers;
    private boolean[] isCorrect;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assessment_result, container, false);

        if (getArguments() != null) {
            score = getArguments().getInt(ARG_SCORE);
            userAnswers = (int[][]) getArguments().getSerializable(ARG_USER_ANSWERS);
            isCorrect = getArguments().getBooleanArray(ARG_IS_CORRECT);
        }

        setupUI(view);

        return view;
    }

    private void setupUI(View view) {
        ImageView imageResult = view.findViewById(R.id.image_result);
        TextView textStatus = view.findViewById(R.id.text_status);
        TextView textScore = view.findViewById(R.id.text_score);
        Button btnContinue = view.findViewById(R.id.btn_continue);
        Button btnReview = view.findViewById(R.id.btn_review);

        boolean passed = score >= 7;

        if (passed) {
            imageResult.setImageResource(R.drawable.level_complete_icon);
            textStatus.setText("You rock!");
            btnContinue.setText("Continue");
            btnReview.setText("Review assessment");
        } else {
            imageResult.setImageResource(R.drawable.assess_try_again);
            textStatus.setText("So close!");
            btnContinue.setText("Try again");
            btnReview.setText("Review answers");
        }

        textScore.setText("You got " + score + "/8 correct");

        btnContinue.setOnClickListener(v -> {
            if (passed) {
                handleAssessmentPass();
            } else {
                Navigation.findNavController(v).popBackStack(R.id.AssessmentFragment, false);
            }
        });

        btnReview.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putBoolean("isReview", true);
            args.putSerializable("userAnswers", userAnswers);
            args.putBooleanArray("isCorrect", isCorrect);
            Navigation.findNavController(v).navigate(R.id.AssessmentFragment, args);
        });
    }

    private void handleAssessmentPass() {
        LevelProgressManager.completeLevel(requireContext(), 7, () -> {
            if (isAdded()) {
                Navigation.findNavController(requireView()).popBackStack(R.id.LevelMapFragment, false);
            }
        }, () -> Toast.makeText(getContext(), "Failed to save progress", Toast.LENGTH_SHORT).show());
    }
}
