package com.example.project;

public abstract class Card {
    private CardSuit cardSuit;
    private int orderedValue;

    public Card (CardSuit suit, int orderedValue) {
        this.cardSuit = suit;
        this.orderedValue = orderedValue;
    }

    public String getCardName() {
        return this.cardSuit.toString() + String.valueOf(this.orderedValue);
    }

    public int getOrderedValue() {
        return orderedValue;
    }
}
