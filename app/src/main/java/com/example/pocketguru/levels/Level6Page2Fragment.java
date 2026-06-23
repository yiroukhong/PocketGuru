package com.example.pocketguru.levels;

import android.animation.Animator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.example.pocketguru.R;

public class Level6Page2Fragment extends Fragment {

    private LottieAnimationView lottieFruit;
    private Button btnNext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level6_page2, container, false);

        lottieFruit = view.findViewById(R.id.lottie_fruit);
        btnNext = view.findViewById(R.id.btn_next);

        // Freeze on first frame
        lottieFruit.setProgress(0f);

        view.findViewById(R.id.btn_grow_fruit).setOnClickListener(v -> {
            lottieFruit.playAnimation();
            v.setEnabled(false); // Disable button once animation starts
        });

        lottieFruit.addAnimatorListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isAdded()) {
                    btnNext.setEnabled(true);
                }
            }
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });

        btnNext.setOnClickListener(v -> {
            if (getParentFragment() instanceof LevelSixFragment) {
                View parentView = getParentFragment().getView();
                if (parentView != null) {
                    ViewPager2 viewPager = parentView.findViewById(R.id.viewPager);
                    if (viewPager != null) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    }
                }
            }
        });

        return view;
    }
}
