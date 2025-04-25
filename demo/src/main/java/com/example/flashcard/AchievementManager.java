package com.example.flashcard;

import java.util.*;

public class AchievementManager {
    private List<Achievement> achievements;

    public AchievementManager(String achievementsFile) {
        this.achievements = createDefaultAchievements();
    }

    private List<Achievement> createDefaultAchievements() {
        List<Achievement> defaultAchievements = new ArrayList<>();
        defaultAchievements.add(
                new Achievement("SPEED", "Average time for answering a question in a round is less than 5 seconds"));
        defaultAchievements.add(new Achievement("CORRECT", "All cards were answered correctly in the last round"));
        defaultAchievements.add(new Achievement("REPEAT", "Answered a question more than 5 times"));
        defaultAchievements.add(new Achievement("CONFIDENT", "Answered at least 3 times correctly on the same card"));
        return defaultAchievements;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void checkAchievements(Card card, boolean allCorrectThisRound, long totalTime, int totalCards) {

        if (totalTime > 0 && totalCards > 0) {
            double averageTime = totalTime / (1000.0 * totalCards);
            if (averageTime < 5.0) {
                achievements.get(0).setEarned();
            }
        }

        if (allCorrectThisRound) {
            achievements.get(1).setEarned();
        }

        if (card.getTotalAttempts() > 5) {
            achievements.get(2).setEarned();
        }

        if (card.getCorrectAnswers() >= 3) {
            achievements.get(3).setEarned();
        }
    }
}