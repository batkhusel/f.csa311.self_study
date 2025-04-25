package com.example.flashcard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WorstFirstSorter implements CardOrganizer {

    @Override
    public List<Card> organize(List<Card> cards) {
        List<Card> sortedCards = new ArrayList<>(cards);

        sortedCards.sort(new Comparator<Card>() {
            @Override
            public int compare(Card c1, Card c2) {
                double ratio1 = c1.getTotalAttempts() == 0 ? 0
                        : (double) c1.getCorrectAnswers() / c1.getTotalAttempts();
                double ratio2 = c2.getTotalAttempts() == 0 ? 0
                        : (double) c2.getCorrectAnswers() / c2.getTotalAttempts();

                if (c1.getTotalAttempts() == 0 && c2.getTotalAttempts() > 0) {
                    return -1;
                } else if (c1.getTotalAttempts() > 0 && c2.getTotalAttempts() == 0) {
                    return 1;
                }

                return Double.compare(ratio1, ratio2);
            }
        });

        return sortedCards;
    }
}