package com.example.pocketguru.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;

import androidx.annotation.NonNull;

public class SpannableHelper {

    public static SpannableString makeKeywordSpan(String fullText, String keyword, View.OnClickListener onClick) {
        SpannableString spannableString = new SpannableString(fullText);
        int start = fullText.indexOf(keyword);
        if (start != -1) {
            int end = start + keyword.length();
            
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    if (onClick != null) {
                        onClick.onClick(widget);
                    }
                }
            };

            spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }
}
