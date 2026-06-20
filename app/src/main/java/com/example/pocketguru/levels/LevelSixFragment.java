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

public class LevelSixFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level_six, container, false);

        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new LevelSixPagerAdapter(this));
        viewPager.setUserInputEnabled(false);

        return view;
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
                default: return new Level6Page1Fragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
