package com.example.project;

public sealed abstract class Card permits Monster, Potion, Weapon {
    private CardSuit cardSuit;
    private int orderedValue;

    public Card (CardSuit suit, int orderedValue) {
        this.cardSuit = suit;
        this.orderedValue = orderedValue; // values go from 2 to 14
    }

    @Override
    public String toString() {
        String cardRank = switch (this.orderedValue) {
            case 11 -> "Jack";
            case 12 -> "Queen";
            case 13 -> "King";
            case 14 -> "Ace";
            default -> String.valueOf(this.orderedValue);
        };
        
        return cardRank + " of " + this.cardSuit.toString();
    }

    public int getOrderedValue() {
        return orderedValue;
    }
}
