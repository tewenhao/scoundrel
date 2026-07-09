package com.example.project;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

/*
 * The Swing front-end. Like ConsoleGame, it owns all the I/O and drives a
 * GameState purely through its move/query surface -- no game logic lives here.
 * It is event-driven: buttons call GameState moves and then re-render, rather
 * than a loop pulling for input. Everything runs on the Swing event thread.
 */
public class SwingGame {
    private GameState game;

    private final JFrame frame = new JFrame("Scoundrel");
    private final JProgressBar healthBar = new JProgressBar(0, 20);
    private final JLabel weaponLabel = new JLabel();
    private final JPanel roomPanel = new JPanel();
    private final JButton dodgeButton = new JButton("Dodge room");
    private final JLabel messageLabel = new JLabel(" ");

    public SwingGame () {
        this.game = new GameState();
        game.fillRoom();
        buildUi();
        render();
        frame.setVisible(true);
    }

    private void buildUi () {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(660, 440);
        frame.setLayout(new BorderLayout(10, 10));

        // top: health bar + equipped weapon
        JPanel status = new JPanel(new GridLayout(2, 1, 0, 6));
        status.setBorder(BorderFactory.createEmptyBorder(12, 14, 4, 14));
        healthBar.setStringPainted(true);
        healthBar.setPreferredSize(new Dimension(0, 26));
        weaponLabel.setFont(weaponLabel.getFont().deriveFont(Font.PLAIN, 13f));
        status.add(healthBar);
        status.add(weaponLabel);
        frame.add(status, BorderLayout.NORTH);

        // centre: the room, one button per card
        roomPanel.setLayout(new GridLayout(1, 4, 8, 8));
        roomPanel.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
        frame.add(roomPanel, BorderLayout.CENTER);

        // bottom: dodge control + status message
        JPanel bottom = new JPanel(new BorderLayout(10, 0));
        bottom.setBorder(BorderFactory.createEmptyBorder(4, 14, 12, 14));
        dodgeButton.addActionListener(e -> onDodge());
        messageLabel.setForeground(new Color(0x6a6870));
        bottom.add(dodgeButton, BorderLayout.WEST);
        bottom.add(messageLabel, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);
    }

    // ---- rendering: read GameState, draw it ----

    private void render () {
        int hp = game.getPlayerHealth();
        healthBar.setValue(Math.max(0, hp));
        healthBar.setString("HP " + hp + " / 20");

        Weapon w = game.getEquippedWeapon();
        weaponLabel.setText("Weapon: " + (w == null
            ? "bare hands"
            : w + "  (slays monsters up to " + (w.getLowestMonsterAttacked() - 1) + ")"));

        roomPanel.removeAll();
        List<Card> room = game.getRoom();
        for (int i = 0; i < room.size(); i++) {
            roomPanel.add(makeCardButton(i, room.get(i)));
        }
        roomPanel.revalidate();
        roomPanel.repaint();

        dodgeButton.setEnabled(game.canDodge());
    }

    private JButton makeCardButton (int idx, Card c) {
        JButton b = new JButton("<html><center>" + describe(c) + "</center></html>");
        b.setPreferredSize(new Dimension(130, 170));
        b.setFocusPainted(false);
        b.addActionListener(e -> onCardChosen(idx));
        return b;
    }

    // front-end formatting only
    private String describe (Card c) {
        return switch (c) {
            case Monster _ -> c + "<br><br><b>Monster</b><br>" + c.getOrderedValue() + " dmg";
            case Weapon _  -> c + "<br><br><b>Weapon</b>";
            case Potion _  -> c + "<br><br><b>Potion</b><br>heals " + c.getOrderedValue();
        };
    }

    // ---- interaction: gather a choice, poke GameState, re-render ----

    private void onDodge () {
        game.dodgeRoom();
        messageLabel.setText("You dodged the room.");
        render();
    }

    private void onCardChosen (int idx) {
        // only a slayable monster offers a real weapon-vs-hands choice
        Attack how = Attack.BAREHANDED;
        if (game.canUseWeaponOn(idx)) {
            int choice = JOptionPane.showOptionDialog(
                frame,
                "Fight this monster with your weapon or with your bare hands?",
                "Choose your attack",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new String[] { "Weapon", "Bare hands" }, "Weapon");
            how = (choice == 0) ? Attack.WEAPON : Attack.BAREHANDED;
        }

        game.fightCard(idx, how);
        messageLabel.setText(" ");

        if (game.isRoomComplete()) {
            game.startNextRoom();
        }

        render();

        if (game.isOver()) {
            endGame();
        }
    }

    private void endGame () {
        for (Component card : roomPanel.getComponents()) {
            card.setEnabled(false);
        }
        dodgeButton.setEnabled(false);

        String outcome = (game.getPlayerHealth() > 0)
            ? "You cleared the dungeon. You win!"
            : "You died in the dungeon.";

        int again = JOptionPane.showConfirmDialog(
            frame,
            outcome + "\nFinal score: " + game.getScore() + "\n\nPlay again?",
            "Game over",
            JOptionPane.YES_NO_OPTION);

        if (again == JOptionPane.YES_OPTION) {
            this.game = new GameState();
            game.fillRoom();
            messageLabel.setText(" ");
            render();
        } else {
            frame.dispose();
        }
    }
}
