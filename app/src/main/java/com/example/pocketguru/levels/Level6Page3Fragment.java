package com.example.pocketguru.levels;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.example.pocketguru.R;

public class Level6Page3Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level6_page3, container, false);

        LottieAnimationView lottieFruit = view.findViewById(R.id.lottie_fruit_complete);
        // Show final frame (tree with fruit)
        lottieFruit.setProgress(1f);

        view.findViewById(R.id.btn_next).setOnClickListener(v -> {
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
