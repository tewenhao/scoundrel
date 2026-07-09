package com.example.project;

/*
 * The 26 Clubs and Spades in the deck are monsters. Their damage is equal to their ordered value.
 * 10 is 10, Jack is 11, Queen is 12, King is 13, Ace is 14
 */
public non-sealed class Monster extends Card {
    public Monster (CardSuit suit, int val) {
        super(suit, val);
    }
}
