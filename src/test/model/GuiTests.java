package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.CardsPanel;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class GuiTests {
    private Card card;
    private Player player1;
    private Player player2;
    private Table table;

    @BeforeEach
    void runBefore() {
        player1 = new Player("me");
        player2 = new Player("you");
        card = new Card("4", "S");
        table = new Table(player1, player2);
    }

    @Test
    void testContains() {
        card.setPosX(20);
        card.setPosY(10);
        assertTrue(card.contains(new Point(30, 30)));
        assertFalse(card.contains(new Point(30 + CardsPanel.CARD_WIDTH, 30)));
        assertFalse(card.contains(new Point(30, 30 + CardsPanel.CARD_HEIGHT)));
    }

    @Test
    void testCardIsSelected() {
        assertFalse(card.getIsSelected());
        card.setIsSelected(true);
        assertTrue(card.getIsSelected());
    }
}
