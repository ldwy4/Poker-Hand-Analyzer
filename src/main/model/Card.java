package model;

//Card that has value (Ace-King), suit, and position (low, middle, or high card)
public class Card implements Comparable {
    private String value;
    private int rawValue;
    private String suit;
    private String position;

    public String getPosition() {
        return position;
    }

//    public void setPosition(String position) {
//        this.position = position;
//    }

    public Card(String number, String suit) {
        this.value = number;
        this.suit = suit;
        setPosition(value);
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
