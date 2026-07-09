# scoundrel

scoundrel. the rogue-like card game. [rules are here](http://stfj.net/art/2011/Scoundrel.pdf). doing this because i promised my supervisor i'll make a game using java and i procrastinated till my summer holidays.

A single-player game you can play two ways: as a **desktop app** (Swing) or in your **terminal**. You descend through a dungeon dealt from a deck of cards, fighting monsters, picking up weapons, and drinking potions, trying to survive to the bottom. Both modes are the same game — they share all the rules and scoring below.

## Requirements

- **JDK 25 or newer** — the code uses sealed types, pattern-matching `switch`, and unnamed patterns (`_`).
- **A graphical environment** for the desktop app (any normal desktop). The terminal version needs none.
- **Maven** (optional) — only if you want to build/test through Maven instead of `javac`.

## Build & run

The Maven project lives in the `scoundrel/` subfolder. Build with Maven:

```bash
cd scoundrel
mvn compile
```

Or with just the JDK, no Maven:

```bash
cd scoundrel
javac --release 25 -d target/classes src/main/java/com/example/project/*.java
```

Then run either mode:

```bash
java -cp target/classes com.example.project.App           # desktop app (default)
java -cp target/classes com.example.project.App console    # terminal version
```

## Playing the desktop app

Running with no arguments opens a **Scoundrel** window:

- **Top** — a **health bar** (`HP 20 / 20`) and your **equipped weapon**. A weapon reads e.g. `5 of DIAMOND (slays monsters up to 8)`; it can only be used on monsters weaker than that.
- **Middle** — the current **room**, shown as up to **four card buttons**. Each button names the card and whether it's a **Monster** (with its damage), a **Weapon**, or a **Potion** (with how much it heals).
- **Bottom** — a **Dodge room** button and a status line.

How you interact:

1. **Click a card** to resolve it — fight the monster, equip the weapon, or drink the potion.
2. **Fighting a monster:** if your equipped weapon is allowed to slay it, a dialog asks whether to use your **Weapon** or your **Bare hands**. Otherwise you fight bare-handed automatically.
3. **Dodge room** sends all four cards to the bottom of the deck and deals a fresh room. The button is greyed out when you're not allowed to dodge (you can't dodge two rooms in a row).

When the game ends, a dialog shows the outcome and your **final score**, and offers to **play again**. Choosing not to closes the window.

## Playing in the terminal

Running with the `console` argument plays the same game as text. Each turn it prints your status and the current room:

```text
----------------------------------------
HP 20/20     Weapon: bare hands
Dungeon room:
  [0] 10 of HEART (potion - heals 10)
  [1] 6 of SPADE (monster - 6 dmg)
  [2] 2 of DIAMOND (weapon)
  [3] 4 of SPADE (monster - 4 dmg)
----------------------------------------
```

Each card has an index in square brackets; you act on a card by typing its index. Every input is either a **number** (a card index) or **Y / N** (case-insensitive), and bad input is rejected and re-asked. The prompts are:

1. **Dodge this room? (Y to dodge, N to fight)** — only when dodging is allowed.
2. **Which card? Enter 0 to N.** — type the index of the card to resolve.
3. **Use your weapon? (Y for weapon, N for bare hands)** — only when the chosen monster is one your weapon may slay.

Press `Ctrl+C` to quit at any time.

## The rules that matter

You start with **20 health** (max 20). The room is dealt from a deck of monsters (clubs & spades), weapons (diamonds), and potions (hearts).

- **Monsters** cost you health. Bare-handed you lose the monster's full value; with a weapon you lose `monster value − weapon value` (never below 0).
- **Weapons wear down.** After a weapon slays a monster, it can only be used on monsters with a *strictly lower* value next time (that's the "slays monsters up to N" line). Fight a tougher monster bare-handed, or equip a new weapon — equipping a new one discards the old.
- **One potion per room.** Only the first potion you drink in a given room heals you; any others are wasted.
- **Rooms carry over.** A room is 4 cards. Fight through 3 and the last one carries into the next room. Clearing a room lets you dodge again.

## Winning, losing, and your score

The game ends when you clear the whole dungeon (**win**) or your health hits 0 (**lose**). Either way you get a **final score**:

- **If you win:** your score is your remaining health. If the very last card of the dungeon is a potion, its value is added on top — the only way to score above 20.
- **If you lose:** your score is your health minus the total value of every monster still left undefeated in the dungeon (so, a negative number — the deeper you got, the less negative).

Lower monsters left behind means a better score. Good luck down there.
