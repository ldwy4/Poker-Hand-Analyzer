package persistence;

import model.Card;
import model.Player;
import model.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class WriterTest {
    private static final String TEST_FILE = "./data/testHands.txt";
    private Writer testWriter;
    private Player user;
    private Player opponent;
    private Table table;

    @BeforeEach
    void runBefore() throws IOException {
        testWriter = new Writer(new File(TEST_FILE));
        user = new Player("user");
        opponent = new Player("opponent");
        table = new Table(user, opponent);
        user.setFirstCard(table.addCard("J","H"));
        user.setSecondCard(table.addCard("J","D"));
        opponent.setFirstCard(table.addCard("T","H"));
        opponent.setSecondCard(table.addCard("6","H"));
        table.getBoardCards().add(table.addCard("A", "H"));
        table.getBoardCards().add(table.addCard("Q", "H"));
        table.getBoardCards().add(table.addCard("5", "H"));
        table.setTableName("test");
    }

    @Test
    void testWriteAccounts() {
        // save chequing and savings accounts to file
        testWriter.write(user);
        testWriter.write(opponent);
        testWriter.write(table);
        testWriter.close();

        // now read them back in and verify that the player hands have the expected values
        try {
            Table newTable = Reader.readHands(new File(TEST_FILE), table.getTableName());
            Player user = newTable.getPlayers().get(0);
            assertEquals(11, user.getFirstCard().getRawValue());
            assertEquals("H", user.getFirstCard().getSuit());
            assertEquals(11, user.getSecondCard().getRawValue());
            assertEquals("D", user.getSecondCard().getSuit());

            Player opponent = newTable.getPlayers().get(1);
            assertEquals(10, opponent.getFirstCard().getRawValue());
            assertEquals("H", opponent.getFirstCard().getSuit());
            assertEquals(6, opponent.getSecondCard().getRawValue());
            assertEquals("H", opponent.getSecondCard().getSuit());

            //verify that board cards have expected values
            assertEquals(14, newTable.getBoardCards().get(0).getRawValue());
            assertEquals("H", newTable.getBoardCards().get(0).getSuit());
            assertEquals(12, newTable.getBoardCards().get(1).getRawValue());
            assertEquals("H", newTable.getBoardCards().get(1).getSuit());
            assertEquals(5, newTable.getBoardCards().get(2).getRawValue());
            assertEquals("H", newTable.getBoardCards().get(2).getSuit());
        } catch (IOException e) {
            fail("IOException should not have been thrown");
        }
    }
}
