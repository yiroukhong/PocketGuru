package com.example.pocketguru.levels;

import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.DragEvent;
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
import com.example.pocketguru.utils.KeywordTooltipHelper;
import com.example.pocketguru.utils.LevelProgressManager;
import com.example.pocketguru.utils.SpannableHelper;

public class LevelFourFragment extends Fragment {

    private int collectedCount = 0;
    private ImageView imageChloroplast;
    private Button btnComplete;
    private KeywordTooltipHelper tooltipHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level_four, container, false);

        tooltipHelper = new KeywordTooltipHelper(requireContext());
        imageChloroplast = view.findViewById(R.id.image_chloroplast);
        btnComplete = view.findViewById(R.id.btn_complete);

        setupKeywords(view.findViewById(R.id.text_body_top));

        // Set up draggables
        setupSunDraggable(view.findViewById(R.id.sun1));
        setupSunDraggable(view.findViewById(R.id.sun2));
        setupSunDraggable(view.findViewById(R.id.sun3));

        // Set up drop target
        imageChloroplast.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                case DragEvent.ACTION_DROP:
                    View draggedView = (View) event.getLocalState();
                    draggedView.setVisibility(View.INVISIBLE);
                    handleSunCollected();
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    if (!event.getResult()) {
                        View sunView = (View) event.getLocalState();
                        animateSnapBack(sunView);
                    }
                    return true;
            }
            return true;
        });

        view.findViewById(R.id.btn_back).setOnClickListener(v -> requireActivity().onBackPressed());
        btnComplete.setOnClickListener(v -> handleLevelComplete());

        return view;
    }

    private void setupKeywords(TextView textView) {
        String text = "In leaves, there is a pigment found in the chloroplast that has one very important role,";
        String pWord = "pigment";
        String pDef = "A colored molecule that absorbs specific wavelengths of light. In plants, the pigment chlorophyll absorbs sunlight for photosynthesis.";
        String cWord = "chloroplast";
        String cDef = "The organelle inside plant cells where photosynthesis takes place. It contains chlorophyll which captures light energy.";

        textView.setText(SpannableHelper.makeKeywordSpan(text, pWord, v -> tooltipHelper.show(v, pWord, pDef)));
        textView.setText(SpannableHelper.makeKeywordSpan(textView.getText().toString(), cWord, v -> tooltipHelper.show(v, cWord, cDef)));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setupSunDraggable(View sun) {
        sun.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                ClipData.Item item = new ClipData.Item("sun");
                ClipData dragData = new ClipData("sun", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                View.DragShadowBuilder shadow = new View.DragShadowBuilder(v);
                v.startDragAndDrop(dragData, shadow, v, 0);
                return true;
            }
            return false;
        });
    }

    private void handleSunCollected() {
        collectedCount++;
        
        // Scale animation on chloroplast
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageChloroplast, "scaleX", 1.0f, 1.2f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageChloroplast, "scaleY", 1.0f, 1.2f, 1.0f);
        scaleX.setDuration(200);
        scaleY.setDuration(200);
        scaleX.start();
        scaleY.start();

        if (collectedCount == 3) {
            btnComplete.setEnabled(true);
            imageChloroplast.setImageResource(R.drawable.chloroplast);
        }
    }

    private void animateSnapBack(View view) {
        ObjectAnimator transX = ObjectAnimator.ofFloat(view, "translationX", view.getTranslationX(), 0f);
        ObjectAnimator transY = ObjectAnimator.ofFloat(view, "translationY", view.getTranslationY(), 0f);
        transX.setDuration(300);
        transY.setDuration(300);
        transX.start();
        transY.start();
    }

    private void handleLevelComplete() {
        LevelProgressManager.completeLevel(requireContext(), 4, () -> {
            if (isAdded()) {
                Bundle args = new Bundle();
                args.putString(LevelCompleteFragment.ARG_CHAPTER_NAME, "Photosynthesis");
                args.putString(LevelCompleteFragment.ARG_LEVEL_NAME, "Level 4: Get the sunlight");
                Navigation.findNavController(requireView()).navigate(R.id.LevelCompleteFragment, args);
            }
        }, () -> Toast.makeText(getContext(), "Failed to save progress", Toast.LENGTH_SHORT).show());
    }
}
