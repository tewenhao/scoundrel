package com.example.project;

/*
 * The 9 Diamonds in the deck are Weapons. Red face cards are removed from the deck.
*/
public class Weapon extends Card {
    private int highestMonsterAttacked;

    public Weapon (int val) {
        super(CardSuit.DIAMOND, val);
        this.highestMonsterAttacked = 15;
    }

    public int getHighestMonsterAttacked () {
        // 15 - no monster attacked before
        // since 14 is the highest value (Ace)
        return this.highestMonsterAttacked;
    }

    public void updateMonsterAttacked (Monster monster) throws MonsterValueTooHighException {
        if (monster.getOrderedValue() >= this.highestMonsterAttacked) {
            throw new MonsterValueTooHighException();
        }

        else {
            this.highestMonsterAttacked = monster.getOrderedValue();
        }
    }
}
