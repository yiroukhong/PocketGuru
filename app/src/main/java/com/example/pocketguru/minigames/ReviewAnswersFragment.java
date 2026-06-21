package com.example.pocketguru.minigames;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketguru.R;
import com.example.pocketguru.models.Question;

import java.util.ArrayList;
import java.util.List;

public class ReviewAnswersFragment extends Fragment {

    public static final String ARG_QUESTIONS = "questions";
    public static final String ARG_USER_ANSWERS = "user_answers";
    public static final String ARG_IS_CORRECT = "is_correct";

    private List<Question> questions;
    private int[][] userAnswers;
    private boolean[] isCorrect;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_answers, container, false);

        if (getArguments() != null) {
            questions = (List<Question>) getArguments().getSerializable(ARG_QUESTIONS);
            // Handling the conversion from Object array back to 2D int array if needed,
            // or if we passed it as a Serializable object.
            userAnswers = (int[][]) getArguments().getSerializable(ARG_USER_ANSWERS);
            isCorrect = getArguments().getBooleanArray(ARG_IS_CORRECT);
        }

        RecyclerView recyclerView = view.findViewById(R.id.recycler_review);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new ReviewQuestionAdapter(questions, userAnswers, isCorrect));

        view.findViewById(R.id.btn_back_to_home).setOnClickListener(v -> 
            Navigation.findNavController(v).popBackStack(R.id.LevelMapFragment, false)
        );

        return view;
    }
}
