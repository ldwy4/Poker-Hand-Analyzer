package ui;

import model.Card;
import model.Player;
import model.Table;

public class Main {
    public static void main(String[] args) {
        Player user = new Player("user");
        Player opponent = new Player("opponent");
        Table table = new Table(user, opponent);
        InfoManager info = new InfoManager(table);
        info.handleUserInput();
    }
}
