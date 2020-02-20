package persistence;

import exceptions.NoHandFound;
import model.Player;
import model.Table;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// A reader that can read account data from a file
public class Reader {
    public static final String DELIMITER = ", ";

    // EFFECTS: returns a list of accounts parsed from file; throws
    // IOException if an exception is raised when opening / reading from file
    public static Table readHands(File file, String handName) throws IOException, NoHandFound {
        List<String> fileContent = readFile(file);
        Table table = new Table(new Player("me"), new Player("you"));
        table = parseContent(fileContent, handName);
        return table;
    }

    // EFFECTS: returns content of file as a list of strings, each string
    // containing the content of one row of the file
    private static List<String> readFile(File file) throws IOException {
        return Files.readAllLines(file.toPath());
    }

    // EFFECTS: returns a list of accounts parsed from list of strings
    // where each string contains data for one account
    private static Table parseContent(List<String> fileContent, String handName) throws NoHandFound {
        for (String line : fileContent) {
            ArrayList<String> lineComponents = splitString(line);
            if (lineComponents.get(lineComponents.size() - 1).equals(handName)) {
                return parseHand(lineComponents);
            }
        }
        throw new NoHandFound();
    }

    // EFFECTS: returns a list of strings obtained by splitting line on DELIMITER
    private static ArrayList<String> splitString(String line) {
        String[] splits = line.split(DELIMITER);
        return new ArrayList<>(Arrays.asList(splits));
    }

    // REQUIRES: components has size 4 where element 0 represents the
    // id of the next account to be constructed, element 1 represents
    // the id, elements 2 represents the name and element 3 represents
    // the balance of the account to be constructed
    // EFFECTS: returns an account constructed from components
    private static Table parseHand(List<String> components) {
        Player user = new Player("user");
        Player opponent = new Player("opponent");
        Table table = new Table(user, opponent);
        user.setFirstCard(table.addCard(components.get(0), components.get(1)));
        user.setSecondCard(table.addCard(components.get(2), components.get(3)));
        opponent.setFirstCard(table.addCard(components.get(4), components.get(5)));
        opponent.setSecondCard(table.addCard(components.get(6), components.get(7)));
        for (int i = 8; i < components.size() - 1; i += 2) {
            table.getBoardCards().add(table.addCard(components.get(i), components.get(i + 1)));
        }
        return table;
    }
}
