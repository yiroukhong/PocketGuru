package com.example.pocketguru.models;

import java.io.Serializable;

public class Question implements Serializable {
    public enum QuestionType {
        TEXT_OPTIONS,
        IMAGE_OPTIONS
    }

    private String questionText;
    private QuestionType type;
    private String[] options;
    private int[] imageResIds; // used if type == IMAGE_OPTIONS
    private int[] correctIndexes; // supports multi-select
    private int illustrationResId; // optional center image, 0 if none

    public Question(String questionText, QuestionType type, String[] options, int[] correctIndexes, int illustrationResId) {
        this.questionText = questionText;
        this.type = type;
        this.options = options;
        this.correctIndexes = correctIndexes;
        this.illustrationResId = illustrationResId;
    }

    public Question(String questionText, QuestionType type, String[] options, int[] imageResIds, int[] correctIndexes, int illustrationResId) {
        this.questionText = questionText;
        this.type = type;
        this.options = options;
        this.imageResIds = imageResIds;
        this.correctIndexes = correctIndexes;
        this.illustrationResId = illustrationResId;
    }

    public String getQuestionText() { return questionText; }
    public QuestionType getType() { return type; }
    public String[] getOptions() { return options; }
    public int[] getImageResIds() { return imageResIds; }
    public int[] getCorrectIndexes() { return correctIndexes; }
    public int getIllustrationResId() { return illustrationResId; }

    public boolean isCorrect(int[] userSelectedIndices) {
        if (userSelectedIndices == null || userSelectedIndices.length != correctIndexes.length) {
            return false;
        }
        for (int correctIndex : correctIndexes) {
            boolean found = false;
            for (int userIndex : userSelectedIndices) {
                if (correctIndex == userIndex) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }
}
