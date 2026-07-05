package com.example.project;

public class GameLoop {
    private Deck deck;
    private Player player;
    private User user;

    private boolean dodgedRoom;
    private boolean usedPotion;

    // TODO: find difference between iniitalising in constructor
    // TODO: and initialising when declaring variable
    // TODO: for variables which we already know the value from the start
    // TODO: ie. is there a need for a constructor
    public GameLoop () {
        this.deck = Deck.getInstance();
        this.player = new Player();
        this.user = User.getInstance();;

        this.dodgedRoom = false;
        this.usedPotion = false;
    }

    public void gameTurn () {
        // 1. set up room at the start of every turn and display room
        deck.fillRoom();
        deck.displayRoom();

        // 2. choose between dodging room and fightng room
        if (dodgedRoom == false) {
            if (user.chooseDodgeOrFight() == false) {
                deck.dodgeRoom();
                return;
            }
        }

        // combat phase
        
    }
}
