package com.example.pocketguru.minigames;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pocketguru.R;

import java.util.ArrayList;
import java.util.List;

public class FlashcardsFragment extends Fragment {

    private final List<String[]> cards = new ArrayList<>();
    private int currentIndex = 0;
    private boolean isFlipped = false;
    private boolean currentCardWasFlipped = false;

    private TextView textCounter, textCardContent, textHint;
    private View cardContainer;
    private Button btnNext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flashcards, container, false);

        initCards();
        
        textCounter = view.findViewById(R.id.text_counter);
        textCardContent = view.findViewById(R.id.text_card_content);
        textHint = view.findViewById(R.id.text_hint);
        cardContainer = view.findViewById(R.id.card_container);
        btnNext = view.findViewById(R.id.btn_next);

        updateCardUI();

        cardContainer.setOnClickListener(v -> flipCard());
        
        btnNext.setOnClickListener(v -> {
            if (currentIndex < cards.size() - 1) {
                currentIndex++;
                isFlipped = false;
                currentCardWasFlipped = false;
                updateCardUI();
            } else {
                Navigation.findNavController(v).navigateUp();
            }
        });

        view.findViewById(R.id.btn_close).setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        return view;
    }

    private void initCards() {
        cards.add(new String[]{"Photosynthesis", "The process by which plants capture sunlight and use it to make their own food."});
        cards.add(new String[]{"Chloroplast", "The location inside a plant cell where photosynthesis takes place."});
        cards.add(new String[]{"Chlorophyll", "The green pigment inside chloroplasts that captures light energy for photosynthesis."});
        cards.add(new String[]{"Stomata", "Tiny openings on the underside of leaves that allow carbon dioxide in and oxygen out."});
        cards.add(new String[]{"Xylem", "The water-conducting tissue in plants that transports water from roots to leaves."});
        cards.add(new String[]{"Pigment", "A colored molecule that absorbs specific wavelengths of light."});
        cards.add(new String[]{"Gaseous exchange", "The movement of gases into and out of a plant through the stomata."});
        cards.add(new String[]{"Sugar", "The food produced by plants during photosynthesis, used for energy and stored as starch."});
        cards.add(new String[]{"Energy", "The ability to do work, required by all living things to carry out life processes."});
    }

    private void updateCardUI() {
        textCounter.setText((currentIndex + 1) + "/" + cards.size());
        textCardContent.setText(cards.get(currentIndex)[0]);
        
        textHint.setVisibility(View.VISIBLE);
        
        btnNext.setEnabled(false);
        btnNext.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.progress_grey, null)));
        
        if (currentIndex == cards.size() - 1) {
            btnNext.setText("Done");
        } else {
            btnNext.setText("Next");
        }
    }

    private void flipCard() {
        ObjectAnimator flipOut = ObjectAnimator.ofFloat(cardContainer, "rotationY", 0f, 90f);
        flipOut.setDuration(150);
        flipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isFlipped = !isFlipped;
                currentCardWasFlipped = true;
                
                textCardContent.setText(cards.get(currentIndex)[isFlipped ? 1 : 0]);
                textHint.setVisibility(View.GONE);
                
                enableNextButton();

                ObjectAnimator flipIn = ObjectAnimator.ofFloat(cardContainer, "rotationY", -90f, 0f);
                flipIn.setDuration(150);
                flipIn.start();
            }
        });
        flipOut.start();
    }

    private void enableNextButton() {
        if (currentCardWasFlipped) {
            btnNext.setEnabled(true);
            btnNext.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFD93D")));
        }
    }
}
