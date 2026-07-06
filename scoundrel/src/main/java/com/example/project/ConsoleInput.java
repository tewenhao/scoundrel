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
        System.out.println("Key either Y or N ONLY. Y for Yes, N for No: ");
        String input = scanner.next();

        while (!input.equalsIgnoreCase("Y") && !input.equalsIgnoreCase("N")) {
            System.out.println("\"" + input + "\" is invalid. Please type Y or N.");
            System.out.print("Key either Y or N ONLY. Y for Yes, N for No: ");
            input = scanner.next(); // Clear buffer and get fresh input
        }

        return input.equalsIgnoreCase("Y");
    }

    public int getNumberFromUser () {
        System.out.print("Please enter an integer: ");

        while (!scanner.hasNextInt()) {
            String badInput = scanner.next();
            System.out.println("\"" + badInput + "\" is not a valid integer.");
            System.out.print("Try again. Enter an integer: ");
        }

        return scanner.nextInt();
    }
}
