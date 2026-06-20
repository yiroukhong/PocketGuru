package com.example.pocketguru.hub;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pocketguru.R;

public class VideoPlayerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_player, container, false);

        VideoView videoView = view.findViewById(R.id.video_view);
        View placeholder = view.findViewById(R.id.text_placeholder);

        // Check if video exists
        int videoResId = getResources().getIdentifier("photosynthesis_video", "raw", requireContext().getPackageName());
        
        if (videoResId != 0) {
            String videoPath = "android.resource://" + requireContext().getPackageName() + "/" + videoResId;
            videoView.setVideoURI(Uri.parse(videoPath));
            
            MediaController mediaController = new MediaController(requireContext());
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
            
            videoView.setOnPreparedListener(mp -> videoView.start());
        } else {
            videoView.setVisibility(View.GONE);
            placeholder.setVisibility(View.VISIBLE);
        }

        view.findViewById(R.id.btn_close).setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        return view;
    }
}
