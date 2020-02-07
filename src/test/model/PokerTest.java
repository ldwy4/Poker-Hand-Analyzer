package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PokerTest {
    private Card card;
    private Card p1card1;
    private Card p1card2;
    private Card p2card1;
    private Card p2card2;
    private Player player1;
    private Player player2;
    private Table table;

    @BeforeEach
    void runBefore() {
        player1 = new Player("me");
        player2 = new Player("you");
        p1card1 = new Card("A", "S");
        p1card2 = new Card("A", "H");
        p2card1 = new Card("7", "H");
        p2card2 = new Card("2", "C");
        player1.setHand("A", "A", "S", "H");
        player2.setHand("7", "2", "H", "C");
    }

    @Test
    void testPlayerConstructor() {
        Player player = new Player("Liam");
        assertEquals("Liam", player.getName());
    }

    @Test
    void testCardConstructor() {
        card = new Card("A", "S");
        assertEquals("A", card.getValue());
        assertEquals("S", card.getSuit());
        assertEquals("high", card.getPosition());
        card = new Card("2", "C");
        assertEquals("2", card.getValue());
        assertEquals("C", card.getSuit());
        assertEquals("low", card.getPosition());
        card = new Card("8", "H");
        assertEquals("8", card.getValue());
        assertEquals("H", card.getSuit());
        assertEquals("middle", card.getPosition());
    }

    @Test
    void testTableConstructor() {
        table = new Table(player1, player2);
        assertEquals(2, table.getNumPlayers());
        assertEquals(52, table.getDeck().size());
        assertEquals(2, table.getPlayers().size());
        assertEquals(0, table.getUsedCards().size());
    }

    @Test
    void testCheckPairs() {
        player1.checkPairs(player2);
        assertEquals(80, player1.getOdds());
        assertEquals(20, player2.getOdds());
        player1.setHand("A", "A", "S", "H");
        player2.setHand("7", "7", "H", "C");
        player1.checkPairs(player2);
        assertEquals(82, player1.getOdds());
        assertEquals(18, player2.getOdds());
        player2.checkPairs(player1);
        assertEquals(82, player1.getOdds());
        assertEquals(18, player2.getOdds());
        player1.setHand("9", "A", "S", "H");
        player1.checkPairs(player2);
        assertEquals(45, player1.getOdds());
        assertEquals(55, player2.getOdds());
    }

    @Test
    void testCompareHighCard() {
        player1.setHand("A", "8", "S", "H");
        player2.setHand("7", "2", "H", "C");
        player1.compareHighCard(player2);
        assertEquals(50 + 15 + (7/(float)12) * 4, player1.getOdds());
        assertEquals(50 - (15 + (7/(float)12) * 4), player2.getOdds());
    }

    //Todo 5: finish this test
//    @Test
//    void testCompareLowCard() {
//
//    }
//
    @Test
    void testCompareHard() {
        player1.compareHand(player2);
        assertEquals(80, player1.getOdds());
        assertEquals(20, player2.getOdds());
        player1 = new Player("me");
        player2 = new Player("you");
        player1.setHand("A", "8", "S", "H");
        player2.setHand("7", "2", "H", "C");
        player1.compareHand(player2);
        assertEquals(50 + 15 + (7/(float)12) * 4, player1.getOdds());
        assertEquals(50 - (15 + (7/(float)12) * 4), player2.getOdds());
    }

    @Test
    void testTableOdds() {
        table = new Table(player1, player2);
        table.tableOdds();
        assertEquals(80, player1.getOdds());
        assertEquals(20, player2.getOdds());
    }

    @Test
    void testPlayerToString() {
        assertEquals("me: A" + Character.toString('\u2660') + " A" + Character.toString('\u2764'), player1.toString());
    }
    //TODO 1: test table.newDeck()
    //TODO 2: test player.printHand()
    //TODO 3: test card.toString()
}
