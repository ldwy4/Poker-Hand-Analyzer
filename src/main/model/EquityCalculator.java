package model;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;

//Calculates what rank to give each players hand
public class EquityCalculator {
    private ArrayList<Card> boardCards;
    private ArrayList<Card> deckLeft;
    private ArrayList<Card> usedCards;
    private ArrayList<Player> players;
    private ArrayList<Integer> handValues; // raw value of high card in hand
    private ArrayList<Integer> handRanks; // hand rank in
    private int splitOuts;
    private float splitOdds;
    int counter = 0;
    static final int HIGH = 0;
    static final int PAIR = 1;
    static final int TWO_PAIR = 2;
    static final int TRIP = 3;
    static final int STRAIGHT = 4;
    static final int FLUSH = 5;
    static final int FULL = 6;
    static final int QUADS = 7;
    static final int S_FLUSH = 8;
    static final int R_FLUSH = 9;

    public EquityCalculator(ArrayList<Card> boardCards, ArrayList<Player> players, ArrayList<Card> deckLeft) {
        this.boardCards = boardCards;
        this.players = players;
        this.deckLeft = deckLeft;
        handValues = new ArrayList<>();
        handRanks = new ArrayList<>();
        usedCards = new ArrayList<>();
        splitOdds = 0;
        splitOuts = 0;
    }

    //MODIFIES: Players p: players
    //EFFECTS: sets each players hand ranking
    public void setHandRankings() {
        for (Player p: players) {
            p.getHand().clear();
            for (Card c: boardCards) {
                p.getHand().add(c);
            }
            p.getHand().add(p.getFirstCard());
            p.getHand().add(p.getSecondCard());
            p.setHandRank(setRank(p.getHand(), p));
        }
        calculateOdds();
    }

    //EFFECTS: compares hand rank of one hand to the other
//    public Player compareHands() {
//        int hr1 = players.get(0).getHankRank();
//        int hr2 = players.get(1).getHankRank();
//        int diff = hr1 - hr2;
//        if (diff > 0) {
//            return players.get(1);
//        } else if (diff < 0) {
//            return players.get(0);
//        } else {
//            if (players.get(0).getHandValue() > players.get(1).getHandValue()) {
//                return players.get(1);
//            } else {
//                return players.get(0);
//            }
//        }
//    }

    //MODIFIES: Player p: players
    //EFFECTS: calculates odds of each player winning
    public void calculateOdds() {
        if (boardCards.size() == 5) {
            ArrayList<Player> topPlayers = new ArrayList<>();
            int max = 0;
            for (Player p: players) {
                max = Math.max(max, p.getHankRank());
            }
            for (Player p: players) {
                if (p.getHankRank() == max) {
                    topPlayers.add(p);
                }
            }
            if (topPlayers.size() == 1) {
                for (Player p: players) {
                    if (p == topPlayers.get(0)) {
                        p.setOdds(100);
                    } else {
                        p.setOdds(0);
                    }
                }
            }
        } else {
            getOuts();
        }
    }

    //MODIFIES: bp, ap
    //EFFECTS: counts the number of cards that will make this player have the winning hand
    public void getOuts() {
//        int[] handRanks = new int[players.size()];
//        for (int i = 0; i < players.size(); i++) {
//            handRanks[i] = players.get(i).getHankRank();
//        }
        countOuts();
        usedCards.clear();
//        for (int i = 0; i < players.size(); i++) {
//            players.get(i).setHandRank(handRanks[i]);
//        }
        if (players.get(0).getHand().size() == 5) {
            splitOdds = 100 * splitOuts / ((float) (deckLeft.size() * (deckLeft.size() - 1)) / 2);
            for (Player p: players) {
                p.setOdds(p.getOuts() / ((float) (deckLeft.size() * (deckLeft.size() - 1)) / 2));
            }
        } else {
            splitOdds = 100 * splitOuts / (float) deckLeft.size();
            for (Player p : players) {
                p.setOdds(p.getOuts() / (float) deckLeft.size());
            }
        }
    }

    //EFFECTS: counts the outs for the behind player
    public void countOuts() {
        for (Card c: deckLeft) {
            if (usedCards.contains(c)) {
                continue;
            }
            for (Player p: players) {
                p.getHand().add(c);
            }
            if (players.get(0).getHand().size() < 7) {
                usedCards.add(c);
                countOuts();
            } else {
                compareNewHands();
                counter++;
            }
            for (Player p: players) {
                p.getHand().remove(c);
            }
        }
    }

