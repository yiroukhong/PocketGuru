package com.example.pocketguru.levels;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pocketguru.R;

public class Level1Page3Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level1_page3, container, false);

        // Update progress viewer
        View i1 = view.findViewById(R.id.indicator1);
        View i2 = view.findViewById(R.id.indicator2);
        View i3 = view.findViewById(R.id.indicator3);
        if (i1 != null) i1.setBackgroundResource(R.color.salmon_pink);
        if (i2 != null) i2.setBackgroundResource(R.color.salmon_pink);
        if (i3 != null) i3.setBackgroundResource(R.color.salmon_pink);

        view.findViewById(R.id.btn_next).setOnClickListener(v -> {
            if (getParentFragment() instanceof LevelOneFragment) {
                View parentView = getParentFragment().getView();
                if (parentView != null) {
                    ViewPager2 viewPager = parentView.findViewById(R.id.viewPager);
                    if (viewPager != null) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    }
                }
            }
        });

        return view;
    }
}
