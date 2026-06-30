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

    public void equipWeapon (Weapon weapon) {
        this.equippedWeapon = weapon;
    }

    public void drinkPotion (Potion potion) {
        this.health = Math.min(20, health + potion.getOrderedValue());
    }

    public void attackMonster (Monster monster, boolean bareHands) throws NoWeaponEquippedException, WeaponSlayedLowerMonsterException {
        if (bareHands) {
            this.health -= monster.getOrderedValue();
        }

        else {
            if (this.equippedWeapon == null) {
                throw new NoWeaponEquippedException();
            } else if (this.equippedWeapon.getHighestMonsterAttacked() <= monster.getOrderedValue()) {
                throw new WeaponSlayedLowerMonsterException();
            } else try {
                this.equippedWeapon.updateMonsterAttacked(monster);
                this.health -= Math.max(0, monster.getOrderedValue() - this.equippedWeapon.getOrderedValue());
            } catch (MonsterValueTooHighException e) {
                throw new AssertionError("This should be unreachable because of the previous clause in the if/else", e);
            }
        }
    }
}
