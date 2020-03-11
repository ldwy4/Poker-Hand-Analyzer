package model;

import ui.CardsPanel;
import ui.ImageStore;

import java.awt.*;

//Card that has value (Ace-King), suit, and position (low, middle, or high card)
public class Card implements Comparable {
    private String value;
    private int rawValue;
    private String suit;
    private String position;
    private Image image;
    private int posX;
    private int posY;
    private boolean isSelected = false;

    public String getPosition() {
        return position;
    }


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

//    public void draw(Graphics g) {
//        g.drawImage(image, 10, 10,59, 90, null);
//    }

    public Image getImage() {
        return image;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

//    public boolean containsX(int x) {
//        return (this.posX <= x) && (x <= this.posX + CardsPanel.CARD_WIDTH);
//    }
//
//    // EFFECTS: return true iff the given y value is within the bounds of the Shape
//    public boolean containsY(int y) {
//        return (this.posY <= y) && (y <= this.posY + CardsPanel.CARD_HEIGHT);
//    }
//
//    // EFFECTS: return true if the given Point (x,y) is contained within the bounds of this Shape
//    public boolean contains(Point point) {
//        int pointX = point.x;
//        int pointY = point.y;
//
//        return containsX(pointX) && containsY(pointY);
//    }

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
    public int compareTo(Object o) {
        Card c2 = (Card) o;
        int value = c2.getRawValue();
        return this.getRawValue() - value;
    }
}
