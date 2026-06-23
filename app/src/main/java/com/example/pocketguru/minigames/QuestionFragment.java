package com.example.pocketguru.minigames;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pocketguru.R;
import com.example.pocketguru.models.Question;
import com.example.pocketguru.utils.SoundManager;

import java.util.ArrayList;
import java.util.List;

public class QuestionFragment extends Fragment {

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(int questionIndex, int[] selectedIndices, boolean correct);
        void onNextTapped();
    }

    private static final String ARG_QUESTION = "question";
    private static final String ARG_INDEX = "index";
    private static final String ARG_IS_REVIEW = "isReview";
    private static final String ARG_USER_ANSWERS = "userAnswers";

    private Question question;
    private int questionIndex;
    private boolean isReviewMode;
    private int[] previousAnswers;

    private List<View> optionViews = new ArrayList<>();
    private List<Integer> selectedIndicesList = new ArrayList<>();
    private Button btnNext, btnCheck;
    private TextView textFeedback;

    public static QuestionFragment newInstance(Question question, int index, boolean isReview, int[] userAnswers) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUESTION, question);
        args.putInt(ARG_INDEX, index);
        args.putBoolean(ARG_IS_REVIEW, isReview);
        args.putIntArray(ARG_USER_ANSWERS, userAnswers);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable(ARG_QUESTION);
            questionIndex = getArguments().getInt(ARG_INDEX);
            isReviewMode = getArguments().getBoolean(ARG_IS_REVIEW);
            previousAnswers = getArguments().getIntArray(ARG_USER_ANSWERS);
        }

        int layoutId = (question.getType() == Question.QuestionType.TEXT_OPTIONS) 
                ? R.layout.fragment_question_text : R.layout.fragment_question_image;
        
        View view = inflater.inflate(layoutId, container, false);

        setupCommonUI(view);
        if (question.getType() == Question.QuestionType.TEXT_OPTIONS) {
            setupTextOptions(view);
        } else {
            setupImageOptions(view);
        }

        if (isReviewMode) {
            showReviewState();
        }

        return view;
    }

    private void setupCommonUI(View view) {
        TextView textQuestion = view.findViewById(R.id.text_question);
        textQuestion.setText(question.getQuestionText());

        ImageView imageIllustration = view.findViewById(R.id.image_illustration);
        if (question.getIllustrationResId() != 0) {
            imageIllustration.setImageResource(question.getIllustrationResId());
            imageIllustration.setVisibility(View.VISIBLE);
        }

        textFeedback = view.findViewById(R.id.text_feedback);
        btnNext = view.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v -> {
            if (getParentFragment() instanceof OnAnswerSelectedListener) {
                ((OnAnswerSelectedListener) getParentFragment()).onNextTapped();
            }
        });

        btnCheck = view.findViewById(R.id.btn_check);
        if (btnCheck != null) {
            btnCheck.setOnClickListener(v -> handleCheckClick());
        }
    }

    private void setupTextOptions(View view) {
        int[] ids = {R.id.option1, R.id.option2, R.id.option3, R.id.option4};
        String[] options = question.getOptions();

        for (int i = 0; i < ids.length; i++) {
            Button btn = view.findViewById(ids[i]);
            btn.setText(options[i]);
            int index = i;
            btn.setOnClickListener(v -> handleOptionClick(index));
            optionViews.add(btn);
        }
    }

    private void setupImageOptions(View view) {
        int[] containerIds = {R.id.option1_container, R.id.option2_container, R.id.option3_container, R.id.option4_container};
        int[] imageIds = {R.id.option1_image, R.id.option2_image, R.id.option3_image, R.id.option4_image};
        int[] drawables = question.getImageResIds();

        for (int i = 0; i < containerIds.length; i++) {
            View container = view.findViewById(containerIds[i]);
            ImageView img = view.findViewById(imageIds[i]);
            img.setImageResource(drawables[i]);
            int index = i;
            container.setOnClickListener(v -> handleOptionClick(index));
            optionViews.add(container);
        }
    }

    private void handleOptionClick(int index) {
        if (isReviewMode) return;

        boolean isMultiSelect = question.getCorrectIndexes().length > 1;

        if (isMultiSelect) {
            if (selectedIndicesList.contains(index)) {
                selectedIndicesList.remove((Integer) index);
                setOptionColor(index, Color.parseColor("#E8A598")); // Reset to salmon
            } else {
                selectedIndicesList.add(index);
                setOptionColor(index, Color.parseColor("#FFD93D")); // Highlight selected
            }
            btnCheck.setVisibility(!selectedIndicesList.isEmpty() ? View.VISIBLE : View.GONE);
        } else {
            // Single select - immediate feedback
            selectedIndicesList.clear();
            selectedIndicesList.add(index);
            boolean correct = question.isCorrect(new int[]{index});
            
            showFeedback(index, correct);
            
            if (getParentFragment() instanceof OnAnswerSelectedListener) {
                ((OnAnswerSelectedListener) getParentFragment()).onAnswerSelected(questionIndex, new int[]{index}, correct);
            }
        }
    }

    private void handleCheckClick() {
        int[] selected = new int[selectedIndicesList.size()];
        for (int i = 0; i < selectedIndicesList.size(); i++) {
            selected[i] = selectedIndicesList.get(i);
        }

        boolean isFullyCorrect = question.isCorrect(selected);
        showMultiFeedback(selected, isFullyCorrect);

        if (getParentFragment() instanceof OnAnswerSelectedListener) {
            ((OnAnswerSelectedListener) getParentFragment()).onAnswerSelected(questionIndex, selected, isFullyCorrect);
        }
    }

    private void showMultiFeedback(int[] selected, boolean isFullyCorrect) {
        btnCheck.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);

        int[] correctIndexes = question.getCorrectIndexes();
        
        for (int i = 0; i < optionViews.size(); i++) {
            optionViews.get(i).setEnabled(false);
            
            boolean wasSelected = false;
            for (int s : selected) if (s == i) wasSelected = true;
            
            boolean isCorrectIdx = false;
            for (int c : correctIndexes) if (c == i) isCorrectIdx = true;

            if (isCorrectIdx) {
                setOptionColor(i, Color.parseColor("#81C784")); // Correct answer -> green
            } else if (wasSelected) {
                setOptionColor(i, Color.parseColor("#E57373")); // Wrong selection -> red
            } else {
                optionViews.get(i).setAlpha(0.5f);
            }
        }

        textFeedback.setVisibility(View.VISIBLE);
        if (isFullyCorrect) {
            SoundManager.getInstance(requireContext()).playCorrect();
            textFeedback.setText("Amazing!");
            textFeedback.setTextColor(Color.parseColor("#81C784"));
            textFeedback.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.checkbox_on_background, 0, 0, 0);
        } else {
            textFeedback.setText("Not quite! The correct answers are highlighted in green.");
            textFeedback.setTextColor(Color.parseColor("#E57373"));
            textFeedback.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    private void showFeedback(int selectedIndex, boolean correct) {
        for (int i = 0; i < optionViews.size(); i++) {
            optionViews.get(i).setEnabled(false);
            if (i == selectedIndex) {
                setOptionColor(i, correct ? Color.parseColor("#81C784") : Color.parseColor("#E57373"));
                if (!correct) {
                    playShakeAnimation(optionViews.get(i));
                }
            } else {
                optionViews.get(i).setAlpha(0.5f);
                // Also highlight the correct one if user was wrong
                if (!correct) {
                    for (int cIdx : question.getCorrectIndexes()) {
                        if (i == cIdx) {
                            setOptionColor(i, Color.parseColor("#81C784"));
                            optionViews.get(i).setAlpha(1.0f);
                        }
                    }
                }
            }
        }

        if (correct) {
            SoundManager.getInstance(requireContext()).playCorrect();
            textFeedback.setVisibility(View.VISIBLE);
        }
        btnNext.setVisibility(View.VISIBLE);
    }

    private void setOptionColor(int index, int color) {
        View v = optionViews.get(index);
        if (v instanceof Button) {
            ((Button) v).setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
        } else {
            v.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
        }
    }

    private void playShakeAnimation(View view) {
        ObjectAnimator.ofFloat(view, "translationX", 0, 10, -10, 10, -10, 5, -5, 0)
                .setDuration(300)
                .start();
    }

    private void showReviewState() {
        // Logic to show correct/wrong based on previousAnswers
        btnNext.setVisibility(View.VISIBLE);
        // ... implementation of review colors ...
    }
}
