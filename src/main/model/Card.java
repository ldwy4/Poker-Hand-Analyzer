package model;

public class Card {
    private String value;
    private int rawValue;
    private String suit;
    private String position;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Card(String number, String suit) {
        this.value = number;
        this.suit = suit;
        if (value.equals("T") || value.equals("J") || value.equals("Q") || value.equals("K") || value.equals("A")) {
            position = "high";
        } else if (Integer.parseInt(value) <= 9 && Integer.parseInt(value) >= 6) {
            position = "middle";
        } else {
            position = "low";
        }
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

    public int getRawValue() {
        return rawValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSuit() {
        return suit;
    }

    public void setSuit(String suit) {
        this.suit = suit;
    }

    //EFFECTS: returns true if other card is same suit as this
    public boolean isSuited(Card other) {
        return false;
    }

    @Override
    public String toString() {
        char symbol = 'l';
        switch (suit) {
            case "S":
                symbol = '\u2660';
                break;
            case "C":
                symbol = '\u2663';
                break;
            case "D":
                symbol = '\u2666';
                break;
            case "H":
                symbol = '\u2764';
                break;
        }
        return this.value.toUpperCase() + Character.toString(symbol);
    }
}
