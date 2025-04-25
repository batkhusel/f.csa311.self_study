package com.example.flashcard;

import java.util.ArrayList;
import java.util.List;

public class RecentMistakesFirstSorter implements CardOrganizer {

    @Override
    public List<Card> organize(List<Card> cards) {
        List<Card> incorrectCards = new ArrayList<>();
        List<Card> correctCards = new ArrayList<>();

        for (Card card : cards) {
            if (!card.wasAnsweredCorrectlyLastTime()) {
                incorrectCards.add(card);
            } else {
                correctCards.add(card);
            }
        }

        List<Card> result = new ArrayList<>(incorrectCards);
        result.addAll(correctCards);

        return result;
    }
}