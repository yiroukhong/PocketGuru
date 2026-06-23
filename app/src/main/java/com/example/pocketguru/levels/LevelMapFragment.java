package com.example.pocketguru.levels;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pocketguru.R;
import com.example.pocketguru.supabase.SupabaseManager;
import com.example.pocketguru.utils.DataPreloader;
import com.example.pocketguru.utils.SessionManager;
import com.example.pocketguru.utils.SoundManager;
import com.example.pocketguru.utils.ToastHelper;

public class LevelMapFragment extends Fragment {

    private FrameLayout nodeFrame1, nodeFrame2, nodeFrame3, nodeFrame4, nodeFrame5, nodeFrame6, nodeFrameAssessment;
    private ImageView nodeBg1, nodeBg2, nodeBg3, nodeBg4, nodeBg5, nodeBg6, nodeBgAssessment;
    private ImageView nodeIcon1, nodeIcon2, nodeIcon3, nodeIcon4, nodeIcon5, nodeIcon6, nodeIconAssessment;
    private ImageButton btnFlashcards, btnMixMatch;

    private void handleNodeClick(int levelNumber, int actionId) {
        int currentLevel = DataPreloader.getCachedCurrentLevel();
        if (levelNumber <= currentLevel) {
            SoundManager.getInstance(requireContext()).playStart();
            navigateToLevel(actionId);
        } else {
            ToastHelper.show(requireContext(), "This level is locked! Complete previous levels to unlock.", ToastHelper.ToastType.INFO);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level_map, container, false);

        // Find Level 1
        nodeFrame1 = view.findViewById(R.id.nodeFrame1);
        nodeBg1 = view.findViewById(R.id.nodeBg1);
        nodeIcon1 = view.findViewById(R.id.nodeIcon1);

        // Find Level 2
        nodeFrame2 = view.findViewById(R.id.nodeFrame2);
        nodeBg2 = view.findViewById(R.id.nodeBg2);
        nodeIcon2 = view.findViewById(R.id.nodeIcon2);

        // Find Level 3
        nodeFrame3 = view.findViewById(R.id.nodeFrame3);
        nodeBg3 = view.findViewById(R.id.nodeBg3);
        nodeIcon3 = view.findViewById(R.id.nodeIcon3);

        // Find Level 4
        nodeFrame4 = view.findViewById(R.id.nodeFrame4);
        nodeBg4 = view.findViewById(R.id.nodeBg4);
        nodeIcon4 = view.findViewById(R.id.nodeIcon4);

        // Find Level 5
        nodeFrame5 = view.findViewById(R.id.nodeFrame5);
        nodeBg5 = view.findViewById(R.id.nodeBg5);
        nodeIcon5 = view.findViewById(R.id.nodeIcon5);

        // Find Level 6
        nodeFrame6 = view.findViewById(R.id.nodeFrame6);
        nodeBg6 = view.findViewById(R.id.nodeBg6);
        nodeIcon6 = view.findViewById(R.id.nodeIcon6);

        // Find Assessment
        nodeFrameAssessment = view.findViewById(R.id.nodeFrameAssessment);
        nodeBgAssessment = view.findViewById(R.id.nodeBgAssessment);
        nodeIconAssessment = view.findViewById(R.id.nodeIconAssessment);

        // Find Mini-games
        btnFlashcards = view.findViewById(R.id.btn_flashcards);
        btnMixMatch = view.findViewById(R.id.btn_mix_match);

        // Navigation logic for levels
        nodeFrame1.setOnClickListener(v -> handleNodeClick(1, R.id.action_LevelMapFragment_to_LevelOneFragment));
        nodeFrame2.setOnClickListener(v -> handleNodeClick(2, R.id.action_LevelMapFragment_to_LevelTwoFragment));
        nodeFrame3.setOnClickListener(v -> handleNodeClick(3, R.id.action_LevelMapFragment_to_LevelThreeFragment));
        nodeFrame4.setOnClickListener(v -> handleNodeClick(4, R.id.action_LevelMapFragment_to_LevelFourFragment));
        nodeFrame5.setOnClickListener(v -> handleNodeClick(5, R.id.action_LevelMapFragment_to_LevelFiveFragment));
        nodeFrame6.setOnClickListener(v -> handleNodeClick(6, R.id.action_LevelMapFragment_to_LevelSixFragment));
        nodeFrameAssessment.setOnClickListener(v -> handleNodeClick(7, R.id.AssessmentFragment));

        // Navigation for mini-games
        btnFlashcards.setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playStart();
            navigateToLevel(R.id.FlashcardsFragment);
        });
        btnMixMatch.setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playStart();
            navigateToLevel(R.id.MixAndMatchFragment);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Apply colors before first frame renders
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                updateLevelNodes(DataPreloader.getCachedCurrentLevel());
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Live refresh from Supabase in background
        updateLevelNodes(DataPreloader.getCachedCurrentLevel()); // instant from cache
        String userId = new SessionManager(requireContext()).getUserId();
        if (userId != null) {
            SupabaseManager.INSTANCE.getCurrentLevel(userId, new SupabaseManager.SupabaseCallback<Integer>() {
                @Override
                public void onSuccess(Integer level) {
                    DataPreloader.setCachedCurrentLevel(level);
                    updateLevelNodes(level);
                }
                @Override
                public void onError(String error) {}
            });
        }
    }

    private void updateLevelNodes(int currentLevel) {
        Log.d("PocketGuru", "Updating level nodes with level: " + currentLevel);

        // Arrays of background ImageViews and icon ImageViews in order
        ImageView[] nodeBgs = { nodeBg1, nodeBg2, nodeBg3, nodeBg4, nodeBg5, nodeBg6, nodeBgAssessment };
        ImageView[] nodeIcons = { nodeIcon1, nodeIcon2, nodeIcon3, nodeIcon4, nodeIcon5, nodeIcon6, nodeIconAssessment };
        FrameLayout[] nodeFrames = { nodeFrame1, nodeFrame2, nodeFrame3, nodeFrame4, nodeFrame5, nodeFrame6, nodeFrameAssessment };

        for (int i = 0; i < nodeBgs.length; i++) {
            boolean unlocked = (i + 1) <= currentLevel;

            if (unlocked) {
                // Yellow background, white icon
                nodeBgs[i].setColorFilter(Color.parseColor("#FFD93D"), PorterDuff.Mode.SRC_IN);
                nodeIcons[i].setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                nodeFrames[i].setAlpha(1.0f);
            } else {
                // Grey background, grey icon
                nodeBgs[i].setColorFilter(Color.parseColor("#CCCCCC"), PorterDuff.Mode.SRC_IN);
                nodeIcons[i].setColorFilter(Color.parseColor("#999999"), PorterDuff.Mode.SRC_IN);
                nodeFrames[i].setAlpha(0.6f);
            }
            // Always keep clickable to show "Locked" toast
            nodeFrames[i].setClickable(true);
            nodeFrames[i].setEnabled(true);
        }
    }

    private void navigateToLevel(int actionId) {
        Navigation.findNavController(requireView()).navigate(actionId);
    }
}
