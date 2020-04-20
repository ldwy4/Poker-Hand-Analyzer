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
        table.preFlopTableOdds();
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
        table.getBoardCards().add(table.addCard("K", "C"));
        table.getBoardCards().add(table.addCard("K", "S"));
        table.getBoardCards().add(table.addCard("J", "C"));
//        table.getBoardCards().add(table.addCard("7", "H"));
        table.removeCard("5", "S");
        table.removeCard("7", "D");
        table.removeCard("4", "D");
        table.removeCard("2", "S");
        calculator = new EquityCalculator(table.getBoardCards(), table.getPlayers(), table.getDeck());
        calculator.setHandRankings();
//        assertEquals((float) 41/44, player1.getOdds());
//        assertEquals(0, player2.getOdds());
//        assertEquals((float) 3/44 * 100, calculator.getSplitOdds());
//
        assertEquals((float)436/990, player1.getOdds());
        assertEquals((float)249/990, player2.getOdds());
        assertEquals((float)305/990 * 100, calculator.getSplitOdds());
    }
//
    @Test
    void testCountOuts2() {
        player1 = new Player("me");
        player2 = new Player("you");
        Player player3 = new Player("other");
        player1.setHand("7", "7", "S", "D");
        player2.setHand("9", "5", "S", "S");
        player3.setHand("6", "T", "S", "S");
        table = new Table(player1, player2);
        table.addPlayer(player3);
        table.getBoardCards().add(new Card("6", "C"));
        table.getBoardCards().add(new Card("7", "C"));
        table.getBoardCards().add(new Card("9", "C"));
        table.getBoardCards().add(new Card("8", "S"));
        table.removeCard("6","C");
        table.removeCard("7", "C");
        table.removeCard("9", "C");
        table.removeCard("8", "S");
        table.removeCard("7", "S");
        table.removeCard("7", "D");
        table.removeCard("9", "S");
        table.removeCard("5", "S");
        table.removeCard("T", "S");
        table.removeCard("6", "S");

        calculator = new EquityCalculator(table.getBoardCards(), table.getPlayers(), table.getDeck());
        calculator.setHandRankings();

        assertEquals((float) 31/42, player3.getOdds());
        assertEquals((float) 8/42, player1.getOdds());
        assertEquals((float) 3/42 * 100, calculator.getSplitOdds());
        assertEquals((float) 0, player2.getOdds());
//        assertEquals((float) 136/990, player2.getOdds());
//        assertEquals((float) 71/990 * 100, calculator.getSplitOdds());
//        assertEquals((float) 783/990, player1.getOdds());
//        assertEquals((float) 120/903, player3.getOdds());
//        assertEquals((float) 701/903, player1.getOdds());
//        assertEquals((float) 56/903 * 100, calculator.getSplitOdds());
//        assertEquals((float) 26/903, player2.getOdds());
//        table.getBoardCards().add(table.addCard("7", "H"));
//        calculator.calculateOdds(player2);
//        assertEquals((float)100, player1.getOdds());
//        assertEquals((float)0, player2.getOdds());
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
    void testPlayerOddsToString() {
        player1.checkPairs(player2);
        assertEquals("me 80.0%", player1.oddsToString());
    }
}
