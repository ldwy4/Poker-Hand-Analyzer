package model;

import persistence.Saveable;
import ui.CardsPanel;
import ui.ImageStore;

import java.awt.*;
import java.io.PrintWriter;

//Represents player at table, has hand of two cards, hand rank, and the odds associated with their hand
public class Player extends Clickable implements Saveable {
    private String name;
    private Card firstCard;
    private Card secondCard;
    private boolean isPair;
    private float odds;
    private int handPosition; // 0-5 , 0=low/low, 1=mid/low, 2=mid/mid, 3=high/low, 4=high/mid, 5=high/high
    private int handRank; //0-10; High Card - Royal Flush
    private int handValue; //value of high card in hand made
    private int kickerValue; //value of card in player hand that is not in total hand
    private int posX;
    public static final int posY = 500;

    public Player(String name) {
        this.name = name;
        odds = (float)50;
    }

    public int getKickerValue() {
        return kickerValue;
    }

    public void setKickerValue(int kickerValue) {
        this.kickerValue = kickerValue;
    }

    public void setHandRank(int handRank) {
        this.handRank = handRank;
    }

    public int getHankRank() {
        return this.handRank;
    }


    public int getHandValue() {
        return handValue;
    }

    public void setHandValue(int handValue) {
        this.handValue = handValue;
    }


    public Card getFirstCard() {
        return firstCard;
    }

    public void setFirstCard(Card firstCard) {
        this.firstCard = firstCard;
    }

    public Card getSecondCard() {
        return secondCard;
    }

