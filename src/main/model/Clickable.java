package model;

import ui.CardsPanel;

import java.awt.*;

//Contains methods that are necessary for program to know if object has been clicked
public abstract class Clickable {
    protected int posY;
    protected int posX;

//    public int getPosX() {
//        return posX;
//    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public abstract boolean containsX(int x);

    public abstract void draw(Graphics g);

    // EFFECTS: return true iff the given y value is within the bounds of the Shape
    public boolean containsY(int y) {
        return (this.posY <= y) && (y <= this.posY + CardsPanel.CARD_HEIGHT);
    }

    // EFFECTS: return true if the given Point (x,y) is contained within the bounds of this Shape
    public boolean contains(Point point) {
        int pointX = point.x;
        int pointY = point.y;

        return containsX(pointX) && containsY(pointY);
    }
}
