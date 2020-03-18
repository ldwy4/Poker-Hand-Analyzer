package ui;

import model.Card;
import model.Player;
import model.Table;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class CardsPanel extends JPanel {
    ArrayList<Card> deck;
    ArrayList<Player> players;
    Table table;
    private JLabel playerLbl1;
    private JLabel playerLbl2;
    private static final String POKER_LABEL = "Player odds: ";
    public static final int CARD_WIDTH = 59;
    public static final int CARD_HEIGHT = 90;

    // File representing the folder that you select using a FileChooser
    static final File dir = new File("/images");

    // array of supported extensions (use a List if you prefer)
    static final String[] EXTENSIONS = new String[]{
            "C", "H", "D", "S" // and other formats you need
    };

    // array of supported extensions (use a List if you prefer)
    static final String[] VALUES = new String[]{
            "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A" // and other formats you need
    };
    // filter to identify images based on their extensions
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };

    public CardsPanel(ArrayList<Card> deck, Table table) {
        super(new BorderLayout());
        this.deck = deck;
        this.players = table.getPlayers();
        this.table = table;
        setPreferredSize(new Dimension(GUI.WIDTH, GUI.HEIGHT));
        playerLbl1 = new JLabel(POKER_LABEL);
        playerLbl1.setPreferredSize(new Dimension(200, 30));
        playerLbl1.setHorizontalTextPosition(SwingConstants.CENTER);
        playerLbl1.setVerticalTextPosition(SwingConstants.CENTER);
        playerLbl2 = new JLabel(POKER_LABEL);
        playerLbl2.setPreferredSize(new Dimension(200, 30));
        playerLbl2.setHorizontalTextPosition(SwingConstants.RIGHT);
        playerLbl2.setVerticalTextPosition(SwingConstants.BOTTOM);
        add(playerLbl1);
        // add(playerLbl2);
    }

    // EFFECTS: returns the Card at a given Point in panel, if any
    public Card getCardAtPoint(Point point) {
        for (Card c : deck) {
            if (c.contains(point)) {
                return c;
            }
        }
        return null;
    }

    // EFFECTS: returns the Card at a given Point in panel, if any
    public Player getPlayerAtPoint(Point point) {
        for (Player p : players) {
            if (p.contains(point)) {
                return p;
            }
        }
        return null;
    }

    // EFFECTS: returns the Card at a given Point in panel, if any
    public Table getTableAtPoint(Point point) {
        if (table.contains(point)) {
            return table;
        }
        return null;
    }

    public void setTable(Table table) {
        this.table = table;
        this.players = table.getPlayers();
        table.getPlayers().get(0).setPosX(500);
        table.getPlayers().get(1).setPosX(700);
        table.setPosX(510);
    }

    public void setDeck(ArrayList<Card> deck) {
        this.deck = deck;
    }

    // EFFECTS: updates player odds that are displayed
    public void update() {
        float odd1 = players.get(0).getOdds();
        float odd2 = players.get(1).getOdds();
        playerLbl1.setText(POKER_LABEL + "User Odds: " + odd1 + " Opponent Odds: " + odd2);
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        //dir.isDirectory();
        super.paintComponent(g);
        int y = 0;
        for (int j = 0; j < 2; j++) {
            int x = 0;
            for (int i = 0; i < 26; i++) {
                deck.get(i + 26 * j).setPosX(x);
                deck.get(i + 26 * j).setPosY(y);
                deck.get(i + 26 * j).draw(g);
                //g.drawImage(deck.get(i + 26 * j).getImage(), x, y, CARD_WIDTH, CARD_HEIGHT, null);
                if (deck.get(i + 26 * j).getIsSelected()) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setStroke(new BasicStroke(3));
                    g2d.setColor(Color.CYAN);
                    g2d.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT);
                }
                x += 60;
            }
            y += 100;
        }
        for (Player p : players) {
            p.draw(g);
        }
        table.draw(g);
    }
}