    private void compareNewHands() {
        ArrayList<Integer> newRanks = new ArrayList();
        for (Player p: players) {
            newRanks.add(setRank(p.getHand(), p));
        }
        int max = Collections.max(newRanks);
        for (int j = 0; j < players.size(); j++) {
            int sameValue = 0;
            for (int i = 0; i < players.size(); i++) {
                if (newRanks.get(j) == max) {
                    if (newRanks.get(i) == max && i != j) {
                        if (players.get(i).getHandValue() > players.get(j).getHandValue()) {
                            break;
                        } else if (players.get(i).getHandValue() == players.get(j).getHandValue() && players.get(i).getKickerValue() > players.get(j).getKickerValue()) {
                            break;
                        } else if (players.get(i).getHandValue() == players.get(j).getHandValue() && players.get(i).getKickerValue() == players.get(j).getKickerValue()) {
                            sameValue++;
                        }
                    }
                    if (i == players.size() - 1) {
                        if (sameValue == 0) {
                            players.get(j).addOut();
                        }
                    }
                }
            }
            if (sameValue != 0 && j == players.size() - 1) {
                splitOuts++;
            }
        }
    }

    //MODIFIES: p
    //EFFECTS: finds hand rank of hand and returns it
    public int setRank(ArrayList<Card> hand, Player p) {
        if (isRoyalFlush(hand, p)) {
            return R_FLUSH;
        } else if (isStraightFlush(hand, p)) {
            return S_FLUSH;
        } else if (isQuads(hand, p)) {
            return QUADS;
        } else if (isFullHouse(hand, p)) {
            return FULL;
        } else if (isFlush(hand, p)) {
            return FLUSH;
        } else if (isStraight(hand, p)) {
            return STRAIGHT;
        } else if (isTrips(hand, p)) {
            return TRIP;
        } else if (isTwoPair(hand, p)) {
            return TWO_PAIR;
        } else if (isPair(hand, p)) {
            return PAIR;
        }
        p.setHandValue(Math.max(p.getFirstCard().getRawValue(), p.getSecondCard().getRawValue()));
        p.setKickerValue(Math.min(p.getFirstCard().getRawValue(), p.getSecondCard().getRawValue()));
        return HIGH;
    }

    //EFFECTS: returns true if hand contains royal flush
    public boolean isRoyalFlush(ArrayList<Card> hand, Player p) {
        Collections.sort(hand);
        if (isStraightFlush(hand, p)) {
            if (p.getHandValue() == 14) {
                return true;
            }
        }
        return false;
    }

    //EFFECTS: returns true if hand contains straight-flush
    public boolean isStraightFlush(ArrayList<Card> hand, Player p) {
        if (suitFlush(hand, p, "S").getValue()) {
            return isStraight(suitFlush(hand, p, "S").getKey(), p);
        } else if (suitFlush(hand, p, "D").getValue()) {
            return isStraight(suitFlush(hand, p, "D").getKey(), p);
        } else if (suitFlush(hand, p, "H").getValue()) {
            return isStraight(suitFlush(hand, p, "H").getKey(), p);
        } else if (suitFlush(hand, p, "C").getValue()) {
            return isStraight(suitFlush(hand, p, "C").getKey(), p);
        }
        return false;
    }

    //MODIFIES: p
    //EFFECTS: returns true if hand contains four of a kind
    public boolean isQuads(ArrayList<Card> hand, Player p) {
        for (int i = 0; i < hand.size(); i++) {
            int numSameCard = 1;
            int cv1 = hand.get(i).getRawValue();
            for (int j = i + 1; j < hand.size(); j++) {
                int cv2 = hand.get(j).getRawValue();
                if (cv1 == cv2) {
                    numSameCard++;
                }
            }
            if (numSameCard == 4) {
                p.setHandValue(cv1);
                p.setKickerValue(0);
                return true;
            }
        }
        return false;
    }

    //MODIFIES: p
    //EFFECTS: returns true if hand contains a full hull
    public boolean isFullHouse(ArrayList<Card> hand, Player p) {
        for (int i = hand.size() - 1; i >= 0; i--) {
            for (int j = i - 1; j >= 0; j--) {
                int c1 = hand.get(i).getRawValue();
                if (isTrips(hand, p) && c1 == hand.get(j).getRawValue() && c1 != p.getHandValue()) {
                    p.setKickerValue(c1);
                    return true;
                }
            }
        }
        return false;
    }

