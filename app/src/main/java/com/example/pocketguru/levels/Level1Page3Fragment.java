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
        view.findViewById(R.id.indicator1).setBackgroundResource(R.color.salmon_pink);
        view.findViewById(R.id.indicator2).setBackgroundResource(R.color.salmon_pink);
        view.findViewById(R.id.indicator3).setBackgroundResource(R.color.salmon_pink);

        view.findViewById(R.id.btn_next).setOnClickListener(v -> {
            View parentView = requireParentFragment().getView();
            if (parentView != null) {
                ViewPager2 viewPager = parentView.findViewById(R.id.viewPager);
                if (viewPager != null) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                }
            }
        });

        return view;
    }
}