    public void setSecondCard(Card secondCard) {
        this.secondCard = secondCard;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    //REQUIRES: cv1, cv2 to be valid card values; s1, s2 to be valid card suits
    //MODIFIES: this
    //EFFECTS: sets player hand to cards with given values with given suits
    public void setHand(String cv1, String cv2, String s1, String s2) {
        this.firstCard = new Card(cv1, s1);
        this.secondCard = new Card(cv2, s2);
        if (firstCard.getRawValue() == secondCard.getRawValue()) {
            isPair = true;
            handRank = 1;
        } else {
            isPair = false;
            handRank = 0;
        }
        this.setHandPosition();
    }

    // EFFECT: ranks hand from 0-5
    public void setHandPosition() {
        if (this.firstCard.getPosition().equals("low")) {
            lowCardHandPosition();
        } else if (this.firstCard.getPosition().equals("middle")) {
            switch (secondCard.getPosition()) {
                case "low":
                    this.handPosition = 1;
                    break;
                case "middle":
                    this.handPosition = 2;
                    break;
                case "high":
                    this.handPosition = 4;
                    break;
            }
        } else {
            this.handPosition = 5;
        }
    }

    //MODIFIES: this
    //EFFECTS: determines hand position based on second card position
    public void lowCardHandPosition() {
        switch (this.secondCard.getPosition()) {
            case "low":
                this.handPosition = 0;
                break;
            case "middle":
                this.handPosition = 1;
                break;
            case "high":
                this.handPosition = 3;
        }
    }


    public boolean getIsPair() {
        return isPair;
    }

    public int getHandPosition() {
        return this.handPosition;
    }

    public String getName() {
        return name;
    }

    public float getOdds() {
        return odds;
    }

    public void setOdds(float odds) {
        this.odds = odds;
    }


    // EFFECTS: compares this hand to other players hand
    public void compareHand(Player other) {
        if (!checkPairs(other)) {
            compareHighCard(other);
        }
    }

    // EFFECTS: checks if hand is paired and changes each players odds
    public boolean checkPairs(Player other) {
        if (this.bothPair(other) || this.onePair(other)) {
            return true;
        }
        return false;
    }

    //MODIFIES: this, other
    //EFFECTS: if both players have a pair, sets odds of each player based on their pair value
    public boolean bothPair(Player other) {
        Card p1c1 = this.getFirstCard();
        Card p2c1 = other.getFirstCard();
        if (this.isPair && other.getIsPair()) {
            if (p1c1.getRawValue() == p2c1.getRawValue()) {
                this.odds = 50;
                other.setOdds(50);
            } else if (p1c1.getRawValue() > p2c1.getRawValue()) {
                this.odds = 82;
                other.setOdds(100 - this.odds);
            } else {
                this.odds = 100 - 82;
                other.setOdds(100 - this.odds);
            }
            return true;
        }
        return false;
    }

    //MODIFIES: this, other
    //EFFECTS: if only one player has a pair, sets odds of each player based on that pair value
    public boolean onePair(Player other) {
        if (this.isPair && !other.getIsPair()) {
            if (other.getHandPosition() > 3) {
                this.odds = 55;
                other.setOdds(100 - this.odds);
            } else {
                other.setOdds(30 / ((float)3 / ((other.getHandPosition()) + 1)));
                this.odds = 100 - other.getOdds();
            }
            return true;
        } else if (other.getIsPair() && !this.isPair) {
            if (this.getHandPosition() > 3) {
                other.odds = 55;
                this.setOdds(100 - other.getOdds());
            } else {
                this.odds = (30 / ((float) 3 / (this.handPosition + 1)));
                other.setOdds(100 - this.odds);
            }
            return true;
        }
        return false;
    }

    //MODIFIES: this, other
    //EFFECTS: compares card raw value of each hand and changes odds depending on the difference in values
    public void compareHighCard(Player other) {
        Card p1c1 = this.getFirstCard();
        Card p1c2 = this.getSecondCard();
        Card p2c1 = other.getFirstCard();
        Card p2c2 = other.getSecondCard();
        int p1HighCard = Integer.max(p1c1.getRawValue(), p1c2.getRawValue());
        int p2HighCard = Integer.max(p2c1.getRawValue(), p2c2.getRawValue());
        float oddsChange = 15 + 4 * (Math.abs(p1HighCard - p2HighCard) / (float)12);
        if (p1HighCard - p2HighCard > 0) {
            this.setOdds(50 + oddsChange);
            other.setOdds(50 - oddsChange);
        } else if (p1HighCard - p2HighCard < 0) {
            this.setOdds(50 - oddsChange);
            other.setOdds(50 + oddsChange);
        } else {
            this.compareLowCard(other);
        }
    }

    //MODIFIES: this, other
    //EFFECTS: compares the lower card (kicker) of each hand and changes odds accordingly
    public void compareLowCard(Player other) {
        Card p1c1 = this.getFirstCard();
        Card p1c2 = this.getSecondCard();
        Card p2c1 = other.getFirstCard();
        Card p2c2 = other.getSecondCard();
        int p1LowCard = Integer.min(p1c1.getRawValue(), p1c2.getRawValue());
        int p2LowCard = Integer.min(p2c1.getRawValue(), p2c2.getRawValue());
        float oddsChange = 15 + 8 * (Math.abs(p1LowCard - p2LowCard) / (float)12);
        if (p1LowCard - p2LowCard > 0) {
            this.setOdds(50 + oddsChange);
            other.setOdds(50 - oddsChange);
        } else if (p1LowCard - p2LowCard < 0) {
            this.setOdds(50 - oddsChange);
            other.setOdds(50 + oddsChange);
        }
    }

//    // EFFECTS: returns string representation of player
//    @Override
//    public String toString() {
//        return name + ": " + firstCard.toString() + " " + secondCard.toString() + " Odds: " + this.oddsToString();
//    }

    // EFFECTS: allows player hand to be written to file
    @Override
    public void save(PrintWriter printWriter) {
        String delimiter = ", ";
        printWriter.print(firstCard.getValue());
        printWriter.print(delimiter);
        printWriter.print(firstCard.getSuit());
        printWriter.print(delimiter);
        printWriter.print(secondCard.getValue());
        printWriter.print(delimiter);
        printWriter.print(secondCard.getSuit());
        printWriter.print(delimiter);
    }

    //EFFECTS: returns this odds as String
    public String oddsToString() {
        return this.name + " " + this.odds + "%";
    }

    @Override
    public boolean containsX(int x) {
        return (this.posX <= x) && (x <= this.posX + 2 * CardsPanel.CARD_WIDTH);
    }

    //EFFECTS: draws this player's cards
    public void draw(Graphics g) {
        Image image1 = null;
        Image image2 = null;
        if (firstCard == null) {
            image1 = ImageStore.get().getImage("images/freeslot.png");
        } else {
            image1 = firstCard.getImage();
        }
        if (secondCard == null) {
            image2 = ImageStore.get().getImage("images/freeslot.png");
        } else {
            image2 = secondCard.getImage();
        }
        g.drawImage(image1, posX, posY, CardsPanel.CARD_WIDTH, CardsPanel.CARD_HEIGHT, null);
        g.drawImage(image2, posX + 60, posY, CardsPanel.CARD_WIDTH, CardsPanel.CARD_HEIGHT, null);
    }
}
