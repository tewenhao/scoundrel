package com.example.project;

import java.util.Scanner;

/*
 * A class to get user input via console.
 * For testing whether the main functionality work.
 * 
 * This is limited to ONLY input related stuff
 * Game logic verification will be left to the game loop
 * Eventual plan is to migrate to Swing or any similar app UI.
 */
public class ConsoleInput {
    Scanner scanner = new Scanner(System.in);

    public ConsoleInput () {};

    public boolean getBooleanFromUser () {
        String result = "";

        while (result != "Yes" || result != "No") {
            System.out.println("Key either Y or N ONLY. Y for Yes, N for No:");
            result = scanner.nextLine();
        }

        return result == "Y";
    }

    public int getNumberFromUser () {
        String result = "";

        // this regex checks if a string contains digits only
        while (!result.matches("\\d+")) {
            result = scanner.nextLine();
        }

        return Integer.parseInt(result);
    }
}
