package com.example.project;

/* A class to get user input */
public class User {
    private static User mInstance = null;

    private User () {};

    public static User getInstance () {
        if (mInstance == null) {
            mInstance = new User();
        }

        return mInstance;
    }

    public boolean chooseDodgeOrFight () {
        System.out.println("Enter 0 to dodge room, 1 to fight");
        // TODO: get user input to decide whether to fight.
        // return false if dodge, true if fights
    }

    public int chooseCard () {
        
    }
}
