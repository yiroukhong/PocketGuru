package com.example.pocketguru.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.pocketguru.R;
import com.example.pocketguru.keywords.KeywordsListFragment;
import com.example.pocketguru.models.KeywordItem;
import com.example.pocketguru.supabase.SupabaseManager;
import com.example.pocketguru.utils.DataPreloader;

import java.util.List;
import java.util.Locale;

public class KeywordTooltipHelper {

    private final Context context;
    private TextToSpeech tts;
    private PopupWindow popupWindow;

    public KeywordTooltipHelper(Context context) {
        this.context = context;
    }

    public void show(View anchor, String keyword, String definition) {
        View popupView = LayoutInflater.from(context).inflate(R.layout.layout_keyword_tooltip, null);

        TextView textName = popupView.findViewById(R.id.text_keyword_name);
        TextView textDefinition = popupView.findViewById(R.id.text_keyword_definition);
        ImageView btnSpeak = popupView.findViewById(R.id.btn_speak);
        ImageView btnBookmark = popupView.findViewById(R.id.btn_bookmark);

        textName.setText(keyword);
        textDefinition.setText(definition);

        btnSpeak.setOnClickListener(v -> speak(keyword, btnSpeak));
        
        btnBookmark.setOnClickListener(v -> saveKeyword(keyword, definition, btnBookmark));

        // Check cache first for instant icon state
        List<KeywordItem> cached = DataPreloader.getCachedKeywords();
        boolean alreadySaved = false;
        if (cached != null) {
            for (KeywordItem item : cached) {
                if (item.getWord().equalsIgnoreCase(keyword)) {
                    alreadySaved = true;
                    break;
                }
            }
        }

        // Set initial icon state immediately without network call
        btnBookmark.setImageResource(alreadySaved ?
                R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark);

        // If not in cache, verify against Supabase in background
        if (!alreadySaved) {
            String userId = new SessionManager(context).getUserId();
            if (userId != null) {
                SupabaseManager.INSTANCE.checkKeywordExists(userId, keyword,
                        new SupabaseManager.SupabaseCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean exists) {
                                if (exists) {
                                    btnBookmark.setImageResource(R.drawable.ic_bookmark_filled);
                                }
                            }

                            @Override
                            public void onError(String error) { /* keep outline icon */ }
                        });
            }
        }

        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);

        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupHeight = popupView.getMeasuredHeight();
        
        popupWindow.showAsDropDown(anchor, 0, -popupHeight - anchor.getHeight());
        
        popupWindow.setOnDismissListener(() -> {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
                tts = null;
            }
        });
    }

    private void saveKeyword(String keyword, String definition, ImageView btnBookmark) {
        Log.d("PocketGuru", "Attempting to save keyword: " + keyword);
        String userId = new SessionManager(context).getUserId();
        
        if (userId == null) {
            Log.e("PocketGuru", "Save keyword failed: userId is null");
            ToastHelper.show(context,"You are not logged in yet!", ToastHelper.ToastType.ERROR);
            return;
        }

        SupabaseManager.INSTANCE.saveKeyword(userId, keyword, definition, new SupabaseManager.SupabaseCallback<KeywordItem>() {
            @Override
            public void onSuccess(KeywordItem newItem) {
                if (newItem != null) {
                    btnBookmark.setImageResource(R.drawable.ic_bookmark_filled);
                    ToastHelper.show(context,"Keyword saved!", ToastHelper.ToastType.SUCCESS);

                    // Update cache immediately so other screens are in sync
                    List<KeywordItem> cached = DataPreloader.getCachedKeywords();
                    if (cached != null) {
                        cached.add(0, newItem);
                    } else {
                        // If no cache exists, create one with the new item
                        List<KeywordItem> newList = new java.util.ArrayList<>();
                        newList.add(newItem);
                        DataPreloader.setCachedKeywords(newList);
                    }
                    
                    // Notify KeywordsListFragment if visible
                    notifyKeywordsFragment(newItem);
                } else {
                    ToastHelper.show(context,"Already saved!", ToastHelper.ToastType.INFO);
                }
            }

            @Override
            public void onError(String error) {
                Log.e("PocketGuru", "Save keyword failed: " + error);
                ToastHelper.show(context,"Couldn't save keyword", ToastHelper.ToastType.ERROR);
            }
        });
    }

    private void notifyKeywordsFragment(KeywordItem item) {
        if (context instanceof AppCompatActivity) {
            FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
            Fragment navHostFragment = fm.findFragmentById(R.id.nav_host_fragment);
            if (navHostFragment != null) {
                List<Fragment> fragments = navHostFragment.getChildFragmentManager().getFragments();
                for (Fragment f : fragments) {
                    if (f instanceof KeywordsListFragment && f.isVisible()) {
                        ((KeywordsListFragment) f).onKeywordAdded(item);
                    }
                }
            }
        }
    }

    private void speak(String text, ImageView btnSpeak) {
        if (tts == null) {
            tts = new TextToSpeech(context, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.US);
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            // Tint speaker icon yellow while speaking
                            if (btnSpeak != null) {
                                btnSpeak.post(() ->
                                        btnSpeak.setColorFilter(
                                                android.graphics.Color.parseColor("#FFD93D"),
                                                android.graphics.PorterDuff.Mode.SRC_IN));
                            }
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            // Reset speaker icon color when done
                            if (btnSpeak != null) {
                                btnSpeak.post(() -> btnSpeak.clearColorFilter());
                            }
                        }

                        @Override
                        public void onError(String utteranceId) {
                            if (btnSpeak != null) {
                                btnSpeak.post(() -> btnSpeak.clearColorFilter());
                            }
                        }
                    });

                    if (tts.isSpeaking()) {
                        tts.stop();
                    }
                    android.os.Bundle params = new android.os.Bundle();
                    params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "keyword");
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "keyword");
                }
            });
        } else {
            if (tts.isSpeaking()) {
                tts.stop();
            }
            android.os.Bundle params = new android.os.Bundle();
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "keyword");
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "keyword");
        }
    }
}
