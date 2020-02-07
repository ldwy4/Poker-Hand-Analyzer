package ui;


import java.util.ArrayList;
import java.util.Scanner;

import model.Card;
import model.EquityCalculator;
import model.Player;
import model.Table;

public class InfoManager {
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

    public InfoManager(Table table) {
        input = new Scanner(System.in);
        runProgram = true;
        this.table = table;
        user = table.getPlayers().get(0);
        opponent = table.getPlayers().get(1);
        deck = table.getDeck();
        usedCards = table.getUsedCards();
        boardCards = table.getBoardCards();
        equityCalculator = new EquityCalculator(boardCards, table.getPlayers(), deck);
    }

    //EFFECTS: parses user input until user quits
    public void handleUserInput() {
        printInstructions();
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
                    System.out.println(user.toString());
                    System.out.println(opponent.toString());
                    handOptions();
                    break;
                case "no":
                    try {
                        randomHand();
                        System.out.println(user.toString());
                        System.out.println(opponent.toString());
                    } catch (Exception e) {
                        System.out.println("Ran out of Cards");
                    }
                    handOptions();
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

    // EFFECTS: asks user to input hand and sets user hand to inputed values
    private void chooseHand() {
        System.out.println("What numbers do you want? Enter two with space between(2-9,T,J,Q,K,A)");
        String str = getUserInputString();
        if (str.length() > 2) {
            String[] cards = str.split("\\s+");
            String cardValue1 = cards[0].toUpperCase();
            String cardValue2 = cards[1].toUpperCase();
            System.out.println("What suits?(S,C,D,H)");
            str = getUserInputString();
            if (str.length() > 2) {
                String[] suits = str.split("\\s+");
                String suit1 = suits[0].toUpperCase();
                String suit2 = suits[1].toUpperCase();
                boolean valid1 = validCard(cardValue1, suit1);
                boolean valid2 = validCard(cardValue2, suit2);
                if (valid1 && valid2) {
                    user.setHand(cardValue1, cardValue2, suit1, suit2);
                    removeCard(cardValue1, suit1);
                    removeCard(cardValue2, suit2);
                    chooseOpponentHand();
                }
            }
        }
    }

    //EFFECTS: assigns random hand to each player
    public void randomHand() {
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

    // EFFECT: produces true if card is a valid card
    private boolean validCard(String cv, String s) {
        for (int i = 0; i <= 12; i++) {
            if (deck.get(i).getValue().equals(cv)) {
                return (s.equals("S") || s.equals("H") || s.equals("D") || s.equals("C"));
            }
        }
        return false;
    }

    private void chooseOpponentHand() {
        System.out.println("Input opponents hand. With space between");
        String str = getUserInputString();
        if (str.length() > 2) {
            String[] cards = str.split("\\s+");
            String cardValue1 = cards[0].toUpperCase();
            String cardValue2 = cards[1].toUpperCase();
            System.out.println("What suits?(S,C,D,H)");
            str = getUserInputString();
            if (str.length() > 2) {
                String[] suits = str.split("\\s+");
                String suit1 = suits[0].toUpperCase();
                String suit2 = suits[1].toUpperCase();
                boolean valid1 = validCard(cardValue1, suit1);
                boolean valid2 = validCard(cardValue2, suit2);
                if (valid1 && valid2) {
                    opponent.setHand(cardValue1, cardValue2, suit1, suit2);
                    removeCard(cardValue1, suit1);
                    removeCard(cardValue2, suit2);
                }
            }
        }
    }

    // EFFECTS: removes card from deck once dealt
    public void removeCard(String cv, String s) {
        for (Card c: deck) {
            if (c.getValue().equals(cv) && c.getSuit().equals(s)) {
                deck.remove(c);
                usedCards.add(c);
                break;
            }
        }
    }

    // EFFECTS: prints options for users hand depending on input
    public void handOptions() {
        System.out.println("What do you want to do?");
        String str = getUserInputString();
        if (str.length() > 0) {
            switch (str) {
                case "odds":
                    table.tableOdds();
                    System.out.println(user.oddsToString());
                    System.out.println(opponent.oddsToString());
                    break;
                case "flop":
                    try {
                        addToBoard(3);
                        System.out.println(table.boardCardsToString());
                    } catch (Exception e) {
                        System.out.println("Ran out of Cards");
                    }
                    //table.tableOdds();
                default:
                    boardCards.clear();
                    printInstructions();
            }
        }
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


}
