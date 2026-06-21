package com.example.pocketguru.minigames;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pocketguru.R;
import com.example.pocketguru.models.Question;

import java.util.ArrayList;
import java.util.List;

public class AssessmentFragment extends Fragment implements QuestionFragment.OnAnswerSelectedListener {

    private ViewPager2 viewPager;
    private View[] segments;
    private List<Question> questions = new ArrayList<>();
    
    // Tracking answers
    private int[][] userAnswers = new int[8][]; // Array of selected indices for each Q
    private boolean[] isCorrect = new boolean[8];
    
    private final int COLOR_UNFILLED = Color.parseColor("#D3D3D3");
    private final int COLOR_FILLED = Color.parseColor("#E8A598");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assessment, container, false);

        initQuestions();

        segments = new View[]{
                view.findViewById(R.id.seg1), view.findViewById(R.id.seg2),
                view.findViewById(R.id.seg3), view.findViewById(R.id.seg4),
                view.findViewById(R.id.seg5), view.findViewById(R.id.seg6),
                view.findViewById(R.id.seg7), view.findViewById(R.id.seg8)
        };

        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new AssessmentPagerAdapter(this));
        viewPager.setUserInputEnabled(false);

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current == 0) {
                requireActivity().onBackPressed();
            } else {
                viewPager.setCurrentItem(current - 1, true);
                updateProgressSegments(current - 1);
            }
        });

        return view;
    }

    private void initQuestions() {
        questions.add(new Question("Why are many plants green?", Question.QuestionType.TEXT_OPTIONS, 
                new String[]{"plant cells are green", "plants absorb green light", "rigid cell walls are green", "chlorophyll reflects green light"}, 
                new int[]{3}, R.drawable.assess_plant_green));

        questions.add(new Question("Chloroplasts are...", Question.QuestionType.TEXT_OPTIONS, 
                new String[]{"A type of pigment used for photosynthesis", "The location inside a plant cell where photosynthesis happens", "The location where gaseous exchange happens", "Gives the green colour of plants"}, 
                new int[]{1}, R.drawable.chloroplast));

        questions.add(new Question("What do plants give off after photosynthesis?", Question.QuestionType.IMAGE_OPTIONS, 
                new String[]{"CO2", "Oxygen", "Water", "Methane"}, 
                new int[]{R.drawable.item_co2, R.drawable.item_oxygen, R.drawable.item_water, R.drawable.item_methane}, 
                new int[]{1}, 0));

        questions.add(new Question("Plants need ________ from the air for photosynthesis.", Question.QuestionType.IMAGE_OPTIONS, 
                new String[]{"CO2", "Oxygen", "Sugar", "Soil"}, 
                new int[]{R.drawable.item_co2, R.drawable.item_oxygen, R.drawable.item_sugar, R.drawable.item_soil}, 
                new int[]{0}, 0));

        questions.add(new Question("The by-products of photosynthesis are", Question.QuestionType.IMAGE_OPTIONS, 
                new String[]{"CO2", "Oxygen", "Sugar", "Soil"}, 
                new int[]{R.drawable.item_co2, R.drawable.item_oxygen, R.drawable.item_sugar, R.drawable.item_soil}, 
                new int[]{1, 2}, 0));

        questions.add(new Question("The main source of energy for all life comes from", Question.QuestionType.IMAGE_OPTIONS, 
                new String[]{"Earth", "Food", "Moon", "Sun"}, 
                new int[]{R.drawable.assess_earth, R.drawable.assess_food, R.drawable.assess_moon, R.drawable.sun_icon},
                new int[]{3}, 0));

        questions.add(new Question("How does water reach the plant's leaves?", Question.QuestionType.TEXT_OPTIONS, 
                new String[]{"Into the leaves, up the stem and through the stomata", "In the leaves, up the stem, into the roots", "Up the stem, into the roots, into the leaves", "Into the roots, up the stem, and to the leaves"}, 
                new int[]{3}, R.drawable.level2_plant));

        questions.add(new Question("The small holes in the plant's leaves that take in carbon dioxide are called the", Question.QuestionType.IMAGE_OPTIONS, 
                new String[]{"Carbon Dioxide", "Stomata", "Chloroplasts", "Stem"}, 
                new int[]{1}, R.drawable.stomata_diagram));
    }

    @Override
    public void onAnswerSelected(int questionIndex, int[] selectedIndices, boolean correct) {
        userAnswers[questionIndex] = selectedIndices;
        isCorrect[questionIndex] = correct;
        updateProgressSegments(questionIndex);
    }

    @Override
    public void onNextTapped() {
        int current = viewPager.getCurrentItem();
        if (current < questions.size() - 1) {
            viewPager.setCurrentItem(current + 1, true);
        } else {
            navigateToResults();
        }
    }

    private void updateProgressSegments(int position) {
        for (int i = 0; i < segments.length; i++) {
            if (i <= position) {
                if (i == position && segments[i].getBackgroundTintList() == null) {
                    animateSegmentColor(segments[i], COLOR_UNFILLED, COLOR_FILLED);
                } else {
                    segments[i].setBackgroundColor(COLOR_FILLED);
                }
            } else {
                segments[i].setBackgroundColor(COLOR_UNFILLED);
            }
        }
    }

    private void animateSegmentColor(View segment, int fromColor, int toColor) {
        ValueAnimator animator = ValueAnimator.ofArgb(fromColor, toColor);
        animator.setDuration(300);
        animator.addUpdateListener(animation -> segment.setBackgroundColor((int) animation.getAnimatedValue()));
        animator.start();
    }

    private void navigateToResults() {
        int score = 0;
        for (boolean correct : isCorrect) {
            if (correct) score++;
        }

        Bundle args = new Bundle();
        args.putInt(AssessmentResultFragment.ARG_SCORE, score);
        args.putSerializable(AssessmentResultFragment.ARG_QUESTIONS, (java.io.Serializable) questions);
        args.putSerializable(AssessmentResultFragment.ARG_USER_ANSWERS, userAnswers);
        args.putBooleanArray(AssessmentResultFragment.ARG_IS_CORRECT, isCorrect);
        
        Navigation.findNavController(requireView()).navigate(R.id.action_AssessmentFragment_to_AssessmentResultFragment, args);
    }

    private class AssessmentPagerAdapter extends FragmentStateAdapter {
        public AssessmentPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return QuestionFragment.newInstance(questions.get(position), position, false, null);
        }

        @Override
        public int getItemCount() {
            return questions.size();
        }
    }
}
