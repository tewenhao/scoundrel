package com.example.project;

import java.util.ArrayList;
import java.util.List;

/*
 * The 9 Diamonds in the deck are Weapons. Red face cards are removed from the deck.
*/
public class Weapon extends Card {
    private int lowestMonsterAttacked;
    private List<Monster> slainMonsters = new ArrayList<>(); // game requires weapon slain monsters to be stacked on top of weapon

    public Weapon (int val) {
        super(CardSuit.DIAMOND, val);
        this.lowestMonsterAttacked = 15;
    }

    public int getLowestMonsterAttacked () {
        // 15 - no monster attacked before
        // since 14 is the highest value (Ace)
        return this.lowestMonsterAttacked;
    }

    public boolean canSlay (Monster monster) {
        return this.getLowestMonsterAttacked() > monster.getOrderedValue();
    }

    public void updateMonsterAttacked (Monster monster) {
            this.lowestMonsterAttacked = monster.getOrderedValue();
            this.slainMonsters.add(monster);
    }
}
