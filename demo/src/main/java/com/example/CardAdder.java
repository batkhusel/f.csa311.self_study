package com.example;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class CardAdder {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Error: No cards file specified.");
            System.out.println("Usage: CardAdder <cards-file>");
            System.exit(1);
        }

        String cardsFile = args[0];
        Scanner scanner = new Scanner(System.in);

        try (PrintWriter writer = new PrintWriter(new FileWriter(cardsFile, true))) {
            while (true) {
                System.out.println("\n=== Add New Flashcard ===");
                System.out.println("(Type 'exit' as the question to quit)");

                System.out.print("\nEnter question: ");
                String question = scanner.nextLine().trim();

                if (question.equalsIgnoreCase("exit")) {
                    break;
                }

                System.out.print("Enter answer: ");
                String answer = scanner.nextLine().trim();

                writer.println(question + "\t" + answer);
                System.out.println("Card added successfully!");
            }
        } catch (IOException e) {
            System.err.println("Error writing to cards file: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("Card adder utility completed. Exiting.");
        scanner.close();
    }
}