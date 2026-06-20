package com.example.pocketguru.levels;

import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.drawable.Drawable;
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

import java.util.HashMap;
import java.util.Map;

public class LevelFiveFragment extends Fragment {

    private final Map<Integer, String> slotContents = new HashMap<>();
    private final Map<String, Integer> itemDrawables = new HashMap<>();
    private final Map<Integer, View> originalViews = new HashMap<>();
    
    private Button btnComplete;
    private KeywordTooltipHelper tooltipHelper;
    private ImageView[] slotImages;
    private View[] slots;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level_five, container, false);

        tooltipHelper = new KeywordTooltipHelper(requireContext());
        btnComplete = view.findViewById(R.id.btn_complete);

        initMaps();
        setupKeywords(view.findViewById(R.id.text_body));

        slotImages = new ImageView[]{
                view.findViewById(R.id.img_slot1),
                view.findViewById(R.id.img_slot2),
                view.findViewById(R.id.img_slot3),
                view.findViewById(R.id.img_slot4)
        };

        slots = new View[]{
                view.findViewById(R.id.slot1),
                view.findViewById(R.id.slot2),
                view.findViewById(R.id.slot3),
                view.findViewById(R.id.slot4)
        };

        setupDraggable(view.findViewById(R.id.item_co2), "CO2");
        setupDraggable(view.findViewById(R.id.item_water), "Water");
        setupDraggable(view.findViewById(R.id.item_sun), "Sunlight");
        setupDraggable(view.findViewById(R.id.item_oxygen), "Oxygen");
        setupDraggable(view.findViewById(R.id.item_sugar), "Sugar");
        setupDraggable(view.findViewById(R.id.item_soil), "Soil");

        for (int i = 0; i < slots.length; i++) {
            setupDropTarget(slots[i], i);
        }

        view.findViewById(R.id.btn_back).setOnClickListener(v -> requireActivity().onBackPressed());
        btnComplete.setOnClickListener(v -> handleLevelComplete());

        return view;
    }

    private void initMaps() {
        itemDrawables.put("CO2", R.drawable.item_co2);
        itemDrawables.put("Water", R.drawable.item_water);
        itemDrawables.put("Sunlight", R.drawable.item_sun);
        itemDrawables.put("Oxygen", R.drawable.item_oxygen);
        itemDrawables.put("Sugar", R.drawable.item_sugar);
        itemDrawables.put("Soil", R.drawable.item_soil);
    }

    private void setupKeywords(TextView textView) {
        String text = "Plants convert carbon dioxide and water into food and oxygen in the presence of light energy, and this process is called Photosynthesis!";
        String kw = "Photosynthesis";
        String def = "The process by which plants capture sunlight and use it to make their own food from carbon dioxide and water.";

        textView.setText(SpannableHelper.makeKeywordSpan(text, kw, v -> tooltipHelper.show(v, kw, def)));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setupDraggable(View view, String name) {
        originalViews.put(name.hashCode(), view);
        view.setOnLongClickListener(v -> {
            ClipData.Item item = new ClipData.Item(name);
            ClipData dragData = new ClipData(name, new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
            View.DragShadowBuilder shadow = new View.DragShadowBuilder(v);
            v.startDragAndDrop(dragData, shadow, v, 0);
            return true;
        });
    }

    private void setupDropTarget(View slot, int index) {
        slot.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                case DragEvent.ACTION_DROP:
                    String itemName = event.getClipData().getItemAt(0).getText().toString();
                    handleDrop(index, itemName);
                    return true;
            }
            return true;
        });
    }

    private void handleDrop(int slotIndex, String itemName) {
        // If slot already has an item, return previous to grid
        if (slotContents.containsKey(slotIndex)) {
            String previousItem = slotContents.get(slotIndex);
            View prevView = originalViews.get(previousItem.hashCode());
            if (prevView != null) prevView.setVisibility(View.VISIBLE);
        }

        // Update slot
        slotContents.put(slotIndex, itemName);
        slotImages[slotIndex].setImageResource(itemDrawables.get(itemName));
        
        // Hide from grid
        View currentView = originalViews.get(itemName.hashCode());
        if (currentView != null) currentView.setVisibility(View.INVISIBLE);

        checkEquation();
    }

    private void checkEquation() {
        if (slotContents.size() < 4) return;

        // Correct order: CO2, Water, Sugar, Oxygen (or Water, CO2, Sugar, Oxygen)
        // Let's assume specific order for the check
        String s1 = slotContents.get(0);
        String s2 = slotContents.get(1);
        String s3 = slotContents.get(2);
        String s4 = slotContents.get(3);

        boolean reactantsCorrect = (s1.equals("CO2") && s2.equals("Water")) || (s1.equals("Water") && s2.equals("CO2"));
        boolean productsCorrect = (s3.equals("Sugar") && s4.equals("Oxygen"));

        if (reactantsCorrect && productsCorrect) {
            btnComplete.setEnabled(true);
            Toast.makeText(getContext(), "Magic! The equation is balanced.", Toast.LENGTH_SHORT).show();
            // Brief success indicator could be added here
        } else {
            btnComplete.setEnabled(false);
            // Shake logic for wrong slots could be implemented here
        }
    }

    private void handleLevelComplete() {
        LevelProgressManager.completeLevel(requireContext(), 5, () -> {
            if (isAdded()) {
                Bundle args = new Bundle();
                args.putString(LevelCompleteFragment.ARG_CHAPTER_NAME, "Photosynthesis");
                args.putString(LevelCompleteFragment.ARG_LEVEL_NAME, "Level 5: Let the magic begin!");
                Navigation.findNavController(requireView()).navigate(R.id.LevelCompleteFragment, args);
            }
        }, () -> Toast.makeText(getContext(), "Failed to save progress", Toast.LENGTH_SHORT).show());
    }
}
