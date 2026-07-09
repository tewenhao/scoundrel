package com.example.project;

import java.util.Iterator;

/*
 * A class which handles the current game state.
 *
 * Methods in this class are possible actions in the game
 * that can modify game state.
 */
public class GameState {
    private Deck deck;
    private Player player;

    private boolean usedPotion;
    private boolean dodgedRoom;

    public GameState () {
        this.deck = new Deck();
        this.player = new Player();
        
        this.usedPotion = false;
        this.dodgedRoom = false;
    }

    public boolean isOver () {
        return player.isAlive() || deck.isEmpty();
    }

    public boolean isRoomComplete () {
        if (deck.emptyDrawPile()) {
            return deck.emptyRoom();
        }
        else {
            return deck.getRoom().size() == 1;
        }
    }

    public void fillRoom () {
        while (deck.getRoom().size() < 4) {
            deck.drawCard();
        }
    }

    public boolean canDodge () {
        return !this.dodgedRoom;
    }

    public void dodgeRoom () {
        this.dodgedRoom = true;

        Iterator<Card> iter = deck.getRoom().iterator();
        while (iter.hasNext()) {
            Card c = iter.next();
            deck.putInBottom(c);
            iter.remove();
        }

        for (int i = 0; i < 4; i++) {
            deck.drawCard();
        }
    }

    public void fightCard(int idx, Attack attackChoice) {
        Card c = deck.chooseCardFromRoom(idx);
        
        // not antipattern to check type of card here
        // since there is no unifying action the respective
        // cards can take
        switch (c) {
            case Monster m -> {
                if (attackChoice == Attack.WEAPON && player.hasWeapon() && player.getWeapon().canSlay(m)) {
                    player.attackMonsterWithWeapon(m);
                } else {
                    player.attackMonsterWithHands(m);
                    deck.putInDiscard(m);
                }
            }
            case Weapon w -> {
                Weapon oldWeapon = player.getWeapon();
                player.equipWeapon(w);
                if (oldWeapon != null) {
                    deck.putInDiscard(oldWeapon);
                }
            }
            case Potion p -> {
                if (!this.usedPotion) {
                    this.usedPotion = true;
                    player.drinkPotion(p);
                }
                deck.putInDiscard(p);
            }
        }
    }
}
