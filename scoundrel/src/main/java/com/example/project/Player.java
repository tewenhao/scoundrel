package com.example.project;

import java.lang.Math;

public class Player {
    private int health;
    private Weapon equippedWeapon = null;

    public Player () {
        this.health = 20;
    }

    public int getHealth () {
        return this.health;
    }

    public boolean isAlive () {
        return this.health > 0;
    }

    public boolean hasWeapon () {
        return this.equippedWeapon != null;
    }

    public Weapon getWeapon () {
        return this.equippedWeapon;
    } 

    public void equipWeapon (Weapon weapon) {
        this.equippedWeapon = weapon;
    }

    public void drinkPotion (Potion potion) {
        this.health = Math.min(20, health + potion.getOrderedValue());
    }

    public void attackMonsterWithHands (Monster monster) {
        this.health -= monster.getOrderedValue();
    }

    public void attackMonsterWithWeapon (Monster monster) {
        this.equippedWeapon.updateMonsterAttacked(monster);
        this.health -= Math.max(0, monster.getOrderedValue() - this.equippedWeapon.getOrderedValue());
    }
}
