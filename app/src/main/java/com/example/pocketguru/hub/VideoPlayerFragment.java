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

        // Load from res/raw
        Uri videoUri = Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + R.raw.photosynthesis_video);
        videoView.setVideoURI(videoUri);

        // Add playback controls (play, pause, seek bar)
        MediaController mediaController = new MediaController(requireContext());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Auto play when ready
        videoView.setOnPreparedListener(mp -> videoView.start());

        // Close button
        view.findViewById(R.id.btn_close_video).setOnClickListener(v -> {
            videoView.stopPlayback();
            Navigation.findNavController(v).navigateUp();
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}