package com.example.project;

import java.util.Iterator;
import java.util.List;
import java.util.Collections;

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

    private Card lastCardPlayed; // the final card decides the win-with-potion bonus

    public GameState () {
        this.deck = new Deck();
        this.player = new Player();
        
        this.usedPotion = false;
        this.dodgedRoom = false;
    }

    public boolean isOver () {
        return !player.isAlive() || deck.isEmpty();
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
        while (!deck.emptyDrawPile() && deck.getRoom().size() < 4) {
            deck.drawCard();
        }
    }

    public boolean canDodge () {
        return !this.dodgedRoom;
    }

    public void dodgeRoom () {
        this.dodgedRoom = true;
        this.usedPotion = false;

        Iterator<Card> iter = deck.getRoom().iterator();
        while (iter.hasNext()) {
            Card c = iter.next();
            deck.putInBottom(c);
            iter.remove();
        }

        this.fillRoom();
    }

    public void fightCard(int idx, Attack attackChoice) {
        Card c = deck.chooseCardFromRoom(idx);
        this.lastCardPlayed = c;

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
                // the final card of the dungeon is not drunk — its value is added
                // once, at scoring (getScore), so a last potion never counts twice
                if (!deck.isEmpty() && !this.usedPotion) {
                    this.usedPotion = true;
                    player.drinkPotion(p);
                }
                deck.putInDiscard(p);
            }
        }
    }

    // called after a room is fought down to its carry-over card:
    // clears the turn flags and deals the next room
    public void startNextRoom () {
        this.usedPotion = false;
        this.dodgedRoom = false;
        this.fillRoom();
    }

    // ---- read-only surface for the front-end to render ----

    public int getPlayerHealth () {
        return player.getHealth();
    }

    public Weapon getEquippedWeapon () {
        return player.getWeapon();
    }

    public List<Card> getRoom () {
        return Collections.unmodifiableList(deck.getRoom());
    }

    // true only when the card at idx is a Monster the equipped weapon may legally slay
    public boolean canUseWeaponOn (int idx) {
        return deck.getRoom().get(idx) instanceof Monster m
            && player.hasWeapon()
            && player.getWeapon().canSlay(m);
    }

    // scoring — only meaningful once isOver() is true.
    // won   -> remaining health, plus the last card's value if it was a potion
    // died  -> health minus the value of every monster left undefeated (negative)
    public int getScore () {
        if (player.isAlive()) {
            int score = player.getHealth();
            if (lastCardPlayed instanceof Potion p) {
                score += p.getOrderedValue();
            }
            return score;
        }
        return player.getHealth() - deck.remainingMonsterValue();
    }
}
