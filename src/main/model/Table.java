package model;

import java.util.ArrayList;

public class Table {
    private int numPlayers;
    private ArrayList<Player> players;
    private ArrayList<Card> deck;
    private ArrayList<Card> usedCards;
    private ArrayList<Card> boardCards;




    public Table(Player p1, Player p2) {
        players = new ArrayList<>();
        deck = newDeck();
        usedCards = new ArrayList<>();
        boardCards = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        numPlayers = players.size();
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

    public ArrayList<Card> newDeck() {
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
        for (String s: suits) {
            for (String v: values) {
                Card card = new Card(v,s);
                deck.add(card);
            }
        }
        return deck;
    }

    public String boardCardsToString() {
        String board = "";
        for (int i = 0; i < boardCards.size(); i++) {
            board += this.boardCards.get(i).toString();
        }
        return board;
    }

    public ArrayList<Card> getBoardCards() {
        return boardCards;
    }

    // EFFECTS: returns odds of each player winning hand
    public void tableOdds() {
        players.get(0).compareHand(players.get(1));
    }

    public ArrayList<Card> getUsedCards() {
        return usedCards;
    }

//    public void setUsedCards(ArrayList<Card> usedCards) {
//        this.usedCards = usedCards;
//    }
}
