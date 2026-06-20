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

public class Level1Page2Fragment extends Fragment {

    private int growthStage = 0;
    private ImageView imgBaby, imgKid, imgAdult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level1_page2, container, false);

        // Update progress viewer
        view.findViewById(R.id.indicator1).setBackgroundResource(R.color.salmon_pink);
        view.findViewById(R.id.indicator2).setBackgroundResource(R.color.salmon_pink);

        imgBaby = view.findViewById(R.id.img_baby);
        imgKid = view.findViewById(R.id.img_kid);
        imgAdult = view.findViewById(R.id.img_adult);

        view.findViewById(R.id.animation_container).setOnClickListener(v -> handleGrowth());

        view.findViewById(R.id.btn_next).setOnClickListener(v -> {
            ViewPager2 viewPager = requireActivity().findViewById(R.id.viewPager);
            if (viewPager != null) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });

        return view;
    }

    private void handleGrowth() {
        if (growthStage == 0) {
            // Baby to Kid
            ObjectAnimator.ofFloat(imgBaby, "alpha", 1f, 0.2f).setDuration(500).start();
            ObjectAnimator.ofFloat(imgKid, "alpha", 0f, 1f).setDuration(500).start();
            growthStage = 1;
        } else if (growthStage == 1) {
            // Kid to Adult
            ObjectAnimator.ofFloat(imgKid, "alpha", 1f, 0.2f).setDuration(500).start();
            ObjectAnimator.ofFloat(imgAdult, "alpha", 0f, 1f).setDuration(500).start();
            growthStage = 2;
        }
    }
}
