package com.example.flashcard;

import java.io.Serializable;

public class Achievement implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private boolean earned;

    /**
     * 
     * @param name        The name of the achievement
     * @param description The description of the achievement
     */
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