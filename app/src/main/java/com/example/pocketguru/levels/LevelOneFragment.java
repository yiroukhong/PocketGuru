package com.example.pocketguru.levels;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pocketguru.R;

public class LevelOneFragment extends Fragment {

    private ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level_one, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new LevelOnePagerAdapter(this));
        // Disable swiping to force using the Next buttons
        viewPager.setUserInputEnabled(false);

        return view;
    }

    private static class LevelOnePagerAdapter extends FragmentStateAdapter {
        public LevelOnePagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new Level1Page1Fragment();
                case 1: return new Level1Page2Fragment();
                case 2: return new Level1Page3Fragment();
                case 3: return new Level1Page4Fragment();
                default: return new Level1Page1Fragment();
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}
