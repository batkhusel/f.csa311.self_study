package com.example;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== Flashcard Learning System ===");
        System.out.println("Version 2.0 - Enhanced with mistake tracking and custom achievements");

        if (args.length > 0 && args[0].equals("--help")) {
            System.out.println("\nStarting with help information...");
            Flashcard.main(new String[] { "--help" });
        } else {
            System.out.println("\nUse --help for command line options.");
            System.out.println("Starting flashcard application...");
            Flashcard.main(args);
        }
    }
}