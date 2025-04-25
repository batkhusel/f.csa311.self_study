package com.example.flashcard;

import java.io.Serializable;

public class Achievement implements Serializable {

    private String name;
    private String description;
    private boolean earned;

    public Achievement(String name, String description) {
        this.name = name;
        this.description = description;
        this.earned = false;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEarned() {
        return earned;
    }

    public void setEarned() {
        this.earned = true;
    }

    @Override
    public String toString() {
        return name + ": " + description + (earned ? " (EARNED)" : "");
    }
}