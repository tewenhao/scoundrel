package com.example.project;

import java.util.Queue;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/*
 * While there should only be one deck of cards per game
 * a singleton implementation doesn't allow multiple games to spawn
*/
public class Deck {
    private final Queue<Card> drawPile;
    private final List<Card> dungeonRoom = new ArrayList<>(4);
    private final List<Card> discardPile = new ArrayList<>();

    public Deck () {
        // one time initialisation of drawPile
        List<Card> freshCards = new ArrayList<Card>();

        for (int i = 2; i < 15; i++) {
            freshCards.add(new Monster(CardSuit.CLUB, i));
            freshCards.add(new Monster(CardSuit.SPADE, i));
        }

        for (int i = 2; i < 11; i++) {
            freshCards.add(new Weapon(i));
            freshCards.add(new Potion(i));
        }
        
        Collections.shuffle(freshCards);

        this.drawPile = new ArrayDeque<Card>(freshCards);
    }

    public void fillRoom () {
        while (Array.getLength(this.dungeonRoom) < 4) {
            this.dungeonRoom.add(this.drawPile.remove());
        }
    }

    public void displayRoom () {
        System.out.println("Current Dungeon Room:");
        System.out.println(this.dungeonRoom);
    }

    public void dodgeRoom () {
        for (Card c : this.dungeonRoom) {
            this.drawPile.add(c);
        }

        this.dungeonRoom.clear();

        for (int i = 0; i < 4; i++) {
            this.dungeonRoom.add(this.drawPile.remove());
        }
    }

    public Card chooseCardFromRoom (int idx) {
        Card c = this.dungeonRoom.get(idx);
        this.dungeonRoom.remove(idx);
        return c;
    }

    public void putInDiscard (Card c) {
        this.discardPile.add(c);
    }
}
