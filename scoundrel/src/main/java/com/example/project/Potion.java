package com.example.project;

/* The 9 Hearts in the deck are Health Potions. */
public non-sealed class Potion extends Card {
    public Potion(int val) {
        super(CardSuit.HEART, val);
    }
}
