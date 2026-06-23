package com.example.pocketguru.models;

import java.io.Serializable;

public class KeywordItem implements Serializable {
    private String id;
    private String word;
    private String definition;
    private String createdAt;

    public KeywordItem(String id, String word, String definition, String createdAt) {
        this.id = id;
        this.word = word;
        this.definition = definition;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getWord() { return word; }
    public String getDefinition() { return definition; }
    public String getCreatedAt() { return createdAt; }
}
