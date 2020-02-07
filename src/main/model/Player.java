package model;

public class Player {
    private String name;
    private Card firstCard;
    private Card secondCard;
    private boolean isPair;
    private float odds;
    private int handPosition; // 0-5 , 0=low/low, 1=mid/low, 2=mid/mid, 3=high/low, 4=high/mid, 5=high/high
    private int handRank; //0-10; High Card - Royal Flush

    public Player(String name) {
        this.name = name;
        odds = (float)50;
    }

    public void setHandRank(int handRank) {
        this.handRank = handRank;
    }

    public int getHankRank() {
        return this.handRank;
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
        setHandPosition();
    }

    // EFFECT: ranks hand from 0-5
    public void setHandPosition() {
        if (firstCard.getPosition().equals("low")) {
            switch (secondCard.getPosition()) {
                case "low":
                    handPosition = 0;
                    break;
                case "middle":
                    handPosition = 1;
                    break;
                case "high":
                    handPosition = 3;
                    break;
            }
        } else if (firstCard.getPosition().equals("middle")) {
            switch (secondCard.getPosition()) {
                case "low":
                    handPosition = 1;
                    break;
                case "middle":
                    handPosition = 2;
                    break;
                case "high":
                    handPosition = 4;
                    break;
            }
        } else {
            handPosition = 5;
        }
    }

    public boolean getIsPair() {
        return isPair;
    }

    public int getHandPosition() {
        return handPosition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getOdds() {
        return odds;
    }

    public void setOdds(float odds) {
        this.odds = odds;
    }


    // MODIFIES: this, other
    // EFFECTS: compares this hand to other players hand
    public void compareHand(Player other) {
        if (!checkPairs(other)) {
            compareHighCard(other);
        }
    }

    // EFFECTS: checks if hand is paired and changes each players odds
    public boolean checkPairs(Player other) {
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
        } else if (this.isPair && !other.getIsPair()) {
            if (other.getHandPosition() > 3) {
                this.odds = 55;
                other.setOdds(100 - this.odds);
            } else {
                other.setOdds(30 / ((float)3 / ((other.getHandPosition()) + 1)));
                this.odds = 100 - other.getOdds();
            }
        } else if (other.getIsPair() && !this.isPair) {
            if (this.getHandPosition() > 3) {
                other.odds = 55;
                this.setOdds(100 - other.getOdds());
            } else {
                this.odds = (30 / ((float)3 / (this.handPosition + 1)));
                other.setOdds(100 - this.odds);
            }
        }
        return (this.isPair || other.getIsPair());
    }

    //EFFECTS: compares card raw value of each hand
    public void compareHighCard(Player other) {
        Card p1c1 = this.getFirstCard();
        Card p1c2 = this.getSecondCard();
        Card p2c1 = other.getFirstCard();
        Card p2c2 = other.getSecondCard();
        int p1HighCard = Integer.max(p1c1.getRawValue(), p1c2.getRawValue());
        int p2HighCard = Integer.max(p2c1.getRawValue(), p2c2.getRawValue());
        float oddsChange = 15 + 4 * (Math.abs(p1HighCard - p2HighCard) / (float)12);
        if (p1HighCard - p2HighCard > 0) {
            this.setOdds(this.getOdds() + oddsChange);
            other.setOdds(other.getOdds() - oddsChange);
        } else if (p1HighCard - p2HighCard < 0) {
            this.setOdds(this.getOdds() - oddsChange);
            other.setOdds(other.getOdds() + oddsChange);
        } else {
            compareLowCard(other);
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
            this.setOdds(this.getOdds() + oddsChange);
            other.setOdds(other.getOdds() - oddsChange);
        } else if (p1LowCard - p2LowCard < 0) {
            this.setOdds(this.getOdds() - oddsChange);
            other.setOdds(other.getOdds() + oddsChange);
        } else {
            compareLowCard(other);
        }
    }

    @Override
    //EFFECTS: returns string of cards in players hand
    public String toString() {
        return name + ": " + firstCard.toString() + " " + secondCard.toString() + " Hand Rank:" + handRank;
    }

    //EFFECTS: print this odds
    public String oddsToString() {
        return this.name + " " + this.odds + "%";
    }
}
