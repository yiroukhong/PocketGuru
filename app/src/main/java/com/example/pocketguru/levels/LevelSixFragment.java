package com.example.pocketguru.levels;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pocketguru.R;

public class LevelSixFragment extends Fragment {

    private ViewPager2 viewPager;
    private View[] segments;
    private final int COLOR_UNFILLED = Color.parseColor("#D3D3D3");
    private final int COLOR_FILLED = Color.parseColor("#E8A598");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level_six_host, container, false);

        segments = new View[]{
                view.findViewById(R.id.seg1),
                view.findViewById(R.id.seg2),
                view.findViewById(R.id.seg3),
                view.findViewById(R.id.seg4)
        };

        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new LevelSixPagerAdapter(this));
        viewPager.setUserInputEnabled(false);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateProgressSegments(position);
            }
        });

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current == 0) {
                requireActivity().onBackPressed();
            } else {
                viewPager.setCurrentItem(current - 1, true);
            }
        });

        return view;
    }

    private void updateProgressSegments(int position) {
        for (int i = 0; i < segments.length; i++) {
            if (i <= position) {
                if (i == position) {
                    animateSegmentColor(segments[i], COLOR_UNFILLED, COLOR_FILLED);
                } else {
                    segments[i].setBackgroundColor(COLOR_FILLED);
                }
            } else {
                segments[i].setBackgroundColor(COLOR_UNFILLED);
            }
        }
    }

    private void animateSegmentColor(View segment, int fromColor, int toColor) {
        ValueAnimator animator = ValueAnimator.ofArgb(fromColor, toColor);
        animator.setDuration(300);
        animator.addUpdateListener(animation -> segment.setBackgroundColor((int) animation.getAnimatedValue()));
        animator.start();
    }

    private static class LevelSixPagerAdapter extends FragmentStateAdapter {
        public LevelSixPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new Level6Page1Fragment();
                case 1: return new Level6Page2Fragment();
                case 2: return new Level6Page3Fragment();
                case 3: return new Level6Page4Fragment();
                default: return new Level6Page1Fragment();
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}
