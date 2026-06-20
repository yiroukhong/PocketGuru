package com.example.pocketguru.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketguru.R;
import com.example.pocketguru.supabase.SupabaseClient;

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

        btnSpeak.setOnClickListener(v -> speak(definition));
        
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
        String userId = new SessionManager(context).getUserId();
        
        SupabaseClient.getInstance().getExecutor().execute(() -> {
            // Real implementation: check if exists, then insert
            // For now, simulate successful save
            try {
                Thread.sleep(300);
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    btnBookmark.setImageResource(R.drawable.ic_bookmark_filled);
                    Toast.makeText(context, "Saved to Keywords List!", Toast.LENGTH_SHORT).show();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void speak(String text) {
        if (tts == null) {
            tts = new TextToSpeech(context, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.US);
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            });
        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }
}
