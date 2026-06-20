package com.example.pocketguru.levels;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pocketguru.R;
import com.example.pocketguru.utils.KeywordTooltipHelper;
import com.example.pocketguru.utils.LevelProgressManager;
import com.example.pocketguru.utils.SpannableHelper;

public class Level1Page4Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level1_page4, container, false);

        // Update progress viewer
        View i1 = view.findViewById(R.id.indicator1);
        View i2 = view.findViewById(R.id.indicator2);
        View i3 = view.findViewById(R.id.indicator3);
        View i4 = view.findViewById(R.id.indicator4);
        if (i1 != null) i1.setBackgroundResource(R.color.salmon_pink);
        if (i2 != null) i2.setBackgroundResource(R.color.salmon_pink);
        if (i3 != null) i3.setBackgroundResource(R.color.salmon_pink);
        if (i4 != null) i4.setBackgroundResource(R.color.salmon_pink);

        TextView textBodyTop = view.findViewById(R.id.text_body_top);
        String topText = "But plants can, they obtain energy through a process called Photosynthesis";
        
        KeywordTooltipHelper tooltipHelper = new KeywordTooltipHelper(requireContext());
        
        textBodyTop.setText(SpannableHelper.makeKeywordSpan(topText, "Photosynthesis", v -> {
            tooltipHelper.show(v, "Photosynthesis", "The process by which green plants and some other organisms use sunlight to synthesize foods with the help of chlorophyll.");
        }));
        textBodyTop.setMovementMethod(LinkMovementMethod.getInstance());

        view.findViewById(R.id.btn_complete).setOnClickListener(v -> {
            LevelProgressManager.completeLevel(requireContext(), 1, () -> {
                if (isAdded()) {
                    Navigation.findNavController(v).navigateUp();
                }
            }, () -> {
                // error
            });
        });

        return view;
    }
}
