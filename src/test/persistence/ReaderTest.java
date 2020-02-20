package persistence;

import exceptions.NoHandFound;
import model.Table;
import model.Player;
import model.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ReaderTest {
    @Test
    void testParseHand() {
        try {
            Table table = Reader.readHands(new File("./data/testHandsFile1.txt"), "twoaces");
            Player user = table.getPlayers().get(0);
            Player opponent = table.getPlayers().get(1);
            assertEquals("user: T♦ 6♣ Odds: user 50.0%", user.toString());
            assertEquals("opponent: A❤ Q❤ Odds: opponent 50.0%", opponent.toString());
            assertEquals(4, table.getBoardCards().size());
            assertEquals(44, table.getDeck().size());
        } catch (IOException | NoHandFound e) {
            fail("IOException should not have have been thrown");
        }
    }
}

