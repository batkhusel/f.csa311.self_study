package com.example.flashcard;

public class Card {
    private String question;
    private String answer;
    private int correctAnswers;
    private int totalAttempts;
    private boolean answeredCorrectlyLastTime;
    private long lastAnswerTime;

    public Card(String question, String answer) {
        this.question = question;
        this.answer = answer;
        this.correctAnswers = 0;
        this.totalAttempts = 0;
        this.answeredCorrectlyLastTime = false;
        this.lastAnswerTime = 0;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public void recordAttempt(boolean correct, long time) {
        this.totalAttempts++;
        this.lastAnswerTime = time;
        this.answeredCorrectlyLastTime = correct;
        if (correct) {
            this.correctAnswers++;
        }
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public boolean wasAnsweredCorrectlyLastTime() {
        return answeredCorrectlyLastTime;
    }

    public long getLastAnswerTime() {
        return lastAnswerTime;
    }
}