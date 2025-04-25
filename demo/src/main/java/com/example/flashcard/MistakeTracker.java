package com.example.flashcard;

import java.io.*;
import java.util.*;

public class MistakeTracker {
    private Map<String, List<MistakeEntry>> mistakesByQuestion;
    private String statsFilePath;

    public MistakeTracker(String statsFile) {
        this.statsFilePath = statsFile;
        this.mistakesByQuestion = new HashMap<>();
        loadMistakes();
    }

    public void recordMistake(Card card) {
        if (!card.wasAnsweredCorrectlyLastTime() && card.getTotalAttempts() > 0) {
            String question = card.getQuestion();
            MistakeEntry entry = new MistakeEntry(
                    question, card.getAnswer(), System.currentTimeMillis());

            if (!mistakesByQuestion.containsKey(question)) {
                mistakesByQuestion.put(question, new ArrayList<>());
            }

            mistakesByQuestion.get(question).add(entry);
            saveMistakes();
        }
    }

    public List<MistakeEntry> getRecentMistakes(int limit) {
        List<MistakeEntry> allMistakes = new ArrayList<>();

        for (List<MistakeEntry> entries : mistakesByQuestion.values()) {
            allMistakes.addAll(entries);
        }

        allMistakes.sort((e1, e2) -> Long.compare(e2.getTimestamp(), e1.getTimestamp()));

        return allMistakes.size() <= limit ? allMistakes : allMistakes.subList(0, limit);
    }

    public List<Map.Entry<String, Integer>> getMostFrequentMistakes(int limit) {
        Map<String, Integer> mistakeCounts = new HashMap<>();

        for (Map.Entry<String, List<MistakeEntry>> entry : mistakesByQuestion.entrySet()) {
            mistakeCounts.put(entry.getKey(), entry.getValue().size());
        }

        List<Map.Entry<String, Integer>> sortedMistakes = new ArrayList<>(mistakeCounts.entrySet());
        sortedMistakes.sort((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()));

        return sortedMistakes.size() <= limit ? sortedMistakes : sortedMistakes.subList(0, limit);
    }

    private void saveMistakes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(statsFilePath))) {
            oos.writeObject(mistakesByQuestion);
        } catch (IOException e) {
            System.err.println("Error saving mistake data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadMistakes() {
        File file = new File(statsFilePath);

        if (!file.exists()) {
            saveMistakes();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(statsFilePath))) {
            mistakesByQuestion = (Map<String, List<MistakeEntry>>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading mistake data: " + e.getMessage());
            mistakesByQuestion = new HashMap<>();
        }
    }

    public static class MistakeEntry implements Serializable {
        private static final long serialVersionUID = 1L;

        private String question;
        private String answer;
        private long timestamp;

        public MistakeEntry(String question, String answer, long timestamp) {
            this.question = question;
            this.answer = answer;
            this.timestamp = timestamp;
        }

        public String getQuestion() {
            return question;
        }

        public String getAnswer() {
            return answer;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getFormattedDate() {
            return new Date(timestamp).toString();
        }
    }
}