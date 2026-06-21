package com.example.pocketguru.minigames;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketguru.R;
import com.example.pocketguru.models.Question;

import java.util.List;

public class ReviewQuestionAdapter extends RecyclerView.Adapter<ReviewQuestionAdapter.ViewHolder> {
    private List<Question> questions;
    private int[][] userAnswers; // indices selected for each question
    private boolean[] isCorrect;
    
    public ReviewQuestionAdapter(List<Question> questions, int[][] userAnswers, boolean[] isCorrect) {
        this.questions = questions;
        this.userAnswers = userAnswers;
        this.isCorrect = isCorrect;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question q = questions.get(position);
        int[] userAns = userAnswers[position];
        boolean correct = isCorrect[position];

        holder.textQuestionNumber.setText("Q" + (position + 1) + ". " + q.getQuestionText());
        
        if (q.getIllustrationResId() != 0) {
            holder.imageIllustration.setImageResource(q.getIllustrationResId());
            holder.imageIllustration.setVisibility(View.VISIBLE);
        } else {
            holder.imageIllustration.setVisibility(View.GONE);
        }

        TextView[] optionTextViews = {holder.textOption1, holder.textOption2, holder.textOption3, holder.textOption4};
        String[] options = q.getOptions();
        int[] correctIndexes = q.getCorrectIndexes();

        for (int i = 0; i < optionTextViews.length; i++) {
            optionTextViews[i].setText(options[i]);
            
            boolean isCorrectIdx = false;
            for (int c : correctIndexes) if (c == i) isCorrectIdx = true;
            
            boolean wasSelected = false;
            if (userAns != null) {
                for (int s : userAns) if (s == i) wasSelected = true;
            }

            if (isCorrectIdx) {
                applyBorderStyle(optionTextViews[i], "#81C784"); // Green
                optionTextViews[i].setTextColor(Color.parseColor("#81C784"));
            } else if (wasSelected) {
                applyBorderStyle(optionTextViews[i], "#E57373"); // Red
                optionTextViews[i].setTextColor(Color.parseColor("#E57373"));
            } else {
                applyBorderStyle(optionTextViews[i], "#CCCCCC"); // Grey
                optionTextViews[i].setTextColor(Color.parseColor("#999999"));
            }
        }

        if (correct) {
            holder.textResultLabel.setText("✓ Correct!");
            holder.textResultLabel.setTextColor(Color.parseColor("#81C784"));
        } else {
            StringBuilder sb = new StringBuilder("✗ You answered: ");
            if (userAns == null || userAns.length == 0) {
                sb.append("Unanswered");
            } else {
                for (int i = 0; i < userAns.length; i++) {
                    sb.append(options[userAns[i]]);
                    if (i < userAns.length - 1) sb.append(", ");
                }
            }
            holder.textResultLabel.setText(sb.toString());
            holder.textResultLabel.setTextColor(Color.parseColor("#E57373"));
        }
    }

    private void applyBorderStyle(TextView textView, String colorHex) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke(6, Color.parseColor(colorHex)); // Left border simulated by stroke
        // To really simulate a left border, we might need a LayerDrawable or a custom view.
        // For simplicity, let's use a subtle background with stroke.
        drawable.setColor(Color.WHITE);
        textView.setBackground(drawable);
        textView.setPadding(24, 16, 16, 16);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textQuestionNumber, textResultLabel;
        ImageView imageIllustration;
        TextView textOption1, textOption2, textOption3, textOption4;

        ViewHolder(View itemView) {
            super(itemView);
            textQuestionNumber = itemView.findViewById(R.id.text_question_number);
            imageIllustration = itemView.findViewById(R.id.image_review_illustration);
            textOption1 = itemView.findViewById(R.id.text_option1);
            textOption2 = itemView.findViewById(R.id.text_option2);
            textOption3 = itemView.findViewById(R.id.text_option3);
            textOption4 = itemView.findViewById(R.id.text_option4);
            textResultLabel = itemView.findViewById(R.id.text_result_label);
        }
    }
}
