package com.example.project;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.List;

/*
 * The Swing front-end. Like ConsoleGame, it owns all the I/O and drives a
 * GameState purely through its move/query surface -- no game logic lives here.
 * Event-driven: cards and buttons call GameState moves and then re-render.
 *
 * Visually it borrows the vocabulary of card games -- a green felt table and
 * real playing-card faces (see CardView) -- since Scoundrel is literally a deck
 * of cards.
 */
public class SwingGame {
    private static final Color FELT_TOP = new Color(0x21, 0x63, 0x3f);
    private static final Color FELT_BOTTOM = new Color(0x11, 0x38, 0x26);
    private static final Color PARCHMENT = new Color(0xEC, 0xEA, 0xE0);

    private GameState game;

    private final JFrame frame = new JFrame("Scoundrel");
    private final StatusBar statusBar = new StatusBar();
    private final JPanel roomRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
    private final PillButton dodgeButton = new PillButton("Dodge room");
    private final JLabel messageLabel = new JLabel(" ", SwingConstants.CENTER);

    public SwingGame () {
        this.game = new GameState();
        game.fillRoom();
        buildUi();
        render();
        frame.setVisible(true);
    }

    private void buildUi () {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(620, 460));
        frame.setSize(680, 480);

        FeltPanel table = new FeltPanel();
        table.setLayout(new BorderLayout());
        table.setBorder(BorderFactory.createEmptyBorder(6, 8, 12, 8));
        frame.setContentPane(table);

        // top: health + weapon
        statusBar.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
        table.add(statusBar, BorderLayout.NORTH);

        // centre: the room of cards
        roomRow.setOpaque(false);
        table.add(roomRow, BorderLayout.CENTER);

        // bottom: dodge + message
        JPanel bottom = new JPanel(new BorderLayout(12, 0));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        dodgeButton.addActionListener(e -> onDodge());
        messageLabel.setForeground(PARCHMENT);
        messageLabel.setFont(messageLabel.getFont().deriveFont(Font.ITALIC, 13f));
        bottom.add(dodgeButton, BorderLayout.WEST);
        bottom.add(messageLabel, BorderLayout.CENTER);
        table.add(bottom, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
    }

    // ---- rendering: read GameState, draw it ----

    private void render () {
        Weapon w = game.getEquippedWeapon();
        String weaponText = (w == null)
            ? "bare hands"
            : w + "  (slays monsters up to " + (w.getLowestMonsterAttacked() - 1) + ")";
        statusBar.update(game.getPlayerHealth(), weaponText);

        roomRow.removeAll();
        List<Card> room = game.getRoom();
        for (int i = 0; i < room.size(); i++) {
            final int idx = i;
            CardView view = new CardView(room.get(i));
            view.onActivate(() -> onCardChosen(idx));
            roomRow.add(view);
        }
        roomRow.revalidate();
        roomRow.repaint();

        dodgeButton.setEnabled(game.canDodge());
    }

    // ---- interaction: gather a choice, poke GameState, re-render ----

    private void onDodge () {
        game.dodgeRoom();
        messageLabel.setText("You slipped past the room.");
        render();
    }

    private void onCardChosen (int idx) {
        // only a slayable monster offers a real weapon-vs-hands choice
        Attack how = Attack.BAREHANDED;
        if (game.canUseWeaponOn(idx)) {
            int choice = JOptionPane.showOptionDialog(
                frame,
                "Fight this monster with your weapon, or with your bare hands?",
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
        for (Component card : roomRow.getComponents()) {
            if (card instanceof CardView cv) {
                cv.setActive(false);
            }
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

    // ---- little custom-painted pieces, all presentation ----

    // green felt table with a soft vignette
    private static class FeltPanel extends JPanel {
        @Override
        protected void paintComponent (Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();
            g2.setPaint(new GradientPaint(0, 0, FELT_TOP, 0, h, FELT_BOTTOM));
            g2.fillRect(0, 0, w, h);
            g2.setPaint(new RadialGradientPaint(
                new Point2D.Float(w / 2f, h / 2f), Math.max(w, h) / 1.15f,
                new float[] { 0f, 1f },
                new Color[] { new Color(0, 0, 0, 0), new Color(0, 0, 0, 95) }));
            g2.fillRect(0, 0, w, h);
            g2.dispose();
        }
    }

    // health as a colour-coded pill, with the weapon named alongside
    private static class StatusBar extends JPanel {
        private int hp = 20;
        private String weaponText = "bare hands";

        StatusBar () {
            setOpaque(false);
            setPreferredSize(new Dimension(0, 60));
        }

        void update (int hp, String weaponText) {
            this.hp = hp;
            this.weaponText = weaponText;
            repaint();
        }

        @Override
        protected void paintComponent (Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int barX = 10;
            int barY = 18;
            int barW = 230;
            int barH = 24;

            // track
            g2.setColor(new Color(0, 0, 0, 90));
            g2.fillRoundRect(barX, barY, barW, barH, barH, barH);

            // fill
            double ratio = Math.max(0, Math.min(1.0, hp / 20.0));
            Color hpColor = (hp > 10) ? new Color(0x46, 0xB8, 0x6E)
                          : (hp > 5)  ? new Color(0xDD, 0xA6, 0x3A)
                                      : new Color(0xD1, 0x46, 0x46);
            g2.setColor(hpColor);
            g2.fillRoundRect(barX, barY, Math.max(barH, (int) (barW * ratio)), barH, barH, barH);

            // label
            g2.setColor(Color.WHITE);
            g2.setFont(getFont().deriveFont(Font.BOLD, 13f));
            g2.drawString("HP " + hp + " / 20", barX + 14, barY + 17);

            // weapon
            g2.setColor(PARCHMENT);
            g2.setFont(getFont().deriveFont(Font.PLAIN, 13f));
            g2.drawString("⚔  " + weaponText, barX + barW + 20, barY + 17);

            g2.dispose();
        }
    }

    // a flat rounded button that respects the table's palette
    private static class PillButton extends JButton {
        PillButton (String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(getFont().deriveFont(Font.BOLD, 13f));
            setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent (Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color base;
            if (!isEnabled()) {
                base = new Color(0x3d, 0x4a, 0x42);
            } else if (getModel().isRollover()) {
                base = new Color(0x8c, 0x22, 0x2f);
            } else {
                base = new Color(0x70, 0x1a, 0x24);
            }
            g2.setColor(base);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();

            setForeground(isEnabled() ? Color.WHITE : new Color(0x9a, 0xa2, 0x9c));
            super.paintComponent(g);
        }
    }
}
