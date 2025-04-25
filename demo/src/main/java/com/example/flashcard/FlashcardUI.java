package com.example.flashcard;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.NoSuchElementException;
import com.example.Flashcard;

public class FlashcardUI {
    private Scanner scanner;
    private BufferedReader reader;
    private Flashcard flashcard;

    public FlashcardUI(Flashcard flashcard) {
        this.flashcard = flashcard;
        try {
            this.scanner = new Scanner(System.in);
            this.reader = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            System.err.println("Error initializing input: " + e.getMessage());
        }
    }

    private String readInput() {
        try {
            // CRITICAL FIX: Scanner.hasNextLine() blocks and waits for input, leading to
            // the infinite loop
            // Instead, just try to read directly and handle any exception
            if (scanner != null) {
                try {
                    return scanner.nextLine().trim();
                } catch (NoSuchElementException | IllegalStateException e) {
                    // If scanner fails, continue to other methods
                    System.err.println("Scanner input failed, trying other methods");
                }
            }

            // Try BufferedReader next
            if (reader != null) {
                try {
                    String line = reader.readLine();
                    return line != null ? line.trim() : "9";
                } catch (IOException e) {
                    // If reader fails, continue to other methods
                    System.err.println("BufferedReader input failed, trying other methods");
                }
            }

            // Last resort - Console
            Console console = System.console();
            if (console != null) {
                String line = console.readLine();
                return line != null ? line.trim() : "9";
            }

            System.err.println("All input methods failed - returning exit code");
            return "9"; // Exit code if all input methods fail
        } catch (Exception e) {
            System.err.println("Error reading input: " + e.getMessage());
            return "9"; // Exit code on error
        }
    }

    public void showMainMenu() {
        String[] options = {
                "Study Cards",
                "Add New Card",
                "View Recent Mistakes",
                "View Most Frequent Mistakes",
                "Change Card Order",
                "View Achievements",
                "Settings",
                "Help",
                "Exit"
        };

        boolean exit = false;
        while (!exit) {
            try {
                System.out.println("\n===== FLASHCARD SYSTEM MENU =====");
                for (int i = 0; i < options.length; i++) {
                    System.out.println((i + 1) + ". " + options[i]);
                }
                System.out.print("\nEnter your choice (1-" + options.length + "): ");
                System.out.flush();

                String choice = readInput();

                try {
                    int selection = Integer.parseInt(choice);
                    if (selection < 1 || selection > options.length) {
                        System.out
                                .println("Invalid choice. Please enter a number between 1 and " + options.length + ".");
                        continue;
                    }

                    switch (selection) {
                        case 1:
                            flashcard.studyCards();
                            break;
                        case 2:
                            flashcard.addNewCard();
                            break;
                        case 3:
                            flashcard.viewRecentMistakes();
                            break;
                        case 4:
                            flashcard.viewMostFrequentMistakes();
                            break;
                        case 5:
                            flashcard.changeCardOrder();
                            break;
                        case 6:
                            flashcard.viewAchievements();
                            break;
                        case 7:
                            showSettingsMenu();
                            break;
                        case 8:
                            flashcard.displayHelp();
                            break;
                        case 9:
                            exit = true;
                            close();
                            System.out.println("Thank you for using the Flashcard System. Goodbye!");
                            break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                }
            } catch (Exception e) {
                System.err.println("Error in main menu: " + e.getMessage());
                exit = true;
                close();
            }
        }
    }

    public void showSettingsMenu() {
        String[] options = {
                "Change Required Repetitions",
                "Toggle Invert Cards",
                "Back to Main Menu"
        };

        boolean back = false;
        while (!back) {
            System.out.println("\n===== SETTINGS =====");
            System.out.println("1. " + options[0] + " (Current: " + flashcard.getRequiredRepetitions() + ")");
            System.out.println("2. " + options[1] + " (Current: " + (flashcard.isInvertCards() ? "ON" : "OFF") + ")");
            System.out.println("3. " + options[2]);
            System.out.print("\nEnter your choice (1-" + options.length + "): ");

            try {
                int choice = Integer.parseInt(readInput());
                if (choice < 1 || choice > options.length) {
                    System.out.println("Invalid choice. Please enter a number between 1 and " + options.length + ".");
                    continue;
                }

                switch (choice) {
                    case 1:
                        changeRepetitions();
                        break;
                    case 2:
                        flashcard.toggleInvertCards();
                        System.out.println("Invert cards is now " + (flashcard.isInvertCards() ? "ON" : "OFF"));
                        break;
                    case 3:
                        back = true;
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private void changeRepetitions() {
        System.out.print("Enter new required repetitions (1 or more): ");
        try {
            int newReps = Integer.parseInt(readInput());
            if (newReps < 1) {
                System.out.println("Repetitions must be at least 1.");
            } else {
                flashcard.setRequiredRepetitions(newReps);
                System.out.println("Required repetitions updated to " + newReps);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    public void waitForEnter() {
        System.out.print("Press Enter to continue...");
        readInput();
    }

    public void close() {
        try {
            if (scanner != null) {
                scanner.close();
                scanner = null;
            }
            if (reader != null) {
                reader.close();
                reader = null;
            }
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}