package com.example.pocketguru.keywords;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketguru.R;
import com.example.pocketguru.models.KeywordItem;

import java.util.List;

public class KeywordsAdapter extends RecyclerView.Adapter<KeywordsAdapter.KeywordViewHolder> {

    public interface OnKeywordDeleteListener {
        void onDelete(String keywordId, int position);
    }

    private final List<KeywordItem> keywordList;
    private final TextToSpeech tts;
    private final OnKeywordDeleteListener deleteListener;

    public KeywordsAdapter(List<KeywordItem> keywordList, TextToSpeech tts, OnKeywordDeleteListener deleteListener) {
        this.keywordList = keywordList;
        this.tts = tts;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public KeywordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_keyword, parent, false);
        return new KeywordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeywordViewHolder holder, int position) {
        KeywordItem item = keywordList.get(position);
        holder.textWord.setText(item.getWord());
        holder.textDefinition.setText(item.getDefinition());

        holder.btnSpeaker.setOnClickListener(v -> {
            if (tts != null) {
                tts.speak(item.getDefinition(), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(item.getId(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return keywordList.size();
    }

    public void removeItem(int position) {
        keywordList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, keywordList.size());
    }

    static class KeywordViewHolder extends RecyclerView.ViewHolder {
        TextView textWord, textDefinition;
        ImageButton btnSpeaker, btnDelete;

        KeywordViewHolder(@NonNull View itemView) {
            super(itemView);
            textWord = itemView.findViewById(R.id.text_keyword_name);
            textDefinition = itemView.findViewById(R.id.text_keyword_definition);
            btnSpeaker = itemView.findViewById(R.id.btn_speaker);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
