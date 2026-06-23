package com.example.pocketguru.minigames;

import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pocketguru.R;
import com.example.pocketguru.utils.SoundManager;
import com.example.pocketguru.views.LineOverlayView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MixAndMatchFragment extends Fragment {

    private static class MatchPair {
        String definition;
        int imageResId;

        MatchPair(String definition, int imageResId) {
            this.definition = definition;
            this.imageResId = imageResId;
        }
    }

    private final List<MatchPair> pairs = new ArrayList<>();
    private final Map<String, Integer> correctMap = new HashMap<>();
    private int matchedCount = 0;

    private LinearLayout columnDefinitions, columnImages;
    private TextView textCounter;
    private LineOverlayView lineOverlay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mix_and_match, container, false);

        initPairs();

        columnDefinitions = view.findViewById(R.id.column_definitions);
        columnImages = view.findViewById(R.id.column_images);
        textCounter = view.findViewById(R.id.text_counter);
        lineOverlay = view.findViewById(R.id.line_overlay);

        setupGame(inflater);

        view.findViewById(R.id.btn_close).setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        return view;
    }

    private void initPairs() {
        pairs.add(new MatchPair("The place where photosynthesis takes place", R.drawable.chloroplast));
        pairs.add(new MatchPair("The gas released from photosynthesis. Humans breathe this", R.drawable.item_oxygen));
        pairs.add(new MatchPair("The process in which green plants make their own food", R.drawable.level1_diagram));
        pairs.add(new MatchPair("Which gas is needed by plants to make food?", R.drawable.item_co2));
        pairs.add(new MatchPair("The sugar made from photosynthesis", R.drawable.item_sugar));
        pairs.add(new MatchPair("The pore on leaves where gases can enter and leave", R.drawable.stomata_diagram));

        for (MatchPair pair : pairs) {
            correctMap.put(pair.definition, pair.imageResId);
        }
    }

    private void setupGame(LayoutInflater inflater) {
        columnDefinitions.removeAllViews();
        columnImages.removeAllViews();

        // Left column: definitions (shuffled if desired, but specified as stacked)
        List<MatchPair> shuffledDefs = new ArrayList<>(pairs);
        Collections.shuffle(shuffledDefs);
        for (MatchPair pair : shuffledDefs) {
            View defView = inflater.inflate(R.layout.item_match_definition, columnDefinitions, false);
            TextView tv = defView.findViewById(R.id.text_definition);
            tv.setText(pair.definition);
            setupDraggable(defView, pair.definition);
            columnDefinitions.addView(defView);
        }

        // Right column: images shuffled
        List<MatchPair> shuffledImages = new ArrayList<>(pairs);
        Collections.shuffle(shuffledImages);
        for (MatchPair pair : shuffledImages) {
            View imgView = inflater.inflate(R.layout.item_match_image, columnImages, false);
            ImageView iv = imgView.findViewById(R.id.image_match);
            iv.setImageResource(pair.imageResId);
            setupDropTarget(imgView, pair.imageResId);
            columnImages.addView(imgView);
        }
    }

    private void setupDraggable(View view, String definition) {
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                ClipData.Item item = new ClipData.Item(definition);
                ClipData dragData = new ClipData(definition, new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                View.DragShadowBuilder shadow = new View.DragShadowBuilder(v);
                v.startDragAndDrop(dragData, shadow, v, 0);
                return true;
            }
            return false;
        });
    }

    private void setupDropTarget(View targetView, int expectedResId) {
        targetView.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                case DragEvent.ACTION_DROP:
                    String droppedDef = event.getClipData().getItemAt(0).getText().toString();
                    View draggedView = (View) event.getLocalState();
                    
                    if (correctMap.get(droppedDef) == expectedResId) {
                        handleMatch(draggedView, targetView);
                    } else {
                        handleMismatch(draggedView);
                    }
                    return true;
            }
            return true;
        });
    }

    private void handleMatch(View defView, View imgView) {
        SoundManager.getInstance(requireContext()).playPop();
        matchedCount++;
        textCounter.setText(matchedCount + "/6");

        // Lock both
        defView.setOnTouchListener(null);
        imgView.setOnDragListener(null);

        // Draw line
        // We need to get the center coordinates of both views relative to the root frame
        defView.post(() -> {
            int[] defLoc = new int[2];
            int[] imgLoc = new int[2];
            int[] rootLoc = new int[2];
            
            View root = getView();
            if (root == null) return;
            root.getLocationOnScreen(rootLoc);
            defView.getLocationOnScreen(defLoc);
            imgView.getLocationOnScreen(imgLoc);

            float startX = defLoc[0] + defView.getWidth() - rootLoc[0];
            float startY = defLoc[1] + (defView.getHeight() / 2f) - rootLoc[1];
            float endX = imgLoc[0] - rootLoc[0];
            float endY = imgLoc[1] + (imgView.getHeight() / 2f) - rootLoc[1];

            lineOverlay.addLine(startX, startY, endX, endY);
        });

        if (matchedCount == 6) {
            Snackbar.make(requireView(), "Well done! 🎉", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Finish", v -> Navigation.findNavController(requireView()).navigateUp())
                    .show();
        }
    }

    private void handleMismatch(View view) {
        ObjectAnimator shake = ObjectAnimator.ofFloat(view, "translationX", 0, 10, -10, 10, -10, 5, -5, 0);
        shake.setDuration(300);
        shake.start();
    }
}
