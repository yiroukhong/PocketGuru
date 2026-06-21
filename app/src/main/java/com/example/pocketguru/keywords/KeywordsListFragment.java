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
import com.example.pocketguru.supabase.SupabaseManager;
import com.example.pocketguru.utils.DataPreloader;
import com.example.pocketguru.utils.SessionManager;

import kotlin.Unit;

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

        setupRecyclerView(); // creates adapter and attaches it immediately
        loadKeywords();      // fetches data immediately, doesn't wait for TTS
        setupTTS();          // TTS initializes in background, updates adapter when ready

        return view;
    }

    private void setupTTS() {
        tts = new TextToSpeech(requireContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.ENGLISH);
            }
            // Update adapter with TTS regardless of success/fail
            if (adapter != null) {
                adapter.setTts(status == TextToSpeech.SUCCESS ? tts : null);
            }
        });
    }



    private void setupRecyclerView() {
        recyclerKeywords.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Create adapter immediately with null TTS — attach it right away
        adapter = new KeywordsAdapter(keywordList, null, this);
        recyclerKeywords.setAdapter(adapter);
    }

    private void loadKeywords() {
        List<KeywordItem> cached = DataPreloader.getCachedKeywords();
        if (cached != null) {
            keywordList.clear();
            keywordList.addAll(cached);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            progressLoading.setVisibility(View.GONE);
            updateUI();
        } else {
            fetchKeywordsFromSupabase();
        }
    }

    private void fetchKeywordsFromSupabase() {
        progressLoading.setVisibility(View.VISIBLE);
        String userId = new SessionManager(requireContext()).getUserId();

        if (userId == null) {
            progressLoading.setVisibility(View.GONE);
            updateUI();
            return;
        }

        SupabaseManager.INSTANCE.getKeywords(userId, new SupabaseManager.SupabaseCallback<List<KeywordItem>>() {
            @Override
            public void onSuccess(List<KeywordItem> result) {
                keywordList.clear();
                keywordList.addAll(result);
                DataPreloader.setCachedKeywords(result);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                progressLoading.setVisibility(View.GONE);
                updateUI();
            }

            @Override
            public void onError(String error) {
                progressLoading.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
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

    public void onKeywordAdded(String word, String definition) {
        // This is called when a keyword is saved from a tooltip while this fragment is potentially visible
        // Since we don't have the ID yet (it's generated by DB), we might need to reload or just add a temp one
        // For simplicity, let's just trigger a reload to get the latest from Supabase
        loadKeywords();
    }

    @Override
    public void onDelete(String keywordId, int position) {
        SupabaseManager.INSTANCE.deleteKeyword(keywordId, new SupabaseManager.SupabaseCallback<Unit>() {
            @Override
            public void onSuccess(Unit result) {
                DataPreloader.setCachedKeywords(null); // Invalidate cache
                adapter.removeItem(position);
                updateUI();
                Toast.makeText(requireContext(), "Keyword deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), "Delete failed: " + error, Toast.LENGTH_SHORT).show();
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
