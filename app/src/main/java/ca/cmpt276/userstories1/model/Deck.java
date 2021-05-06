package ca.cmpt276.userstories1.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 *  Class Deck
 *  Helps with the game
 *  Creates 7 cards
 *  Shuffles the cards
 */

public class Deck {
    private List <List<Integer>> deck;
    // using List datatype because a Array is not resizeable

    public Deck(int cardOrder, int totalCards) {
        this.generateDeckOrder(cardOrder, totalCards);
    }

    // Checks if deck is empty, if empty then game is done
    public boolean isEmpty(){
        return deck.isEmpty();
    }

    // Return the top "card" from the top of the deck
    public List < Integer > getTopCard() {
        return deck.get(0);
    }

    // Returns the top "card" from the top of the deck and removes it from the array
    public List < Integer > popTopCard() {
        List < Integer > popped = deck.get(0);
        deck.remove(0);
        return popped;
    }


    private void generateDeckOrder(int cardOrder, int totalCards) {

        this.deck = new ArrayList<>();

        switch (cardOrder) {
            case 3:
                deck.add(Arrays.asList(0, 1, 4));
                deck.add(Arrays.asList(2, 3, 4));
                deck.add(Arrays.asList(0, 2, 5));
                deck.add(Arrays.asList(1, 3, 5));
                deck.add(Arrays.asList(0, 3, 6));
                deck.add(Arrays.asList(1, 2, 6));
                deck.add(Arrays.asList(4, 5, 6));
                break;
            case 4:
                deck.add(Arrays.asList(0, 1, 2, 9));
                deck.add(Arrays.asList(9, 3, 4, 5));
                deck.add(Arrays.asList(8, 9, 6, 7));
                deck.add(Arrays.asList(0, 10, 3, 6));
                deck.add(Arrays.asList(1, 10, 4, 7));
                deck.add(Arrays.asList(8, 2, 10, 5));
                deck.add(Arrays.asList(0, 8, 11, 4));
                deck.add(Arrays.asList(1, 11, 5, 6));
                deck.add(Arrays.asList(11, 2, 3, 7));
                deck.add(Arrays.asList(0, 12, 5, 7));
                deck.add(Arrays.asList(8, 1, 3, 12));
                deck.add(Arrays.asList(12, 2, 4, 6));
                deck.add(Arrays.asList(9, 10, 11, 12));
                break;
            case 6:
                deck.add(Arrays.asList(0, 1, 2, 3, 4, 25));
                deck.add(Arrays.asList(5, 6, 7, 8, 9, 25));
                deck.add(Arrays.asList(10, 11, 12, 13, 14, 25));
                deck.add(Arrays.asList(15, 16, 17, 18, 19, 25));
                deck.add(Arrays.asList(20, 21, 22, 23, 24, 25));
                deck.add(Arrays.asList(0, 5, 10, 15, 20, 26));
                deck.add(Arrays.asList(1, 6, 11, 16, 21, 26));
                deck.add(Arrays.asList(2, 7, 12, 17, 22, 26));
                deck.add(Arrays.asList(3, 8, 13, 18, 23, 26));
                deck.add(Arrays.asList(4, 9, 14, 19, 24, 26));
                deck.add(Arrays.asList(0, 6, 12, 18, 24, 27));
                deck.add(Arrays.asList(1, 7, 13, 19, 20, 27));
                deck.add(Arrays.asList(2, 8, 14, 15, 21, 27));
                deck.add(Arrays.asList(3, 9, 10, 16, 22, 27));
                deck.add(Arrays.asList(4, 5, 11, 17, 23, 27));
                deck.add(Arrays.asList(0, 7, 14, 16, 23, 28));
                deck.add(Arrays.asList(1, 8, 10, 17, 24, 28));
                deck.add(Arrays.asList(2, 9, 11, 18, 20, 28));
                deck.add(Arrays.asList(3, 5, 12, 19, 21, 28));
                deck.add(Arrays.asList(4, 6, 13, 15, 22, 28));
                deck.add(Arrays.asList(0, 8, 11, 19, 22, 29));
                deck.add(Arrays.asList(1, 9, 12, 15, 23, 29));
                deck.add(Arrays.asList(2, 5, 13, 16, 24, 29));
                deck.add(Arrays.asList(3, 6, 14, 17, 20, 29));
                deck.add(Arrays.asList(4, 7, 10, 18, 21, 29));
                deck.add(Arrays.asList(0, 9, 13, 17, 21, 30));
                deck.add(Arrays.asList(1, 5, 14, 18, 22, 30));
                deck.add(Arrays.asList(2, 6, 10, 19, 23, 30));
                deck.add(Arrays.asList(3, 7, 11, 15, 24, 30));
                deck.add(Arrays.asList(4, 8, 12, 16, 20, 30));
                deck.add(Arrays.asList(25, 26, 27, 28, 29, 30));
        }

        // 0 means all cards
        if (totalCards != 0){
            deck = deck.subList(0, totalCards + 1);
        }

        Collections.shuffle(deck);
    }

    public int returnDeckSize() {
        return deck.size();
    }
}