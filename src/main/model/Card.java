package model;

import ui.CardsPanel;
import ui.ImageStore;

import java.awt.*;
import java.util.Objects;

//Card that has value (Ace-King), suit, and position (low, middle, or high card)
public class Card extends Clickable implements Comparable  {
    private String value;
    private int rawValue;
    private String suit;
    private String position;
    private Image image;
    private boolean isSelected = false;

    // Construct a card
    public Card(String number, String suit) {
        this.value = number;
        this.suit = suit;
//        this.x = x;
//        this.y = y;
        setPosition(value);
        if (number.equals("T")) {
            number = "10";
        }
        image = ImageStore.get().getImage("images/cards/" + number + suit + ".png");
        setRawValue(value);
    }

    //MODIFIES: this
    //EFFECTS: sets the raw value of this card based on value of given string
    private void setRawValue(String value) {
        switch (value) {
            case "T":
                rawValue = 10;
                break;
            case "J":
                rawValue = 11;
                break;
            case "Q":
                rawValue = 12;
                break;
            case "K":
                rawValue = 13;
                break;
            case "A":
                rawValue = 14;
                break;
            default:
                rawValue = Integer.parseInt(value);
        }
    }

    //MODIFIES: this
    //EFFECTS: sets position of card based on card value
    public void setPosition(String v) {
        if (v.equals("T") || v.equals("J") || v.equals("Q") || v.equals("K") || v.equals("A")) {
            position = "high";
        } else if (Integer.parseInt(v) <= 9 && Integer.parseInt(v) >= 6) {
            position = "middle";
        } else {
            position = "low";
        }
    }

    public String getPosition() {
        return position;
    }

    public int getRawValue() {
        return rawValue;
    }

    public String getValue() {
        return value;
    }

//    public void setValue(String value) {
//        this.value = value;
//    }

    public String getSuit() {
        return suit;
    }

//    public void setSuit(String suit) {
//        this.suit = suit;
//    }

    @Override
    //EFFECTS: draws card
    public void draw(Graphics g) {
        g.drawImage(image, posX, posY, CardsPanel.CARD_WIDTH, CardsPanel.CARD_HEIGHT, null);
    }

    public Image getImage() {
        return image;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public boolean containsX(int x) {
        return (this.posX <= x) && (x <= this.posX + CardsPanel.CARD_WIDTH);
    }

    @Override
    public String toString() {
        String symbol = "";
        switch (suit) {
            case "S":
                symbol = "♠";
                break;
            case "C":
                symbol = "♣";
                break;
            case "D":
                symbol = "♦";
                break;
            case "H":
                symbol = "❤";
                break;
        }
        return this.value.toUpperCase() + symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(value, card.value) && Objects.equals(suit, card.suit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, suit);
    }

    @Override
    public int compareTo(Object o) {
        Card c2 = (Card) o;
        int value = c2.getRawValue();
        return this.getRawValue() - value;
    }
}
