package com.example.pocketguru.levels;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pocketguru.R;
import com.example.pocketguru.utils.KeywordTooltipHelper;
import com.example.pocketguru.utils.LevelProgressManager;
import com.example.pocketguru.utils.SoundManager;
import com.example.pocketguru.utils.SpannableHelper;
import com.example.pocketguru.views.DrawingView;

public class LevelTwoFragment extends Fragment {

    private DrawingView drawingView;
    private Button btnAction;
    private KeywordTooltipHelper tooltipHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level_two, container, false);

        tooltipHelper = new KeywordTooltipHelper(requireContext());
        drawingView = view.findViewById(R.id.drawing_view);
        btnAction = view.findViewById(R.id.btn_action);

        SoundManager soundManager = SoundManager.getInstance(requireContext());

        drawingView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    soundManager.playSketchLoop();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    soundManager.stopSketch();
                    break;
            }
            return false; // let DrawingView handle the rest
        });

        setupBodyText(view.findViewById(R.id.text_body));

        drawingView.setOnLineCompleteListener(() -> {
            drawingView.lockDrawing();
            btnAction.setEnabled(true);
            Toast.makeText(getContext(), "Success! Water reached the leaves.", Toast.LENGTH_SHORT).show();
        });


        view.findViewById(R.id.btn_back).setOnClickListener(v -> requireActivity().onBackPressed());

        btnAction.setOnClickListener(v -> handleLevelComplete());

        return view;
    }

    private void setupBodyText(TextView textView) {
        String text = "Water is absorbed through the roots and transported up the stem via the water carrying tubes (xylem) to the leaves";
        String keyword = "xylem";
        String definition = "The water-conducting tissue in plants that transports water and dissolved minerals from the roots to the rest of the plant.";

        textView.setText(SpannableHelper.makeKeywordSpan(text, keyword, v -> 
            tooltipHelper.show(v, keyword, definition)
        ));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void handleLevelComplete() {
        LevelProgressManager.completeLevel(requireContext(), 2, () -> {
            if (isAdded()) {
                Bundle args = new Bundle();
                args.putString(LevelCompleteFragment.ARG_CHAPTER_NAME, "Photosynthesis");
                args.putString(LevelCompleteFragment.ARG_LEVEL_NAME, "Level 2: Get the water");
                Navigation.findNavController(requireView()).navigate(R.id.LevelCompleteFragment, args);
            }
        }, () -> {
            Toast.makeText(getContext(), "Failed to save progress", Toast.LENGTH_SHORT).show();
        });
    }
}
