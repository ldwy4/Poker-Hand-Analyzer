package model;

public class Hand {
    private Card card1;
    private Card card2;
    private Card card3;
    private Card card4;
    private Card card5;
    private float odds;

    public Hand(Card card1, Card card2, Card card3, Card card4, Card card5) {
        this.card1 = card1;
        this.card2 = card2;
        this.card3 = card3;
        this.card4 = card4;
        this.card5 = card5;
        odds = (float) 50;
    }
}
