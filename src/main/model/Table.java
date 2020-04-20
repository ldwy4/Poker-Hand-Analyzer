package model;

import persistence.Saveable;
import ui.CardsPanel;
import ui.ImageStore;

import java.awt.*;
import java.io.PrintWriter;

import java.util.ArrayList;

//Table contains all players and cards on the table, as well as cards left in the deck
public class Table extends Clickable implements Saveable {
    private int numPlayers;
    private String tableName;
    private ArrayList<Player> players;
    private ArrayList<Card> deck;
    private ArrayList<Card> usedCards;
    private ArrayList<Card> boardCards;
    private static final int TABLE_X = 510;
    private static final int TABLE_Y = 400;
    private static final int P1_X = 500;
    private static final int P2_X = 700;

    public Table(Player p1, Player p2) {
        players = new ArrayList<>();
        deck = newDeck();
        usedCards = new ArrayList<>();
        boardCards = new ArrayList<>(5);
        players.add(p1);
        players.add(p2);
        p1.setPosX(P1_X);
        p2.setPosX(P2_X);
        numPlayers = players.size();
        super.setPosX(TABLE_X);
        super.setPosY(TABLE_Y);
    }

    public int getNumPlayers() {
        return numPlayers;
    }

//    public void setNumPlayers(int numPlayers) {
//        this.numPlayers = numPlayers;
//    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

//    public void setPlayers(ArrayList<Player> players) {
//        this.players = players;
//    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

//    public void setDeck(ArrayList<Card> deck) {
//        this.deck = deck;
//    }

    //EFFECTS: creates new ArrayList containing all cards in new deck
    public static ArrayList<Card> newDeck() {
        ArrayList<Card> deck = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        ArrayList<String> suits = new ArrayList<>();
        for (int i = 2; i <= 9; i++) {
            values.add(Integer.toString(i));
        }
        values.add("T");
        values.add("J");
        values.add("Q");
        values.add("K");
        values.add("A");
        suits.add("S");
        suits.add("C");
        suits.add("D");
        suits.add("H");
        for (String s : suits) {
            for (String v : values) {
                Card card = new Card(v, s);
                deck.add(card);
            }
        }
        return deck;
    }

//    public void drawDeck(Graphics g) {
//        for (Card c: deck) {
//            c.draw(g);
//        }
//    }

    // EFFECTS: returns string of each Card in boardCards
    public String boardCardsToString() {
        String board = "";
        for (int i = 0; i < boardCards.size(); i++) {
            board += this.boardCards.get(i).toString();
        }
        return board;
    }

    //REQUIRES: card to be a valid card
    //EFFECTS: returns card with given value and suit
    public Card addCard(String cv, String s) {
        Card foundCard = new Card(cv, s); //dummy
        for (Card c : this.deck) {
            if (c.getValue().equals(cv) && c.getSuit().equals(s)) {
                foundCard = c;
                this.deck.remove(c);
                this.usedCards.add(c);
                break;
            }
        }
        return foundCard;
    }

    // EFFECTS: removes card from deck once dealt
    public void removeCard(String cv, String s) {
        for (Card c : this.deck) {
            if (c.getValue().equals(cv) && c.getSuit().equals(s)) {
                this.deck.remove(c);
                this.usedCards.add(c);
                break;
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: adds player to the table
    public void addPlayer(Player player) {
        players.add(player);
    }

    public ArrayList<Card> getBoardCards() {
        return boardCards;
    }

    // EFFECTS: returns odds of each player winning hand pre-flop
    public void preFlopTableOdds() {
        players.get(0).compareHand(players.get(1));
    }

    public ArrayList<Card> getUsedCards() {
        return usedCards;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public boolean containsX(int x) {
        return (this.posX <= x) && (x <= this.posX + 5 * CardsPanel.CARD_WIDTH);
    }

    // EFFECTS: allows table state to be written to file
    @Override
    public void save(PrintWriter printWriter) {
        String delimiter = ", ";
        for (Player p: players) {
            p.save(printWriter);
        }
        for (Card c : boardCards) {
            printWriter.print(c.getValue());
            printWriter.print(delimiter);
            printWriter.print(c.getSuit());
            printWriter.print(delimiter);
        }
        printWriter.print(tableName);
        printWriter.println();
    }

    //EFFECTS: draws the boardCards cards
    @Override
    public void draw(Graphics g) {
        int x = posX;
        for (int i = 0; i < 5; i++) {
            Image image;
            if (i >= boardCards.size()) {
                image = ImageStore.get().getImage("images/freeslot.png");
            } else {
                image = boardCards.get(i).getImage();
            }
            g.drawImage(image, x, posY, CardsPanel.CARD_WIDTH, CardsPanel.CARD_HEIGHT, null);
            x += 60;
        }
    }
}
