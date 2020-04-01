package ui;

import exceptions.NoHandFound;
import model.Card;
import model.EquityCalculator;
import model.Player;
import model.Table;
import persistence.Reader;
import persistence.Writer;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class CardsPanel extends JPanel {
    ArrayList<Card> deck;
    ArrayList<Card> boardCards;
    ArrayList<Player> players;
    Table table;
    EquityCalculator equityCalculator;
    private JLabel playerLbl1;
    private JLabel playerLbl2;
    private static final String POKER_LABEL = "Player odds: ";
    public static final int CARD_WIDTH = 59;
    public static final int CARD_HEIGHT = 90;
    private static final String HAND_FILE = "./data/hands.txt";

    public CardsPanel(Table table) {
        super(new BorderLayout());
        this.deck = table.newDeck();
        this.players = table.getPlayers();
        this.table = table;
        this.boardCards = table.getBoardCards();
        equityCalculator = new EquityCalculator(boardCards, table.getPlayers(), table.getDeck());
        setPreferredSize(new Dimension(GUI.WIDTH, 600));
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

    public void setTable() {
        table.getPlayers().get(0).setPosX(500);
        table.getPlayers().get(1).setPosX(700);
        table.setPosX(510);
    }

//    public void setDeck(ArrayList<Card> deck) {
//        this.deck = deck;
//    }

    public void reset() {
        table = new Table(new Player("user"), new Player("opponent"));
        players = table.getPlayers();
        deck = table.newDeck();
        boardCards = table.getBoardCards();
        equityCalculator = new EquityCalculator(boardCards, table.getPlayers(), deck);
    }

    //EFFECTS: returns the card that is selected if there is one
    public Card findSelectedCard() {
        for (Card c: deck) {
            if (c.getIsSelected()) {
                return c;
            }
        }
        return null;
    }

    // EFFECTS: saves state of player and opponent hand and deck to HANDS_FILE
    public void saveHand(String str) {
        try {
            table.setTableName(str);
            Writer writer = new Writer(new File(HAND_FILE));
            writer.write(table);
            writer.close();
            System.out.println("Hand has been saved to file " + HAND_FILE);
            System.out.println("Hand name: " + table.getTableName());
        } catch (FileNotFoundException e) {
            System.out.println("Unable to save hand to " + HAND_FILE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // MODIFIES: this
    // EFFECTS: loads hand from ACCOUNTS_FILE, if that file exists;
    public void loadHand(String file) {
        try {
            table = Reader.readHands(new File(HAND_FILE), file);
            players = table.getPlayers();
            deck = table.newDeck();
            boardCards = table.getBoardCards();
            equityCalculator = new EquityCalculator(boardCards, table.getPlayers(), deck);
            System.out.println("Hand has been loaded from file " + HAND_FILE);
            System.out.println("Hand name: " + table.getTableName());
            calculateOdds();
            setTable();
            repaint();
        } catch (IOException e) {
            System.out.println("Error Occurred");
        } catch (NoHandFound e) {
            System.out.println("No hand found");
        }
    }

    //EFFECTS: displays calculated odds
    public void calculateOdds() {
        boolean full = true;
        for (Player p: table.getPlayers()) {
            if (p.getFirstCard() == null || p.getSecondCard() == null) {
                full = false;
            }
        }
        if (full) {
            table.preFlopTableOdds();
            if (table.getBoardCards().size() >= 3) {
                equityCalculator.setHandRankings();
            }
            this.update();
        }
    }

    // MODIFIES: Table.boardCards
    //EFFECTS: adds card to table boardCards
    public void changeBoard(Card card) {
        if (card != null && table.getBoardCards().size() < 5) {
            table.getBoardCards().add(card);
        } else {
            table.getBoardCards().remove(table.getBoardCards().size() - 1);
        }
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
        super.paintComponent(g);
        int y = 0;
        for (int j = 0; j < 2; j++) {
            int x = 0;
            for (int i = 0; i < 26; i++) {
                deck.get(i + 26 * j).setPosX(x);
                deck.get(i + 26 * j).setPosY(y);
                deck.get(i + 26 * j).draw(g);
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