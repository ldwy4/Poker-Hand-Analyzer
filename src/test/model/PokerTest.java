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
    private EquityCalculator calculator;
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
        table = new Table(player1, player2);
    }

    @Test
    void testPlayerConstructor() {
        Player player = new Player("Liam");
        assertEquals("Liam", player.getName());
    }

    @Test
    void testEquityCalculator() {
        table.getBoardCards().add(new Card("A", "C"));
        table.getBoardCards().add(new Card("8", "S"));
        table.getBoardCards().add(new Card("T", "S"));
        calculator = new EquityCalculator(table.getBoardCards(), table.getPlayers(), table.getDeck());
        assertEquals(3, calculator.getBoardCards().size());
        assertEquals(2, calculator.getPlayers().size());
        assertEquals(0, calculator.getHandValues().size());
        assertEquals(0, calculator.getHandRanks().size());
        assertEquals(52, calculator.getDeckLeft().size());
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
        assertEquals(2, table.getNumPlayers());
        assertEquals(52, table.getDeck().size());
        assertEquals(2, table.getPlayers().size());
        assertEquals(0, table.getUsedCards().size());
        assertEquals(0, table.getBoardCards().size());
    }

    @Test
    void testLowCardHandPosition() {
        player2.setHand("2", "8", "H", "C");
        player2.lowCardHandPosition();
        assertEquals(1, player2.getHandPosition());
        player2.setHand("2", "A", "H", "C");
        player2.lowCardHandPosition();
        assertEquals(3, player2.getHandPosition());
    }

    @Test
    void testCheckPairs() {
        player1.checkPairs(player2);
        assertEquals(80, player1.getOdds());
        assertEquals(20, player2.getOdds());
        player2.setHand("7", "7", "H", "C");
        player1.checkPairs(player2);
        assertEquals(82, player1.getOdds());
        assertEquals(18, player2.getOdds());
        player2.checkPairs(player1);
        assertEquals(82, player1.getOdds());
        assertEquals(18, player2.getOdds());
        player2.checkPairs(player1);
        assertEquals(82, player1.getOdds());
        assertEquals(18, player2.getOdds());
        player1.setHand("9", "A", "S", "H");
        player1.checkPairs(player2);
        assertEquals(45, player1.getOdds());
        assertEquals(55, player2.getOdds());
        player2.checkPairs(player1);
        assertEquals(45, player1.getOdds());
        assertEquals(55, player2.getOdds());
        player1.setHand("7", "7", "S", "D");
        player1.checkPairs(player2);
        assertEquals(50, player1.getOdds());
        assertEquals(50, player2.getOdds());
        player1.setHand("2", "4", "S", "D");
        player1.checkPairs(player2);
        assertEquals(10, player1.getOdds());
        assertEquals(90, player2.getOdds());
    }

    @Test
    void testCompareHighCard() {
        player1.setHand("A", "8", "S", "H");
        player2.setHand("7", "2", "H", "C");
        player1.compareHighCard(player2);
        assertEquals(50 + 15 + (7/(float)12) * 4, player1.getOdds());
        assertEquals(50 - (15 + (7/(float)12) * 4), player2.getOdds());
        player2.compareHighCard(player1);
        assertEquals(50 + 15 + (7/(float)12) * 4, player1.getOdds());
        assertEquals(50 - (15 + (7/(float)12) * 4), player2.getOdds());
        player1.setHand("A", "8", "S", "H");
        player2.setHand("A", "2", "H", "C");
        player1.compareHighCard(player2);
        assertEquals(69, player1.getOdds());
        assertEquals(31, player2.getOdds());
        player2.compareHighCard(player1);
        assertEquals(69, player1.getOdds());
        assertEquals(31, player2.getOdds());
    }

    @Test
    void testCompareHard() {
        player1.compareHand(player2);
        assertEquals(80, player1.getOdds());
        assertEquals(20, player2.getOdds());
        player1 = new Player("me");
        player2 = new Player("you");
        player1.setFirstCard(table.addCard("A", "S"));
        player1.setSecondCard(table.addCard("8", "H"));
        player2.setFirstCard(table.addCard("7", "H"));
        player2.setSecondCard(table.addCard("2", "C"));
        player1.compareHand(player2);
        assertEquals(50 + 15 + (7/(float)12) * 4, player1.getOdds());
        assertEquals(50 - (15 + (7/(float)12) * 4), player2.getOdds());
    }

    @Test
    void testSetHandPosition() {
        player1 = new Player("me");
        player1.setHand("5", "2", "H", "C");
        assertEquals(0, player1.getHandPosition());
    }

    @Test
    void testTableOdds() {
        table = new Table(player1, player2);
        table.tableOdds();
        assertEquals(80, player1.getOdds());
        assertEquals(20, player2.getOdds());
    }

    @Test
    void testBoardCardsToString() {
        table.getBoardCards().add(new Card("A", "C"));
        table.getBoardCards().add(new Card("8", "S"));
        table.getBoardCards().add(new Card("T", "S"));
        assertEquals("A" + Character.toString('\u2663') + "8" + Character.toString('\u2660') + "T" + Character.toString('\u2660'), table.boardCardsToString());
    }


    @Test
    void testSetHandRankings() {
        table.getBoardCards().add(new Card("6", "C"));
        table.getBoardCards().add(new Card("3", "H"));
        table.getBoardCards().add(new Card("4", "D"));
        table.getBoardCards().add(new Card("5", "C"));
        calculator = new EquityCalculator(table.getBoardCards(), table.getPlayers(), table.getDeck());
        calculator.setHandRankings();
        assertEquals(1, player1.getHankRank());
        assertEquals(4, player2.getHankRank());
        table.getBoardCards().clear();
        table.getBoardCards().add(new Card("A", "C"));
        table.getBoardCards().add(new Card("3", "C"));
        table.getBoardCards().add(new Card("8", "C"));
        table.getBoardCards().add(new Card("5", "C"));
        calculator.setHandRankings();
        assertEquals(3, player1.getHankRank());
        assertEquals(5, player2.getHankRank());
        table.getBoardCards().clear();
        table.getBoardCards().add(new Card("A", "C"));
        table.getBoardCards().add(new Card("3", "D"));
        table.getBoardCards().add(new Card("8", "C"));
        table.getBoardCards().add(new Card("5", "C"));
        calculator.setHandRankings();
        assertEquals(3, player1.getHankRank());
        assertEquals(0, player2.getHankRank());
        table.getBoardCards().clear();
        table.getBoardCards().add(new Card("2", "C"));
        table.getBoardCards().add(new Card("3", "D"));
        table.getBoardCards().add(new Card("7", "C"));
        table.getBoardCards().add(new Card("5", "C"));
        calculator.setHandRankings();
        assertEquals(1, player1.getHankRank());
        assertEquals(2, player2.getHankRank());
        table.getBoardCards().clear();
        table.getBoardCards().add(new Card("A", "C"));
        table.getBoardCards().add(new Card("A", "D"));
        table.getBoardCards().add(new Card("7", "D"));
        table.getBoardCards().add(new Card("7", "C"));
        calculator.setHandRankings();
        assertEquals(7, player1.getHankRank());
        assertEquals(6, player2.getHankRank());
        table.getBoardCards().clear();
        table.getBoardCards().add(new Card("6", "C"));
        table.getBoardCards().add(new Card("3", "C"));
        table.getBoardCards().add(new Card("4", "C"));
        table.getBoardCards().add(new Card("5", "C"));
        table.getBoardCards().add(new Card("6", "H"));
        calculator.setHandRankings();
        assertEquals(2, player1.getHankRank());
        assertEquals(8, player2.getHankRank());
    }

    @Test
    void testStraightFlush() {
        player1.setHand("A", "K", "D", "D");
        player2.setHand("3", "2", "H", "H");
        table.getBoardCards().add(new Card("Q", "D"));
        table.getBoardCards().add(new Card("5", "H"));
        table.getBoardCards().add(new Card("6", "H"));
        table.getBoardCards().add(new Card("4", "H"));
        calculator = new EquityCalculator(table.getBoardCards(), table.getPlayers(), table.getDeck());
        calculator.setHandRankings();
        assertEquals(0, player1.getHankRank());
        assertEquals(8, player2.getHankRank());
        table.getBoardCards().clear();
        table.getBoardCards().add(new Card("Q", "D"));
        table.getBoardCards().add(new Card("10", "D"));
        table.getBoardCards().add(new Card("J", "D"));
        table.getBoardCards().add(new Card("4", "H"));
        calculator.setHandRankings();
        assertEquals(9, player1.getHankRank());
        assertEquals(0, player2.getHankRank());
    }

    @Test
    void testFlush() {
        player1.setHand("A", "4", "D", "D");
        player2.setHand("3", "2", "H", "H");
        table.getBoardCards().add(new Card("Q", "D"));
        table.getBoardCards().add(new Card("8", "H"));
        table.getBoardCards().add(new Card("6", "H"));
        table.getBoardCards().add(new Card("5", "H"));
        calculator = new EquityCalculator(table.getBoardCards(), table.getPlayers(), table.getDeck());
        calculator.setHandRankings();
        assertEquals(0, player1.getHankRank());
        assertEquals(5, player2.getHankRank());
        table.getBoardCards().clear();
        table.getBoardCards().add(new Card("Q", "D"));
        table.getBoardCards().add(new Card("10", "D"));
        table.getBoardCards().add(new Card("J", "D"));
        table.getBoardCards().add(new Card("8", "H"));
        calculator.setHandRankings();
        assertEquals(5, player1.getHankRank());
        assertEquals(0, player2.getHankRank());
    }

    @Test
    void testCountOuts() {
        player1 = new Player("me");
        player2 = new Player("you");
        player1.setHand("7", "4", "D", "D");
        player2.setHand("2", "5", "S", "S");
        table = new Table(player1, player2);
        calculator = new EquityCalculator(table.getBoardCards(), table.getPlayers(), table.getDeck());
        calculator.setHandRankings();
//        assertEquals(100 * (float)8/52, player2.getOdds());
//        assertEquals(100 * (float)44/52, player1.getOdds());
        table = new Table(player2, player1);
        calculator = new EquityCalculator(table.getBoardCards(), table.getPlayers(), table.getDeck());
        calculator.setHandRankings();
//        assertEquals(100 * (float)8/52, player2.getOdds());
//        assertEquals(100 * (float)44/52, player1.getOdds());
//        assertEquals((float)14/44, calculator.countOuts(player2, player1));
        table.getBoardCards().add(new Card("K", "C"));
        table.getBoardCards().add(new Card("K", "S"));
        table.getBoardCards().add(new Card("J", "C"));
        table.getBoardCards().add(new Card("4", "S"));
        table.removeCard("K","C");
        table.removeCard("K", "S");
        table.removeCard("J", "C");
        table.removeCard("4", "S");
        table.removeCard("5", "S");
        table.removeCard("7", "D");
        table.removeCard("4", "D");
        table.removeCard("2", "S");
        calculator = new EquityCalculator(table.getBoardCards(), table.getPlayers(), table.getDeck());
        calculator.setHandRankings();
        assertEquals((float)14/44, calculator.getOuts(player2, player1));
        table.getBoardCards().add(table.addCard("7", "H"));
        calculator.calculateOdds(player2);
        assertEquals((float)100, player1.getOdds());
        assertEquals((float)0, player2.getOdds());
    }

    @Test
    void testRemoveCard() {
        table = new Table(player1, player2);
        table.removeCard("K","C");
        assertEquals(51, table.getDeck().size());
        table.removeCard("K", "S");
        table.removeCard("J", "C");
        table.removeCard("4", "S");
        table.removeCard("5", "S");
        table.removeCard("7", "D");
        table.removeCard("4", "D");
        table.removeCard("2", "S");
        assertEquals(44, table.getDeck().size());
    }

    @Test
    void testAddCard() {
        table = new Table(player1, player2);
        table.getBoardCards().add(table.addCard("K","C"));
        assertEquals(51, table.getDeck().size());
        table.getBoardCards().add(table.addCard("Q","C"));
        table.getBoardCards().add(table.addCard("J","C"));
        table.getBoardCards().add(table.addCard("T","C"));
        table.getBoardCards().add(table.addCard("9","C"));
        table.getBoardCards().add(table.addCard("8","C"));
        assertEquals(46, table.getDeck().size());
    }

    @Test
    void testValidCard() {
        assertTrue(table.validCard("5","H"));
        assertFalse(table.validCard("20","H"));
        assertFalse(table.validCard("3","g"));
    }

    @Test
    void testCardToString() {
        card = new Card("A", "S");
        assertEquals("A" + Character.toString('\u2660'), card.toString());
        card = new Card("A", "H");
        assertEquals("A" + Character.toString('\u2764'), card.toString());
        card = new Card("A", "C");
        assertEquals("A" + Character.toString('\u2663'), card.toString());
        card = new Card("A", "D");
        assertEquals("A" + Character.toString('\u2666'), card.toString());
    }
//
//    @Test
//    void testPlayerToString() {
//        assertEquals("me: A" + Character.toString('\u2660') + " A" + Character.toString('\u2764') + " Odds: me 50.0%", player1.toString());
//    }

    @Test
    void testPostFlopTableOdds() {
        String spade = Character.toString('\u2660');
        String heart = Character.toString('\u2764');
        String club = Character.toString('\u2663');
        String diamond = Character.toString('\u2666');
        String first = "me: A" + spade + " A" + heart + " Odds: me 50.0%";
        String second = "you: 7❤ 2♣ Odds: you 50.0%";
        assertEquals(first + "\n" + second + "\n", table.postFlopTableOdds());
    }

    @Test
    void testPlayerOddsToString() {
        player1.checkPairs(player2);
        assertEquals("me 80.0%", player1.oddsToString());
    }
}
