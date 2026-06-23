package com.example.pocketguru.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketguru.R;

public class ToastHelper {

    public enum ToastType {
        SUCCESS,
        ERROR,
        INFO
    }

    public static void show(Context context, String message, ToastType type) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.layout_custom_toast, null);

        TextView textMessage = layout.findViewById(R.id.toast_message);
        ImageView icon = layout.findViewById(R.id.toast_icon);
        View container = layout.findViewById(R.id.toast_container);

        textMessage.setText(message);

        switch (type) {
            case SUCCESS:
                container.setBackgroundResource(R.drawable.toast_bg_success);
                icon.setImageResource(R.drawable.ic_check_circle);
                break;
            case ERROR:
                container.setBackgroundResource(R.drawable.toast_bg_error);
                icon.setImageResource(R.drawable.ic_error_circle);
                break;
            case INFO:
                container.setBackgroundResource(R.drawable.toast_bg_info);
                icon.setImageResource(R.drawable.ic_info_circle);
                break;
        }

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}
