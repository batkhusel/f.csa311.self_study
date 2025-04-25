package com.example.flashcard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomSorter implements CardOrganizer {

    @Override
    public List<Card> organize(List<Card> cards) {
        List<Card> shuffledCards = new ArrayList<>(cards);
        Collections.shuffle(shuffledCards);
        return shuffledCards;
    }
}