package com.example.project;

import javax.swing.SwingUtilities;

/*
 * Entry point. Launches the Swing app by default; pass "console" to play
 * the text version instead. Both are front-ends over the same GameState.
 */
public class App
{
    public static void main( String[] args )
    {
        if (args.length > 0 && args[0].equalsIgnoreCase("console")) {
            new ConsoleGame().run();
        } else {
            SwingUtilities.invokeLater(SwingGame::new);
        }
    }
}
