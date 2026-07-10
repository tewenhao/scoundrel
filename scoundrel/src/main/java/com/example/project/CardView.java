package com.example.project;

import javax.swing.JComponent;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/*
 * A single playing card, painted to look like a real card face -- rank and suit
 * pips in the corners, a large centre pip, red for hearts/diamonds and black for
 * clubs/spades -- with the Scoundrel role (monster damage / weapon / heal) along
 * the bottom. Pure presentation: it reads a Card and reports clicks, nothing else.
 */
class CardView extends JComponent {
    private static final Color RED   = new Color(0xB4, 0x2A, 0x33);
    private static final Color BLACK = new Color(0x22, 0x22, 0x28);
    private static final Color FACE  = new Color(0xFB, 0xFA, 0xF4);
    private static final Color FACE_DEAD = new Color(0xD8, 0xD6, 0xCE);
    private static final Color INK_DEAD  = new Color(0x9A, 0x98, 0x92);

    private final Card card;
    private boolean hover = false;
    private boolean active = true;
    private Runnable onActivate;

    CardView (Card card) {
        this.card = card;
        setPreferredSize(new Dimension(124, 172));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered (MouseEvent e) { if (active) { hover = true; repaint(); } }
            @Override public void mouseExited (MouseEvent e)  { hover = false; repaint(); }
            @Override public void mouseClicked (MouseEvent e) { if (active && onActivate != null) onActivate.run(); }
        });
    }

    void onActivate (Runnable r) {
        this.onActivate = r;
    }

    void setActive (boolean a) {
        this.active = a;
        setCursor(Cursor.getPredefinedCursor(a ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        repaint();
    }

    private boolean isRed () {
        CardSuit s = card.getCardSuit();
        return s == CardSuit.HEART || s == CardSuit.DIAMOND;
    }

    private String suitGlyph () {
        return switch (card.getCardSuit()) {
            case HEART   -> "♥";
            case DIAMOND -> "♦";
            case CLUB    -> "♣";
            case SPADE   -> "♠";
        };
    }

    private String rank () {
        return switch (card.getOrderedValue()) {
            case 11 -> "J";
            case 12 -> "Q";
            case 13 -> "K";
            case 14 -> "A";
            default -> String.valueOf(card.getOrderedValue());
        };
    }

    private String role () {
        return switch (card) {
            case Monster _ -> "MONSTER · " + card.getOrderedValue() + " DMG";
            case Weapon _  -> "WEAPON";
            case Potion _  -> "POTION · HEAL " + card.getOrderedValue();
        };
    }

    @Override
    protected void paintComponent (Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int pad = 9;
        int lift = (hover && active) ? 7 : 0;
        int x = pad;
        int y = pad - lift;
        int cw = getWidth() - pad * 2;
        int ch = getHeight() - pad * 2;
        int arc = 18;

        // drop shadow
        g2.setColor(new Color(0, 0, 0, (hover && active) ? 80 : 50));
        g2.fill(new RoundRectangle2D.Float(x + 3, y + 7, cw, ch, arc, arc));

        // face
        g2.setColor(active ? FACE : FACE_DEAD);
        g2.fill(new RoundRectangle2D.Float(x, y, cw, ch, arc, arc));

        Color ink = active ? (isRed() ? RED : BLACK) : INK_DEAD;

        // border (accent on hover)
        g2.setColor((hover && active) ? ink : new Color(0, 0, 0, 45));
        g2.setStroke(new BasicStroke((hover && active) ? 2.6f : 1.2f));
        g2.draw(new RoundRectangle2D.Float(x, y, cw, ch, arc, arc));

        g2.setColor(ink);
        String rank = rank();
        String glyph = suitGlyph();

        // top-left corner
        g2.setFont(getFont().deriveFont(Font.BOLD, 21f));
        g2.drawString(rank, x + 11, y + 27);
        g2.setFont(getFont().deriveFont(Font.PLAIN, 16f));
        g2.drawString(glyph, x + 11, y + 45);

        // bottom-right corner, rotated 180
        Graphics2D gr = (Graphics2D) g2.create();
        gr.rotate(Math.PI, x + cw / 2.0, y + ch / 2.0);
        gr.setFont(getFont().deriveFont(Font.BOLD, 21f));
        gr.drawString(rank, x + 11, y + 27);
        gr.setFont(getFont().deriveFont(Font.PLAIN, 16f));
        gr.drawString(glyph, x + 11, y + 45);
        gr.dispose();

        // large centre pip
        g2.setFont(getFont().deriveFont(Font.PLAIN, 54f));
        FontMetrics fm = g2.getFontMetrics();
        int gx = x + (cw - fm.stringWidth(glyph)) / 2;
        int gy = y + ch / 2 + fm.getAscent() / 2 - 12;
        g2.drawString(glyph, gx, gy);

        // role footer
        g2.setColor(active ? new Color(0x54, 0x52, 0x4e) : INK_DEAD);
        g2.setFont(getFont().deriveFont(Font.BOLD, 10f));
        String role = role();
        FontMetrics fm2 = g2.getFontMetrics();
        int rx = x + (cw - fm2.stringWidth(role)) / 2;
        g2.drawString(role, rx, y + ch - 15);

        g2.dispose();
    }
}
