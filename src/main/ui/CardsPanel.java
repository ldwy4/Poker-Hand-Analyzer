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
    Table table;
    EquityCalculator equityCalculator;
    private JLabel playerLbl1;
    private static final String POKER_LABEL = "<html><font color='white'>Split odds: ";
    public static final int CARD_WIDTH = 59;
    public static final int CARD_HEIGHT = 90;
    private static final String HAND_FILE = "./data/hands.txt";

    public CardsPanel(Table table) {
        super(new BorderLayout());
        this.deck = Table.newDeck();
        this.table = table;
        this.boardCards = table.getBoardCards();
        equityCalculator = new EquityCalculator(boardCards, table.getPlayers(), table.getDeck());
        setPreferredSize(new Dimension(GUI.WIDTH - 200, GUI.HEIGHT));
        playerLbl1 = new JLabel(POKER_LABEL + "</font></html>");
        playerLbl1.setPreferredSize(new Dimension(200, 30));
        playerLbl1.setHorizontalTextPosition(SwingConstants.CENTER);
        playerLbl1.setVerticalTextPosition(SwingConstants.CENTER);
        add(playerLbl1);
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
        for (Player p : table.getPlayers()) {
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
        deck = Table.newDeck();
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
            deck = Table.newDeck();
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

    //Modifies: this, table
    //EFFECTS: adds new player to the table
    public void addPlayer() {
        if (table.getPlayers().size() < 6) {
            Player player = new Player("Player" + table.getPlayers().size());
            setPlayerPosition(player);
            table.getPlayers().add(player);
            repaint();
        } else {
            System.out.println("Table is full!");
        }
    }

    //EFFECTS: sets the position of new Player
    private void setPlayerPosition(Player player) {
        int lastY = table.getPlayers().get(table.getPlayers().size() - 1).getPosY();
        int lastX = table.getPlayers().get(table.getPlayers().size() - 1).getPosX();
        if (lastY == Player.POS_Y) {
            player.setPosX(300);
            player.setPosY(400);
        } else {
            switch (lastX) {
                case 300:
                    player.setPosY(280);
                    player.setPosX(500);
                    break;
                case 500:
                    player.setPosX(700);
                    player.setPosY(280);
                    break;
                case 700:
                    player.setPosX(900);
                    player.setPosY(400);
            }
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
            table.getBoardCards().add(table.addCard(card.getValue(), card.getSuit()));
        } else {
            table.getBoardCards().remove(table.getBoardCards().size() - 1);
        }
    }

    // EFFECTS: updates player odds that are displayed
    public void update() {
        playerLbl1.setText(POKER_LABEL + equityCalculator.getSplitOdds() + "</font></html>");
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);
        int y = 0;
        for (int j = 0; j < 2; j++) {
            int x = 0;
            for (int i = 0; i < 26; i++) {
                deck.get(i + 26 * j).setPosX(x);
                deck.get(i + 26 * j).setPosY(y);
                deck.get(i + 26 * j).draw(g);
                if (deck.get(i + 26 * j).getIsSelected()) {
                    g2d.setStroke(new BasicStroke(3));
                    g2d.setColor(Color.CYAN);
                    g2d.drawRect(x, y, CARD_WIDTH, CARD_HEIGHT);
                }
                x += 60;
            }
            y += 100;
        }
        for (Player p : table.getPlayers()) {
            p.draw(g);
            g.setColor(Color.WHITE);
            g.drawString("Odds: " + p.getOdds(), p.getPosX() + 30, p.getPosY() + 110);
        }
        table.draw(g);
    }
}