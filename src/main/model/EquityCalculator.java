package model;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

//Calculates what rank to give each players hand
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
    static final int QUADS = 7;
    static final int S_FLUSH = 8;
    static final int R_FLUSH = 9;

    public EquityCalculator(ArrayList<Card> boardCards, ArrayList<Player> players, ArrayList<Card> deckLeft) {
        this.boardCards = boardCards;
        this.players = players;
        this.deckLeft = deckLeft;
        handValues = new ArrayList<>();
        handRanks = new ArrayList<>();
    }

    //MODIFIES: Players in players
    //EFFECTS: sets each players hand ranking
    public void setHandRankings() {
        for (Player p: players) {
            ArrayList<Card> hand = new ArrayList<>();
            for (Card c: boardCards) {
                hand.add(c);
            }
            hand.add(p.getFirstCard());
            hand.add(p.getSecondCard());
            p.setHandRank(setRank(hand, p));
        }
        calculateOdds(compareHands());
    }

    //EFFECTS: compares hand rank of one hand to the other
    public Player compareHands() {
        int hr1 = players.get(0).getHankRank();
        int hr2 = players.get(1).getHankRank();
        int diff = hr1 - hr2;
        if (diff > 0) {
            return players.get(1);
        } else if (diff < 0) {
            return players.get(0);
        } else {
            if (players.get(0).getHandValue() > players.get(1).getHandValue()) {
                return players.get(1);
            } else {
                return players.get(0);
            }
        }
    }

    //EFFECTS: calculates odds of each player winning
    public void calculateOdds(Player behindPlayer) {
        Player aheadPlayer;
        if (behindPlayer.equals(players.get(0))) {
            aheadPlayer = players.get(1);
        } else {
            aheadPlayer = players.get(0);
        }
        behindPlayer.setOdds(countOuts(behindPlayer, aheadPlayer) * 100);
        for (Player p: players) {
            if (!p.equals(behindPlayer)) {
                p.setOdds((float)100 - behindPlayer.getOdds()); //TODO: round to two decimals
            }
        }
        if (boardCards.size() == 5) {
            behindPlayer.setOdds((float)0);
            for (Player p: players) {
                if (!p.equals(behindPlayer)) {
                    p.setOdds((float)100);
                }
            }
        }
    }

    //MODIFIES: bp, ap
    //EFFECTS: counts the number of cards that will make this player have the winning hand
    public float countOuts(Player bp, Player ap) {
        ArrayList<Card> handBp = new ArrayList<>();
        ArrayList<Card> handAp = new ArrayList<>();
        int outs = 0;
        for (Card c: boardCards) {
            handBp.add(c);
            handAp.add(c);
        }
        handBp.add(bp.getFirstCard());
        handBp.add(bp.getSecondCard());
        handAp.add(ap.getFirstCard());
        handAp.add(ap.getSecondCard());
        int behindHankRank = bp.getHankRank();
        int aheadHankRank = ap.getHankRank();
        for (Card c: deckLeft) {
            handBp.add(c);
            handAp.add(c);
            int newRankA = setRank(handAp, ap);
            int newRankB = setRank(handBp, bp);
            if (newRankA == newRankB) {
                if (bp.getHandValue() > ap.getHandValue()) {
                    outs++;
                } else if (bp.getKickerValue() > ap.getKickerValue()) {
                    outs++;
                }
            } else if (newRankA < newRankB) {
                outs++;
            }
            handBp.remove(c);
            handAp.remove(c);
        }
        bp.setHandRank(behindHankRank);
        ap.setHandRank(aheadHankRank);
        return outs / (float) deckLeft.size();
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
        p.setHandValue(Math.max(p.getFirstCard().getRawValue(), p.getFirstCard().getRawValue()));
        p.setKickerValue(Math.max(p.getFirstCard().getRawValue(), p.getFirstCard().getRawValue()));
        return HIGH;
    }

    //EFFECTS: returns true if hand contains royal flush
    public boolean isRoyalFlush(ArrayList<Card> hand, Player p) {
        if (isStraightFlush(hand, p)) {
            if (p.getHandValue() == 14) {
                return true;
            }
        }
        return false;
    }

    //MODIFIES: p
    //EFFECTS: returns true if hand contains straight-flush
    public boolean isStraightFlush(ArrayList<Card> hand, Player p) {
        ArrayList<ArrayList<Card>> suits = new ArrayList<>();
        ArrayList<Card> clubCards = new ArrayList<>();
        ArrayList<Card> spadeCards = new ArrayList<>();
        ArrayList<Card> heartCards = new ArrayList<>();
        ArrayList<Card> diamondCards = new ArrayList<>();
        suits.add(clubCards);
        suits.add(spadeCards);
        suits.add(heartCards);
        suits.add(diamondCards);
        for (Card c: hand) {
            switch (c.getSuit()) {
                case "S":
                    spadeCards.add(c);
                    break;
                case "C":
                    clubCards.add(c);
                    break;
                case "H":
                    heartCards.add(c);
                    break;
                case "D":
                    diamondCards.add(c);
            }
        }
        for (ArrayList<Card> a: suits) {
            if (a.size() >= 5) {
                return isStraight(a, p);
            }
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
        int tripsValue = 0;
        boolean hasTrips = false;
        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                for (int k = j + 1; k < hand.size(); k++) {
                    int c1 = hand.get(i).getRawValue();
                    if (c1 == hand.get(j).getRawValue() && c1 == hand.get(k).getRawValue()) {
                        tripsValue = c1;
                        hasTrips = true;
                    }
                }
            }
        }
        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                int c1 = hand.get(i).getRawValue();
                if (c1 == hand.get(j).getRawValue() && c1 != tripsValue && hasTrips) {
                    p.setHandValue(tripsValue);
                    p.setKickerValue(c1);
                    return true;
                }
            }
        }
        return false;
    }

    //MODIFIES: p
    //EFFECTS: returns true if hand contains a flush
    public boolean isFlush(ArrayList<Card> hand, Player p) {
        ArrayList<ArrayList<Card>> suits = new ArrayList<>();
        ArrayList<Card> clubCards = new ArrayList<>();
        ArrayList<Card> spadeCards = new ArrayList<>();
        ArrayList<Card> heartCards = new ArrayList<>();
        ArrayList<Card> diamondCards = new ArrayList<>();
        suits.add(clubCards);
        suits.add(spadeCards);
        suits.add(heartCards);
        suits.add(diamondCards);
        for (Card c: hand) {
            switch (c.getSuit()) {
                case "S":
                    spadeCards.add(c);
                    break;
                case "C":
                    clubCards.add(c);
                    break;
                case "H":
                    heartCards.add(c);
                    break;
                case "D":
                    diamondCards.add(c);
            }
        }
        return (spadeCards.size() >= 5 || clubCards.size() >= 5 || heartCards.size() >= 5 || diamondCards.size() >= 5);
    }

    //MODIFIES: p
    //EFFECTS: returns true if hand contains straight
    public boolean isStraight(ArrayList<Card> hand, Player p) {
        Collections.sort(hand);
        for (int i = 0; i < hand.size(); i++) {
            int numStraightCards = 0;
            int value1 = hand.get(i).getRawValue();
            for (int j = i + 1; j < hand.size(); j++) {
                int value2 = hand.get(j).getRawValue();
                boolean add = false;
                if (Math.abs(value1 - value2) == 1) {
                    add = true;
                    numStraightCards++;
                    value1 = value2;
                }
            }
            if (numStraightCards >= 4) {
                p.setHandValue(Collections.max(hand).getRawValue());
                p.setKickerValue(0);
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
        int valuePair1 = 0;
        ArrayList<Integer> pair2 = new ArrayList<>();
        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                int c1 = hand.get(i).getRawValue();
                if (c1 == hand.get(j).getRawValue()) {
                    valuePair1 = c1;
                }
            }
        }
        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                int c2 = hand.get(i).getRawValue();
                if (c2 == hand.get(j).getRawValue() && c2 != valuePair1) {
                    if (valuePair1 > c2) {
                        p.setHandValue(valuePair1);
                        p.setKickerValue(c2);
                    } else {
                        p.setHandValue(c2);
                        p.setKickerValue(valuePair1);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    //MODIFIES: p
    //EFFECTS: returns true if hand contains a pair
    public boolean isPair(ArrayList<Card> hand, Player p) {
        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
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

}
