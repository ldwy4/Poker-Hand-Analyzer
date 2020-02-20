package ui;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;

import model.Card;
import model.EquityCalculator;
import model.Player;
import model.Table;
import persistence.Reader;
import persistence.Writer;

public class InfoManager {
    private static final String HAND_FILE = "./data/hands.txt";
    private static final String QUIT_COMMAND = "quit";
    private Table table;
    private Scanner input;
    private boolean runProgram;
    Player user;
    Player opponent;
    ArrayList<Card> deck;
    ArrayList<Card> usedCards;
    ArrayList<Card> boardCards;
    EquityCalculator equityCalculator;

    public InfoManager() {
        reset();
    }

    //EFFECTS: parses user input until user quits
    public void handleUserInput() {
        String str;
        while (runProgram) {
            str = getUserInputString();
            parseInput(str);
        }
    }

    //EFFECTS: stops receiving user input
    public void endProgram() {
        System.out.println("Quitting...");
        input.close();
    }

    //EFFECTS: prints instructions to use kiosk
    private void printInstructions() {
        System.out.println("\nDo you want to set the hands?");
    }

    //EFFECTS: prints menu options and info depending on input str
    private void parseInput(String str) {
        if (str.length() > 0) {
            switch (str) {
                case "yes":
                    chooseHand();
                    printPreFlopOdds();
                    break;
                case "no":
                    randomHand();
                    printPreFlopOdds();
                    break;
                case QUIT_COMMAND:
                    runProgram = false;
                    endProgram();
                    break;
                default:
                    System.out.println("Sorry, I didn't understand that command. Please try again.");
                    break;
            }
        }
    }

    //EFFECTS: prints odds before cards are on the board
    private void printPreFlopOdds() {
        table.tableOdds();
        System.out.println(user.toString());
        System.out.println(opponent.toString());
        handOptions();
    }

    //MODIFIES: user, table
    // EFFECTS: asks user to input hand and sets user hand to inputed values
    private void chooseHand() {
        System.out.println("Type in Cards to add (i.e. 9D, AC):");
        String str = getUserInputString();
        if (str.length() == 5) {
            String[] cards = str.split("\\s+");
            if (cards.length == 2) {
                for (int i = 0; i < 2; i++) {
                    String card = cards[i].toUpperCase();
                    String value = Character.toString(card.charAt(0));
                    String suit = Character.toString(card.charAt(1));
                    if (table.validCard(value, suit)) {
                        if (user.getFirstCard() == null) {
                            user.setFirstCard(table.addCard(value, suit));
                        } else {
                            user.setSecondCard(table.addCard(value, suit));
                        }
                        if (opponent.getFirstCard() == null) {
                            chooseOpponentHand();
                        }
                    }
                }
            }
            System.out.println("Invalid input");
        }
    }

    //EFFECTS: assigns random hand to each player
    private void randomHand() {
        int i = 0;
        while (i < 2) {
            //set user hand
            int seed = (int) (Math.random() * deck.size());
            Card card1 = deck.get(seed);
            usedCards.add(deck.get(seed));
            deck.remove(seed);
            seed = (int) (Math.random() * deck.size());
            Card card2 = deck.get(seed);
            usedCards.add(deck.get(seed));
            deck.remove(seed);
            if (i == 0) {
                user.setHand(card1.getValue(), card2.getValue(), card1.getSuit(), card2.getSuit());
            } else {
                opponent.setHand(card1.getValue(), card2.getValue(), card1.getSuit(), card2.getSuit());
            }
            i++;
        }
    }