    //EFFECTS: returns true if hand contains a flush
    public boolean isFlush(ArrayList<Card> hand, Player p) {
        if (suitFlush(hand, p, "S").getValue()) {
            return true;
        } else if (suitFlush(hand, p, "D").getValue()) {
            return true;
        } else if (suitFlush(hand, p, "H").getValue()) {
            return true;
        } else if (suitFlush(hand, p, "C").getValue()) {
            return true;
        }
        return false;
    }

    //MODIFIES: p
    //EFFECTS: returns true if hand contains a flush of clubs
    public Pair<ArrayList<Card>, Boolean> suitFlush(ArrayList<Card> hand, Player p, String suit) {
        ArrayList<Card> suits = new ArrayList<>();
        for (Card c: hand) {
            if (c.getSuit() == suit) {
                suits.add(c);
            }
        }
        if (suits.size() >= 5) {
            Collections.sort(suits);
            p.setHandValue(suits.get(suits.size() - 1).getRawValue());
            p.setKickerValue(suits.get(suits.size() - 2).getRawValue());
            return new Pair<>(suits, true);
        }
        return new Pair<>(suits, false);
    }

    //MODIFIES: p
    //EFFECTS: returns true if hand contains straight
    public boolean isStraight(ArrayList<Card> hand, Player p) {
        Collections.sort(hand);
        for (int i = hand.size() - 1; i >= 0; i--) {
            ArrayList<Card> straightCards = new ArrayList<>();
            straightCards.add(hand.get(i));
            int value1 = hand.get(i).getRawValue();
            for (int j = i - 1; j >= 0; j--) {
                int value2 = hand.get(j).getRawValue();
                boolean add = false;
                if (Math.abs(value1 - value2) == 1) {
                    add = true;
                    straightCards.add(hand.get(j));
                    value1 = value2;
                }
            }
            if (straightCards.size() >= 5) {
                p.setHandValue(straightCards.get(0).getRawValue());
                p.setKickerValue(straightCards.get(1).getRawValue());
                return true;
            }
        }
        return false;
    }

    //MODIFIES: p
    //EFFECTS: returns true if hand contains three of a kind
    public boolean isTrips(ArrayList<Card> hand, Player p) {
        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                for (int k = j + 1; k < hand.size(); k++) {
                    int c1 = hand.get(i).getRawValue();
                    if (c1 == hand.get(j).getRawValue() && c1 == hand.get(k).getRawValue()) {
                        p.setHandValue(c1);
                        findKicker(p, c1);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //MODIFIES: p
    //EFFECTS: returns true if hand contains two pair
    public boolean isTwoPair(ArrayList<Card> hand, Player p) {
        for (int i = hand.size() - 1; i >= 0; i--) {
            for (int j = i - 1; j >= 0; j--) {
                int c2 = hand.get(i).getRawValue();
                if (isPair(hand, p) && c2 != p.getHandValue() && c2 == hand.get(j).getRawValue()) {
//                    p.setKickerValue(p.getHandValue());
//                    p.setHandValue(c2);
                    p.setKickerValue(c2);
                    return true;
                }
            }
        }
        return false;
    }

    //MODIFIES: p
    //EFFECTS: returns true if hand contains a pair
    public boolean isPair(ArrayList<Card> hand, Player p) {
        for (int i = hand.size() - 1; i >= 0; i--) {
            for (int j = i - 1; j >= 0; j--) {
                int c1 = hand.get(i).getRawValue();
                if (c1 == hand.get(j).getRawValue()) {
                    p.setHandValue(c1);
                    findKicker(p, c1);
                    return true;
                }
            }
        }
        return false;
    }

    public void findKicker(Player p, int inHand) {
        int c1 = p.getFirstCard().getRawValue();
        int c2 = p.getSecondCard().getRawValue();
        if (c1 == inHand) {
            p.setKickerValue(c2);
        } else if (c2 == inHand) {
            p.setKickerValue(c1);
        } else {
            p.setKickerValue(Math.max(c1, c2));
        }
    }

    public ArrayList<Card> getBoardCards() {
        return boardCards;
    }

    public ArrayList<Card> getDeckLeft() {
        return deckLeft;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Integer> getHandValues() {
        return handValues;
    }

    public ArrayList<Integer> getHandRanks() {
        return handRanks;
    }

    public float getSplitOdds() {
        return splitOdds;
    }

}
