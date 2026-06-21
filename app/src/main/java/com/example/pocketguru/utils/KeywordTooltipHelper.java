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
import com.example.pocketguru.supabase.SupabaseManager;

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
            Toast.makeText(context, "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        SupabaseManager.INSTANCE.saveKeyword(userId, keyword, definition, new SupabaseManager.SupabaseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean inserted) {
                if (inserted) {
                    btnBookmark.setImageResource(R.drawable.ic_bookmark_filled);
                    Toast.makeText(context, "Saved to Keywords List!", Toast.LENGTH_SHORT).show();
                    
                    // Notify KeywordsListFragment if visible
                    notifyKeywordsFragment(keyword, definition);
                } else {
                    Toast.makeText(context, "Already in your Keywords List", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Log.e("PocketGuru", "Save keyword failed: " + error);
                Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void notifyKeywordsFragment(String word, String definition) {
        if (context instanceof AppCompatActivity) {
            FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
            Fragment navHostFragment = fm.findFragmentById(R.id.nav_host_fragment);
            if (navHostFragment != null) {
                List<Fragment> fragments = navHostFragment.getChildFragmentManager().getFragments();
                for (Fragment f : fragments) {
                    if (f instanceof KeywordsListFragment && f.isVisible()) {
                        ((KeywordsListFragment) f).onKeywordAdded(word, definition);
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
