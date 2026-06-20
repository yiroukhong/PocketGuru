package com.example.pocketguru.levels;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
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

public class LevelThreeFragment extends Fragment {

    private int currentState = 0; // 0: Front, 1: Back, 2: Stomata
    private ImageView imageLeafFront, imageLeafBack, imageStomataZoom;
    private TextView textHint;
    private Button btnAction;
    private KeywordTooltipHelper tooltipHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level_three, container, false);

        tooltipHelper = new KeywordTooltipHelper(requireContext());
        imageLeafFront = view.findViewById(R.id.imageLeafFront);
        imageLeafBack = view.findViewById(R.id.imageLeafBack);
        imageStomataZoom = view.findViewById(R.id.imageStomataZoom);
        textHint = view.findViewById(R.id.text_hint);
        btnAction = view.findViewById(R.id.btn_action);

        setupKeywords(view.findViewById(R.id.text_body));

        view.findViewById(R.id.image_container).setOnClickListener(v -> handleImageClick());
        view.findViewById(R.id.btn_back).setOnClickListener(v -> requireActivity().onBackPressed());
        btnAction.setOnClickListener(v -> handleLevelComplete());

        return view;
    }

    private void setupKeywords(TextView textView) {
        String text = "Carbon dioxide is taken in from the air for gaseous exchange through the stomata on the underside of leaves";
        
        // Define keywords and definitions
        String geWord = "gaseous exchange";
        String geDef = "The movement of gases (carbon dioxide and oxygen) into and out of a plant through the stomata.";
        String sWord = "stomata";
        String sDef = "Tiny openings on the underside of leaves that allow gases like carbon dioxide to enter and oxygen to exit the plant.";

        // Apply first keyword
        textView.setText(SpannableHelper.makeKeywordSpan(text, geWord, v -> 
            tooltipHelper.show(v, geWord, geDef)
        ));
        
        // Apply second keyword on top of the first spannable
        String currentText = textView.getText().toString();
        textView.setText(SpannableHelper.makeKeywordSpan(textView.getText().toString(), sWord, v -> 
            tooltipHelper.show(v, sWord, sDef)
        ));
        
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void handleImageClick() {
        if (currentState == 0) {
            animateLeafFlip();
        } else if (currentState == 1) {
            animateZoomToStomata();
        }
    }

    private void animateLeafFlip() {
        ObjectAnimator flipOut = ObjectAnimator.ofFloat(imageLeafFront, "rotationY", 0f, 90f);
        flipOut.setDuration(150);
        flipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                imageLeafFront.setVisibility(View.GONE);
                imageLeafBack.setVisibility(View.VISIBLE);
                ObjectAnimator flipIn = ObjectAnimator.ofFloat(imageLeafBack, "rotationY", -90f, 0f);
                flipIn.setDuration(150);
                flipIn.start();
                
                currentState = 1;
                textHint.setText("Tap again to zoom to stomata");
            }
        });
        flipOut.start();
    }

    private void animateZoomToStomata() {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(imageLeafBack, "alpha", 1f, 0f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(imageStomataZoom, "alpha", 0f, 1f);
        
        fadeOut.setDuration(300);
        fadeIn.setDuration(300);
        
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                imageStomataZoom.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                imageLeafBack.setVisibility(View.GONE);
                currentState = 2;
                textHint.setVisibility(View.GONE);
                btnAction.setVisibility(View.VISIBLE);
            }
        });
        
        fadeOut.start();
        fadeIn.start();
    }

    private void handleLevelComplete() {
        LevelProgressManager.completeLevel(requireContext(), 3, () -> {
            if (isAdded()) {
                Bundle args = new Bundle();
                args.putString(LevelCompleteFragment.ARG_CHAPTER_NAME, "Photosynthesis");
                args.putString(LevelCompleteFragment.ARG_LEVEL_NAME, "Level 3: Get the CO2");
                Navigation.findNavController(requireView()).navigate(R.id.LevelCompleteFragment, args);
            }
        }, () -> {
            Toast.makeText(getContext(), "Failed to save progress", Toast.LENGTH_SHORT).show();
        });
    }
}
