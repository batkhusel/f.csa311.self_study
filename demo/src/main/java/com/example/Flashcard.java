package com.example;

import com.example.flashcard.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Flashcard {
    private static final int DEFAULT_REPETITIONS = 1;
    private static final String DEFAULT_ORDER = "random";
    private static final boolean DEFAULT_INVERT_CARDS = false;

    private List<Card> cards;
    private String cardsFilePath;
    private CardOrganizer organizer;
    private int requiredRepetitions;
    private boolean invertCards;
    private String currentOrder;

    private MistakeTracker mistakeTracker;
    private AchievementManager achievementManager;

    private static String readInput() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = reader.readLine();
            if (line != null) {
                return line;
            }
        } catch (IOException e) {
            System.err.println("BufferedReader failed: " + e.getMessage());
        }

        try {
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNext()) {
                return scanner.nextLine();
            }
            scanner.close();
        } catch (Exception ex) {
            System.err.println("Scanner failed: " + ex.getMessage());
        }

        System.err.println("All input methods failed in Flashcard");
        return "";
    }

    public Flashcard(String cardsFile, String order, int repetitions, boolean invertCards) {
        this.cardsFilePath = cardsFile;
        this.cards = loadCards(cardsFile);
        this.currentOrder = order;
        this.organizer = createOrganizer(order);
        this.requiredRepetitions = repetitions;
        this.invertCards = invertCards;

        String baseFilePath = cardsFile.substring(0, cardsFile.lastIndexOf("."));
        this.mistakeTracker = new MistakeTracker(baseFilePath + "_stats.dat");
        this.achievementManager = new AchievementManager(baseFilePath + "_achievements.dat");
    }

    private List<Card> loadCards(String cardsFile) {
        List<Card> loadedCards = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(cardsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 2) {
                    loadedCards.add(new Card(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading cards: " + e.getMessage());
            System.exit(1);
        }
        return loadedCards;
    }

    private CardOrganizer createOrganizer(String order) {
        switch (order) {
            case "random":
                return new RandomSorter();
            case "worst-first":
                return new WorstFirstSorter();
            case "recent-mistakes-first":
                return new RecentMistakesFirstSorter();
            default:
                System.err.println("Invalid order: " + order + ". Using random order.");
                return new RandomSorter();
        }
    }

    public void run() {
        FlashcardUI ui = new FlashcardUI(this);
        try {
            ui.showMainMenu();
        } finally {
            ui.close();
        }
    }

    public void studyCards() {
        boolean allCardsCompleted = false;
        boolean hasStudiedAnyCards = false;

        while (!allCardsCompleted) {
            List<Card> organizedCards = organizer.organize(cards);
            long totalTime = 0;
            boolean allCorrectThisRound = true;
            boolean studiedCardsThisRound = false;

            System.out.println("\n===== NEW ROUND =====\n");

            for (Card card : organizedCards) {
                if (card.getCorrectAnswers() >= requiredRepetitions)
                    continue;

                studiedCardsThisRound = true;
                hasStudiedAnyCards = true;

                String question = invertCards ? card.getAnswer() : card.getQuestion();
                String answer = invertCards ? card.getQuestion() : card.getAnswer();

                System.out.println("Question: " + question);
                System.out.print("Your answer: ");

                long startTime = System.currentTimeMillis();
                String userAnswer = readInput().trim();
                long timeSpent = System.currentTimeMillis() - startTime;

                boolean isCorrect = userAnswer.equalsIgnoreCase(answer);
                card.recordAttempt(isCorrect, timeSpent);

                if (!isCorrect) {
                    mistakeTracker.recordMistake(card);
                    System.out.println("Incorrect. The correct answer is: " + answer);
                    allCorrectThisRound = false;
                } else {
                    System.out.println("Correct! Time: " + (timeSpent / 1000.0) + " seconds");
                }

                totalTime += timeSpent;
                System.out.println();

                achievementManager.checkAchievements(card, allCorrectThisRound, totalTime, organizedCards.size());
            }

            if (!studiedCardsThisRound) {
                if (hasStudiedAnyCards) {
                    // We've studied some cards in previous rounds but none this round
                    System.out.println("All cards have reached the required " + requiredRepetitions +
                            " repetition(s)! Great job!");
                } else {
                    // We haven't studied any cards at all
                    System.out.println("No cards to study! The required repetition count is " + requiredRepetitions +
                            ". You may need to add cards or reset your progress.");
                }
                allCardsCompleted = true;
            } else {
                displayAchievements();

                // Check if all cards have now reached the required repetitions
                allCardsCompleted = true;
                for (Card card : cards) {
                    if (card.getCorrectAnswers() < requiredRepetitions) {
                        allCardsCompleted = false;
                        break;
                    }
                }

                if (!allCardsCompleted) {
                    System.out.print("\nPress Enter to continue or type 'menu' to return: ");
                    if (readInput().trim().equalsIgnoreCase("menu"))
                        return;
                } else {
                    System.out.println("\nCongratulations! You have completed all cards.");
                }
            }
        }
    }

    private void displayAchievements() {
        System.out.println("\n===== ACHIEVEMENTS =====");
        for (Achievement achievement : achievementManager.getAchievements()) {
            if (achievement.isEarned()) {
                System.out.println(achievement);
            }
        }
    }

    public void addNewCard() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(cardsFilePath, true))) {
            System.out.println("\n===== ADD NEW FLASHCARD =====");

            System.out.print("\nEnter question: ");
            String question = readInput().trim();

            System.out.print("Enter answer: ");
            String answer = readInput().trim();

            // Write and add card
            writer.println(question + "\t" + answer);
            cards.add(new Card(question, answer));

            System.out.println("Card added successfully!");
        } catch (IOException e) {
            System.err.println("Error writing to cards file: " + e.getMessage());
        }
    }

    public void viewRecentMistakes() {
        List<MistakeTracker.MistakeEntry> recentMistakes = mistakeTracker.getRecentMistakes(10);

        if (recentMistakes.isEmpty()) {
            System.out.println("\nNo recent mistakes found.");
            return;
        }

        System.out.println("\n===== RECENT MISTAKES =====");
        System.out.println("Your 10 most recent mistakes:");
        System.out.println();

        for (int i = 0; i < recentMistakes.size(); i++) {
            MistakeTracker.MistakeEntry entry = recentMistakes.get(i);
            System.out.println((i + 1) + ". Q: " + entry.getQuestion());
            System.out.println("   A: " + entry.getAnswer());
            System.out.println("   Date: " + entry.getFormattedDate());
            System.out.println();
        }

        System.out.print("Press Enter to continue...");
        readInput();
    }

    public void viewMostFrequentMistakes() {
        List<Map.Entry<String, Integer>> frequentMistakes = mistakeTracker.getMostFrequentMistakes(10);

        if (frequentMistakes.isEmpty()) {
            System.out.println("\nNo mistake history found.");
            return;
        }

        System.out.println("\n===== MOST FREQUENT MISTAKES =====");
        System.out.println("Your most frequently mistaken cards:");
        System.out.println();

        for (int i = 0; i < frequentMistakes.size(); i++) {
            Map.Entry<String, Integer> entry = frequentMistakes.get(i);
            String question = entry.getKey();
            int count = entry.getValue();

            // Find the answer
            String answer = "";
            for (Card card : cards) {
                if (card.getQuestion().equals(question)) {
                    answer = card.getAnswer();
                    break;
                }
            }

            System.out.println((i + 1) + ". Q: " + question);
            System.out.println("   A: " + answer);
            System.out.println("   Mistake count: " + count);
            System.out.println();
        }

        System.out.print("Press Enter to continue...");
        readInput();
    }

    public void changeCardOrder() {
        System.out.println("\n===== CHANGE CARD ORDER =====");
        System.out.println("Current order: " + currentOrder);
        System.out.println("Available orders:");
        System.out.println("1. random - Cards appear in random order");
        System.out.println("2. worst-first - Cards with worst performance appear first");
        System.out.println("3. recent-mistakes-first - Cards answered incorrectly appear first");
        System.out.print("\nEnter your choice (1-3): ");

        try {
            int choice = Integer.parseInt(readInput().trim());

            switch (choice) {
                case 1:
                    currentOrder = "random";
                    organizer = new RandomSorter();
                    System.out.println("Card order changed to random.");
                    break;
                case 2:
                    currentOrder = "worst-first";
                    organizer = new WorstFirstSorter();
                    System.out.println("Card order changed to worst-first.");
                    break;
                case 3:
                    currentOrder = "recent-mistakes-first";
                    organizer = new RecentMistakesFirstSorter();
                    System.out.println("Card order changed to recent-mistakes-first.");
                    break;
                default:
                    System.out.println("Invalid choice. Card order remains unchanged.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Card order remains unchanged.");
        }
    }

    public void viewAchievements() {
        System.out.println("\n===== ALL ACHIEVEMENTS =====");
        System.out.println("Your progress towards achievements:");
        System.out.println();

        List<Achievement> achievements = achievementManager.getAchievements();
        for (int i = 0; i < achievements.size(); i++) {
            Achievement achievement = achievements.get(i);
            System.out.println((i + 1) + ". " + achievement.getName() + ": " + achievement.getDescription());
            System.out.println("   Status: " + (achievement.isEarned() ? "EARNED" : "NOT EARNED"));

            if (achievement.isEarned()) {
                switch (achievement.getName()) {
                    case "SPEED":
                        System.out.println("   Tip: Try to answer questions faster to earn this achievement.");
                        break;
                    case "CORRECT":
                        System.out.println("   Tip: Study the cards well before testing yourself.");
                        break;
                    case "REPEAT":
                        int maxAttempts = cards.stream().mapToInt(Card::getTotalAttempts).max().orElse(0);
                        System.out.println("   Progress: Your highest card attempt count is " + maxAttempts + "/5");
                        break;
                    case "CONFIDENT":
                        int maxCorrect = cards.stream().mapToInt(Card::getCorrectAnswers).max().orElse(0);
                        System.out.println("   Progress: Your highest correct answer count is " + maxCorrect + "/3");
                        break;
                }
            }
            System.out.println();
        }

        System.out.print("Press Enter to continue...");
        readInput();
    }

    public void displayHelp() {
        showHelp();
        System.out.print("\nPress Enter to return to the main menu...");
        readInput();
    }

    private void showHelp() {
        System.out.println("\n===== FLASHCARD SYSTEM HELP =====");

        System.out.println("\nMenu Options:");
        System.out.println("  1. Study Cards           - Practice flashcards");
        System.out.println("  2. Add New Card          - Add to collection");
        System.out.println("  3. View Recent Mistakes  - See incorrect answers");
        System.out.println("  4. Most Frequent Mistakes - View problem areas");
        System.out.println("  5. Change Card Order     - Adjust presentation");
        System.out.println("  6. View Achievements     - Track learning progress");
        System.out.println("  7. Settings              - Adjust preferences");
        System.out.println("  8. Help                  - Display help");
        System.out.println("  9. Exit                  - Close application");
    }

    public int getRequiredRepetitions() {
        return requiredRepetitions;
    }

    public void setRequiredRepetitions(int repetitions) {
        this.requiredRepetitions = repetitions;
    }

    public boolean isInvertCards() {
        return invertCards;
    }

    public void toggleInvertCards() {
        this.invertCards = !invertCards;
    }

    public static void main(String[] args) {
        String cardsFile;
        String order = DEFAULT_ORDER;
        int repetitions = DEFAULT_REPETITIONS;
        boolean invertCards = DEFAULT_INVERT_CARDS;
        boolean showHelp = false;

        if (args.length < 1) {
            cardsFile = "src/main/java/com/example/cards.txt";
        } else {
            cardsFile = args[0];

            for (int i = 1; i < args.length; i++) {
                switch (args[i]) {
                    case "--help":
                        showHelp = true;
                        break;
                    case "--order":
                        if (i + 1 < args.length) {
                            order = args[++i];
                        }
                        break;
                    case "--repetitions":
                        if (i + 1 < args.length) {
                            try {
                                repetitions = Integer.parseInt(args[++i]);
                            } catch (NumberFormatException e) {
                                System.err.println("Error: Invalid repetitions value");
                            }
                        }
                        break;
                    case "--invertCards":
                        invertCards = true;
                        break;
                }
            }
        }

        Flashcard flashcard = new Flashcard(cardsFile, order, repetitions, invertCards);

        if (showHelp) {
            flashcard.showHelp();
        } else {
            flashcard.run();
        }
    }
}