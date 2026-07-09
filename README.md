# scoundrel

scoundrel. the rogue-like card game. [rules are here](http://stfj.net/art/2011/Scoundrel.pdf). doing this because i promised my supervisor i'll make a game using java and i procrastinated till my summer holidays.

A single-player, text-based version you play in your terminal. You descend through a dungeon dealt from a deck of cards, fighting monsters, picking up weapons, and drinking potions, trying to survive to the bottom.

## Requirements

- **JDK 25 or newer** — the code uses sealed types, pattern-matching `switch`, and unnamed patterns (`_`).
- **Maven** (optional) — only if you want to build/test through Maven instead of `javac`.

## Build & run

The Maven project lives in the `scoundrel/` subfolder.

With Maven:

```bash
cd scoundrel
mvn compile
java -cp target/classes com.example.project.App
```

Or with just the JDK, no Maven:

```bash
cd scoundrel
javac --release 25 -d target/classes src/main/java/com/example/project/*.java
java -cp target/classes com.example.project.App
```

The game runs entirely in the terminal. Press `Ctrl+C` to quit at any time.

## How to play

You start with **20 health** (max 20). Each turn the game prints your status and the current room, then asks what you want to do:

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

- The top line shows your **health** and your **equipped weapon**. A weapon reads e.g. `5 of DIAMOND (slays monsters up to 8)` — it can only be used on monsters weaker than that.
- The room lists up to **4 cards**, each with an index in square brackets. You interact with a card by typing its index.

### The prompts

Every input is either a **number** (a card index) or **Y / N** (case-insensitive). Bad input is rejected and re-asked, so you can't crash it by fat-fingering.

1. **Dodge this room? (Y to dodge, N to fight)**
   Only appears when dodging is allowed. Dodging sends all four cards to the bottom of the deck and deals a fresh room. You **can't dodge two rooms in a row**.

2. **Which card? Enter 0 to N.**
   Type the index of the card you want to resolve. What happens depends on the card:
   - **Monster** (clubs & spades) — you fight it and take damage.
   - **Weapon** (diamonds) — you equip it, replacing any weapon you were holding.
   - **Potion** (hearts) — you drink it and heal.

3. **Use your weapon? (Y for weapon, N for bare hands)**
   Only appears when the card is a monster your equipped weapon is actually allowed to slay.
   - **Bare hands** — you lose health equal to the monster's full value.
   - **Weapon** — you lose health equal to `monster value − weapon value` (never below 0).

### The rules that matter

- **Weapons wear down.** After a weapon slays a monster, it can only be used on monsters with a *strictly lower* value next time (that's the "slays monsters up to N" line). Fight a tougher monster bare-handed, or equip a new weapon.
- **One potion per room.** Only the first potion you drink in a given room heals you; any others are wasted.
- **Rooms carry over.** A room is 4 cards. Fight through 3 and the last one carries into the next room. Clearing a room lets you dodge again.

### Winning, losing, and your score

The game ends when you clear the whole dungeon (**win**) or your health hits 0 (**lose**). Either way it prints a **final score**:

- **If you win:** your score is your remaining health. If the very last card of the dungeon is a potion, its value is added on top — the only way to score above 20.
- **If you lose:** your score is your health minus the total value of every monster still left undefeated in the dungeon (so, a negative number — the deeper you got, the less negative).

Lower monsters left behind means a better score. Good luck down there.