    //EFFECTS: choose opponents hand
    private void chooseOpponentHand() {
        System.out.println("Type in Cards to add (i.e. 9D, AC):");
        String str = getUserInputString();
        if (str.length() == 5) {
            String[] cards = str.split("\\s+");
            try {
                for (int i = 0; i < 2; i++) {
                    String card = cards[i].toUpperCase();
                    String value = Character.toString(card.charAt(0));
                    String suit = Character.toString(card.charAt(1));
                    if (table.validCard(value, suit)) {
                        if (opponent.getFirstCard() == null) {
                            opponent.setFirstCard(table.addCard(value, suit));
                        } else {
                            opponent.setSecondCard(table.addCard(value, suit));
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Please enter 2 cards (i.e. 9D, AC)");
            }
        }
    }

    //EFFECTS: prints options for users hand depending on input
    private void handOptions() {
        System.out.println("What do you want to do?\nOptions: odds, next, add, new, change");
        String str = getUserInputString();
        if (str.length() > 0) {
            if (str.equals("next")) {
                autoFlop();
            } else if (str.equals("new")) {
                reset();
            } else if (str.equals("add")) {
                manualFlop();
            } else if (str.equals("change")) {
                chooseCardChange();
            } else if (str.equals("save")) {
                saveHand();
            } else if (str.equals("load")) {
                loadHand();
            } else if (str.equals(QUIT_COMMAND)) {
                quitApp();
            } else {
                handOptions();
            }
        }
    }

    public void quitApp() {
        runProgram = false;
        endProgram();
    }

    //EFFECTS: asks user what cards they want to change
    private void chooseCardChange() {
        boolean done = true;
        while (done) {
            System.out.println("What card(s) do you want to change?\n(user, opponent, flop, turn, river)");
            String str = getUserInputString();
            if (str.length() > 0) {
                done = !findCardChange(str);
            }
            if (str.equals("quit")) {
                done = false;
            }
        }
        handOptions();
    }


    //EFFECTS: calls method that allow user to change card in desired position
    private boolean findCardChange(String change) {
        switch (change) {
            case "user":
                newHand(user);
                return true;
            case "opponent":
                newHand(opponent);
                return true;
            case "flop":
                changeBoard(3);
                return true;
            case "turn":
                changeBoard(4);
                return true;
            case "river":
                changeBoard(5);
                return true;
            default:
                return false;
        }
    }

    //MODIFIES: p
    //EFFECTS: allows user to enter new hand for given player
    public void newHand(Player p) {
        deck.add(p.getFirstCard());
        deck.add(p.getSecondCard());
        usedCards.remove(p.getFirstCard());
        usedCards.remove(p.getSecondCard());
        p.setFirstCard(null);
        p.setSecondCard(null);
        if (p.getName() == "user") {
            chooseHand();
        } else {
            chooseOpponentHand();
        }
        printPreFlopOdds();
    }

    //MODIFIES: table
    //EFFECTS: changes hand of given player
    public void changeBoard(int position) {
        System.out.println("Enter new cards");
        if (position == 3) {
            String str = getUserInputString();
            for (int i = 0; i < 3; i++) {
                Card c = boardCards.get(i);
                deck.add(c);
                usedCards.remove(c);
            }
            String[] cards = str.split("\\s+");

            for (int i = 0; i < position; i++) {
                String card = cards[i].toUpperCase();
                String value = Character.toString(card.charAt(0));
                String suit = Character.toString(card.charAt(1));
                if (table.validCard(value, suit)) {
                    boardCards.set(i, table.addCard(value, suit));
                }
            }
        } else {
            changePostFlop(position);
        }
        printPostFlopOdds();
    }

    //EFFECTS: changes turn or river card based on given position
    public void changePostFlop(int position) {
        String str = getUserInputString().toUpperCase();
        Card c = boardCards.get(position - 1);
        deck.add(c);
        usedCards.remove(c);
        String value = Character.toString(str.charAt(0));
        String suit = Character.toString(str.charAt(1));
        if (table.validCard(value, suit)) {
            boardCards.set(position - 1, table.addCard(value, suit));
        }
    }

    //EFFECTS: automatically draws a card from the deck and adds it to the board
    public void autoFlop() {
        if (boardCards.size() == 0) {
            addToBoard(3);
        } else if (boardCards.size() < 5) {
            addToBoard(1);
        } else {
            System.out.println("Board is full");
        }
        printPostFlopOdds();
        handOptions();
    }

    //EFFECTS: asks user to manually enter a card to add to the board
    public void manualFlop() {
        if (boardCards.size() == 0) {
            manualAddToBoard(3);
        } else if (boardCards.size() < 5) {
            manualAddToBoard(1);
        } else {
            System.out.println("Board is full");
        }
        printPostFlopOdds();
        handOptions();
    }

    //EFFECTS: prints the odds of each player at the table
    private void printPostFlopOdds() {
        System.out.println(table.boardCardsToString());
        equityCalculator.setHandRankings();
        System.out.println(table.postFlopTableOdds());
    }

    //EFFECTS: randomly generates the flop (3 cards are added to board cards)
    public void addToBoard(int numCards) {
        int i = 0;
        while (i < numCards) {
            int seed = (int) (Math.random() * deck.size());
            boardCards.add(deck.get(seed));
            usedCards.add(deck.get(seed));
            deck.remove(seed);
            i++;
        }
    }

    //EFFECTS: Allows player to add to board
    public void manualAddToBoard(int numCards) {
        System.out.println("Type in Cards to add (i.e. 9D, AC):");
        String str = getUserInputString();
        if (str.length() >= numCards * 2) {
            String[] cards = str.split("\\s+");
            try {
                for (int i = 0; i < numCards; i++) {
                    String card = cards[i].toUpperCase();
                    String value = Character.toString(card.charAt(0));
                    String suit = Character.toString(card.charAt(1));
                    if (table.validCard(value, suit)) {
                        boardCards.add(table.addCard(value, suit));
                    }
                }
            } catch (Exception e) {
                System.out.println("Please enter " + numCards + "cards (i.e. 9D, AC)");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: loads hand from ACCOUNTS_FILE, if that file exists;
    private void loadHand() {
        System.out.println("What table do you want to load?");
        String str = getUserInputString();
        try {
            table = Reader.readHands(new File(HAND_FILE), str);
            user = table.getPlayers().get(0);
            user = table.getPlayers().get(1);
            deck = table.getDeck();
            usedCards = table.getUsedCards();
            boardCards = table.getBoardCards();
            equityCalculator = new EquityCalculator(boardCards, table.getPlayers(), deck);
            if (boardCards.size() == 0) {
                printPreFlopOdds();
            } else {
                printPostFlopOdds();
            }
            handOptions();
        } catch (IOException e) {
            reset();
        }
    }

    // EFFECTS: saves state of player and opponent hand and deck to HANDS_FILE
    public void saveHand() {
        System.out.println("What do you want to name the table?");
        String str = getUserInputString();
        try {
            table.setTableName(str);
            Writer writer = new Writer(new File(HAND_FILE));
            writer.write(user);
            writer.write(opponent);
            writer.write(table);
            writer.close();
            System.out.println("Hand has been saved to file " + HAND_FILE);
            System.out.println("Hand name: " + table.getTableName());
        } catch (FileNotFoundException e) {
            System.out.println("Unable to save hand to " + HAND_FILE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        handOptions();
    }

    //EFFECTS: removes white space and quotation marks around s
    private String makePrettyString(String s) {
        s = s.toLowerCase();
        s = s.trim();
        s = s.replaceAll("\"|\'", "");
        return s;
    }

    private String getUserInputString() {
        String str = "";
        if (input.hasNext()) {
            str = input.nextLine();
            str = makePrettyString(str);
        }
        return str;
    }

    //EFFECTS: creates a new table with new players and deck
    private void reset() {
        input = new Scanner(System.in);
        runProgram = true;
        user = new Player("user");
        opponent = new Player("opponent");
        table = new Table(user, opponent);
        deck = table.getDeck();
        usedCards = table.getUsedCards();
        boardCards = table.getBoardCards();
        equityCalculator = new EquityCalculator(boardCards, table.getPlayers(), deck);
        printInstructions();
    }

}
