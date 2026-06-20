package com.example.pocketguru.levels;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pocketguru.R;

public class Level6Page1Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level6_page1, container, false);

        ImageView imgGrowth = view.findViewById(R.id.image_growth);
        
        // Simple scale animation to simulate growth
        imgGrowth.setScaleX(0.5f);
        imgGrowth.setScaleY(0.5f);
        imgGrowth.animate().scaleX(1.2f).scaleY(1.2f).setDuration(2000).start();

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
