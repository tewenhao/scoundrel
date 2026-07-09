package com.example.project;

import java.util.List;

/*
 * The console front-end. Owns the game loop and all I/O.
 * Renders state, asks the player for choices, and pokes GameState.
 * Swap this out for a Swing front-end later; GameState stays untouched.
 */
public class ConsoleGame {
    private ConsoleInput input;
    private GameState game;

    public ConsoleGame () {
        this.input = new ConsoleInput();
        this.game = new GameState();
    }

    public void run () {
        game.fillRoom(); // deal the opening room

        while (!game.isOver()) {
            render();

            // 1. offer dodge, but only when the rules allow it
            if (game.canDodge()) {
                System.out.println("Dodge this room? (Y to dodge, N to fight)");
                if (input.getBooleanFromUser()) {
                    game.dodgeRoom();
                    continue;
                }
            }

            // 2. fight: pick a card from the room
            int idx = askCardIndex();

            // 3. only a slayable monster gives a real weapon-vs-hands choice
            Attack how = Attack.BAREHANDED;
            if (game.canUseWeaponOn(idx)) {
                System.out.println("Use your weapon? (Y for weapon, N for bare hands)");
                how = input.getBooleanFromUser() ? Attack.WEAPON : Attack.BAREHANDED;
            }
            game.fightCard(idx, how);

            // 4. once only the carry-over card is left, deal the next room
            if (game.isRoomComplete()) {
                game.startNextRoom();
            }
        }

        renderResult();
    }

    private void render () {
        System.out.println("\n----------------------------------------");

        Weapon weapon = game.getEquippedWeapon();
        String weaponText = (weapon == null)
            ? "bare hands"
            : weapon + " (slays monsters up to " + (weapon.getLowestMonsterAttacked() - 1) + ")";
        System.out.println("HP " + game.getPlayerHealth() + "/20     Weapon: " + weaponText);

        System.out.println("Dungeon room:");
        List<Card> room = game.getRoom();
        for (int i = 0; i < room.size(); i++) {
            System.out.println("  [" + i + "] " + describe(room.get(i)));
        }
        System.out.println("----------------------------------------");
    }

    // front-end formatting only: turn a card into a readable line
    private String describe (Card c) {
        return switch (c) {
            case Monster _ -> c + " (monster - " + c.getOrderedValue() + " dmg)";
            case Weapon _  -> c + " (weapon)";
            case Potion _  -> c + " (potion - heals " + c.getOrderedValue() + ")";
        };
    }

    // re-prompts until the player gives an index that is actually in the room
    private int askCardIndex () {
        int size = game.getRoom().size();
        System.out.println("Which card? Enter 0 to " + (size - 1) + ".");

        int idx = input.getNumberFromUser();
        while (idx < 0 || idx >= size) {
            System.out.println(idx + " is out of range. Enter 0 to " + (size - 1) + ".");
            idx = input.getNumberFromUser();
        }
        return idx;
    }

    private void renderResult () {
        System.out.println("\n========================================");
        if (game.getPlayerHealth() > 0) {
            System.out.println("You cleared the dungeon. You win!");
        } else {
            System.out.println("You died in the dungeon. Game over.");
        }
        System.out.println("Final score: " + game.getScore());
        System.out.println("========================================");
    }
}
