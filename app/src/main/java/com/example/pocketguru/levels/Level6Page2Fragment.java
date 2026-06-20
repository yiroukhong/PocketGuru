package com.example.pocketguru.levels;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pocketguru.R;

public class Level6Page2Fragment extends Fragment {

    private ImageView imgNoFruit, imgWithFruit;
    private Button btnNext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level6_page2, container, false);

        view.findViewById(R.id.indicator1).setBackgroundResource(R.color.salmon_pink);
        view.findViewById(R.id.indicator2).setBackgroundResource(R.color.salmon_pink);

        imgNoFruit = view.findViewById(R.id.img_tree_no_fruit);
        imgWithFruit = view.findViewById(R.id.img_tree_with_fruit);
        btnNext = view.findViewById(R.id.btn_next);

        view.findViewById(R.id.btn_grow_fruit).setOnClickListener(v -> animateFruitGrowth());

        btnNext.setOnClickListener(v -> {
            ViewPager2 viewPager = requireActivity().findViewById(R.id.viewPager);
            if (viewPager != null) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });

        return view;
    }

    private void animateFruitGrowth() {
        ObjectAnimator.ofFloat(imgNoFruit, "alpha", 1f, 0f).setDuration(1000).start();
        ObjectAnimator animator = ObjectAnimator.ofFloat(imgWithFruit, "alpha", 0f, 1f).setDuration(1000);
        animator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                btnNext.setEnabled(true);
            }
        });
        animator.start();
    }
}
