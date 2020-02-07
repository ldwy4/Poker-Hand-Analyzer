package model;

import java.util.ArrayList;
import java.util.Collection;

public class EquityCalculator {
    private ArrayList<Card> boardCards;
    private ArrayList<Card> deckLeft;
    private ArrayList<Player> players;
    private ArrayList<Integer> handValues; // raw value of high card in hand
    private ArrayList<Integer> handRanks; // hand rank in
    static final int HIGH = 0;
    static final int PAIR = 1;
    static final int TWO_PAIR = 2;
    static final int TRIP = 3;
    static final int STRAIGHT = 4;
    static final int FLUSH = 5;
    static final int FULL = 6;
    static final int FOUR = 7;
    static final int S_FLUSH = 8;
    static final int R_FLUSH = 9;

    public EquityCalculator(ArrayList<Card> boardCards, ArrayList<Player> players, ArrayList<Card> deckLeft) {
        this.boardCards = boardCards;
        this.players = players;
        this.deckLeft = deckLeft;
        handValues = new ArrayList<>();
        handRanks = new ArrayList<>();
    }

    //MODIFIES: this
    //EFFECTS: sets each players hand ranking
    public void setHandRankings() {
        for (Player p: players) {
            ArrayList<Card> hand = new ArrayList<>();
            for (Card c: boardCards) {
                hand.add(c);
            }
            hand.add(p.getFirstCard());
            hand.add(p.getSecondCard());
            p.setHandRank(setRank(hand));
            // TODO: add implementation for turn and river cards
        }
    }

    //EFFECTS: finds hand rank
    public int setRank(ArrayList<Card> hand) {
        if (isFlush(hand)) {
            return FLUSH;
        }

        //checks for 3 of kinds
        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                for (int k = j + 1; k < hand.size(); k++) {
                    int c1 = hand.get(i).getRawValue();
                    if (c1 == hand.get(j).getRawValue() && c1 == hand.get(k).getRawValue()) {
                        return TRIP;
                    }
                }
            }
        }

        //checks for pair
        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                int c1 = hand.get(i).getRawValue();
                if (c1 == hand.get(j).getRawValue()) {
                    return PAIR;
                }
            }
        }
        return HIGH;
    }

    private boolean isFlush(ArrayList<Card> hand) {
        ArrayList<String> suits = new ArrayList<>();
        int clubs = 0;
        int spades = 0;
        int hearts = 0;
        int diamonds = 0;

        for (Card c: hand) {
            switch (c.getSuit()) {
                case "S":
                    spades++;
                    break;
                case "C":
                    clubs++;
                    break;
                case "H":
                    hearts++;
                    break;
                case "D":
                    diamonds++;
                    break;
            }
        }
        return (spades >= 5 || clubs >= 5 || hearts >= 5 || diamonds >= 5);
    }

    //EFFECTS: gets high card of players best hand
//    public void getHandHighCard() {
//
//    }
}
