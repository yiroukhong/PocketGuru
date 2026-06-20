package com.example.pocketguru.keywords;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketguru.R;
import com.example.pocketguru.models.KeywordItem;
import com.example.pocketguru.supabase.SupabaseClient;
import com.example.pocketguru.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class KeywordsListFragment extends Fragment implements KeywordsAdapter.OnKeywordDeleteListener {

    private RecyclerView recyclerKeywords;
    private KeywordsAdapter adapter;
    private TextView textEmptyState;
    private ProgressBar progressLoading;
    private TextToSpeech tts;
    private final List<KeywordItem> keywordList = new ArrayList<>();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keywords_list, container, false);

        recyclerKeywords = view.findViewById(R.id.recycler_keywords);
        textEmptyState = view.findViewById(R.id.text_empty_state);
        progressLoading = view.findViewById(R.id.progress_loading);

        view.findViewById(R.id.btn_close).setOnClickListener(v -> 
            Navigation.findNavController(v).popBackStack(R.id.LevelMapFragment, false)
        );

        setupTTS();
        setupRecyclerView();
        loadKeywords();

        return view;
    }

    private void setupTTS() {
        tts = new TextToSpeech(requireContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.ENGLISH);
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new KeywordsAdapter(keywordList, tts, this);
        recyclerKeywords.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerKeywords.setAdapter(adapter);
    }

    private void loadKeywords() {
        progressLoading.setVisibility(View.VISIBLE);
        String userId = new SessionManager(requireContext()).getUserId();

        SupabaseClient.getInstance().performQuery("keywords", userId, new SupabaseClient.DatabaseCallback() {
            @Override
            public void onSuccess(Object data) {
                mainHandler.post(() -> {
                    progressLoading.setVisibility(View.GONE);
                    // In a real app, 'data' would be the List<KeywordItem>
                    // For this scaffold, we check the list size
                    updateUI();
                });
            }

            @Override
            public void onError(String message) {
                mainHandler.post(() -> {
                    progressLoading.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void updateUI() {
        if (keywordList.isEmpty()) {
            recyclerKeywords.setVisibility(View.GONE);
            textEmptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerKeywords.setVisibility(View.VISIBLE);
            textEmptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDelete(String keywordId, int position) {
        // Implementation for Supabase delete
        SupabaseClient.getInstance().getExecutor().execute(() -> {
            // Real implementation: supabase.from("keywords").delete().filter("id", EQ, keywordId)
            try {
                Thread.sleep(500); // Simulate delete delay
                mainHandler.post(() -> {
                    adapter.removeItem(position);
                    updateUI();
                    Toast.makeText(requireContext(), "Keyword deleted", Toast.LENGTH_SHORT).show();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
