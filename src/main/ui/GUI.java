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
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class GUI extends JFrame {
    public static final int WIDTH = 1800;
    public static final int HEIGHT = 800;
    private static final String HAND_FILE = "./data/hands.txt";
    private CardsPanel cards;
    private Table table;
    private JPanel container;
    Player user;
    Player opponent;
    ArrayList<Card> deck;
    ArrayList<Card> usedCards;
    ArrayList<Card> boardCards;
    EquityCalculator equityCalculator;
    JButton save;
    JButton load;
    JButton reset;
    JTextField fileLoad;
    JTextField fileSave;

    public GUI() {
        super("Poker Hand Analyzer");
        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        setLayout(new GridBagLayout());
        add(container, new GridBagConstraints());
        reset();
        setScreen();
        pack();
        setResizable(false);
        setVisible(true);
        setMinimumSize(new Dimension(WIDTH,HEIGHT));
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            } // windowClosing
        });
        requestFocus();
        CardMouseListener cml = new CardMouseListener();
        addMouseListener(cml);
        addMouseMotionListener(cml);
    }

    private void setScreen() {
        cards = new CardsPanel(deck, table);
        Graphics g = cards.getGraphics();
        cards.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(cards);
        save = new JButton("Save");
        load = new JButton("Load");
        reset = new JButton("Reset");
        fileLoad = new JTextField();
        fileSave = new JTextField();
        save.addActionListener(new SaveButtonClickHandler());
        load.addActionListener(new LoadButtonClickHandler());
        reset.addActionListener(new ResetButtonClickHandler());
        container.add(reset);
        container.add(save);
        container.add(fileSave);
        container.add(load);
        container.add(fileLoad);
    }



    // Centres frame on desktop
    // modifies: this
    // effects:  location of frame is set so frame is centred on desktop
    private void centreOnScreen() {
        Dimension scrn = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((scrn.width - getWidth()) / 2, (scrn.height - getHeight()) / 2);
    }

    private void reset() {
        user = new Player("user");
        opponent = new Player("opponent");
        table = new Table(user, opponent);
        deck = table.newDeck();
        boardCards = table.getBoardCards();
        equityCalculator = new EquityCalculator(boardCards, table.getPlayers(), deck);
        user.setPosX(500);
        opponent.setPosX(700);
        table.setPosX(510);
    }

    // if c != null, sets c.isSelected to true and all other cards isSelected to false
    private void handleMouseClicked(MouseEvent e) {
        Card c = cards.getCardAtPoint(e.getPoint());
        if (c != null) {
            c.setIsSelected(true);
            for (Card card : deck) {
                if (!card.equals(c)) {
                    card.setIsSelected(false);
                }
            }
        } else {
            for (Card card : deck) {
                card.setIsSelected(false);
            }
        }
        repaint();
    }

    private void handleMousePressed(MouseEvent e) {
        Player p = cards.getPlayerAtPoint(e.getPoint());
        Table t = cards.getTableAtPoint(e.getPoint());
        Card card = findSelectedCard();
        if (t == null) {
            if (p != null && card != null) {
                if (p.getFirstCard() == null) {
                    p.setFirstCard(card);
                } else {
                    p.setSecondCard(card);
                }
            } else if (p != null && card == null) {
                if (p.getSecondCard() == null) {
                    p.setFirstCard(null);
                } else {
                    p.setSecondCard(null);
                }
            }
        } else {
            changeBoard(card);
        }
        calculateOdds();
        repaint();
    }

    private void changeBoard(Card card) {
        if (card != null && table.getBoardCards().size() < 5) {
            table.getBoardCards().add(card);
        } else {
            table.getBoardCards().remove(table.getBoardCards().size() - 1);
        }
    }

    //EFFECTS: displays calculated odds
    private void calculateOdds() {
        boolean full = true;
        for (Player p: table.getPlayers()) {
            if (p.getFirstCard() == null || p.getSecondCard() == null) {
                full = false;
            }
        }
        if (full) {
            table.tableOdds();
            if (table.getBoardCards().size() >= 3) {
                equityCalculator.setHandRankings();
            }
            cards.update();
        }
    }

    //EFFECTS: returns the card that is selected if there is one
    private Card findSelectedCard() {
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
            writer.write(user);
            writer.write(opponent);
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
            user = table.getPlayers().get(0);
            opponent = table.getPlayers().get(1);
            deck = table.newDeck();
            //usedCards = pokerTable.getUsedCards();
            boardCards = table.getBoardCards();
            equityCalculator = new EquityCalculator(boardCards, table.getPlayers(), deck);
            System.out.println("Hand has been loaded from file " + HAND_FILE);
            System.out.println("Hand name: " + table.getTableName());
            setCardsPanel();
            calculateOdds();
            repaint();
        } catch (IOException e) {
            System.out.println("Error Occurred");
        } catch (NoHandFound e) {
            System.out.println("No hand found");
        }
    }

    private void setCardsPanel() {
        cards.setDeck(deck);
        cards.setTable(table);
    }

    private class CardMouseListener extends MouseAdapter {
        // EFFECTS:Forward mouse clicked event to the active tool
        public void mouseClicked(MouseEvent e) {
            handleMouseClicked(translateEvent(e));
        }

        // EFFECTS:Forward mouse clicked event to the active tool
        public void mousePressed(MouseEvent e) {
            handleMousePressed(translateEvent(e));
        }

        // EFFECTS: translates the mouse event to current drawing's coordinate system
        private MouseEvent translateEvent(MouseEvent e) {
            return SwingUtilities.convertMouseEvent(e.getComponent(), e, cards);
        }
    }

    private class SaveButtonClickHandler implements ActionListener {

        //EFFECTS: either saves or shows list of saved tables based on action
        @Override
        public void actionPerformed(ActionEvent e) {
            saveHand(fileSave.getText());
            fileSave.setText("");
        }
    }

    private class ResetButtonClickHandler implements ActionListener {

        //EFFECTS: either saves or shows list of saved tables based on action
        @Override
        public void actionPerformed(ActionEvent e) {
            reset();
            setCardsPanel();
            cards.update();
            repaint();
        }
    }

    private class LoadButtonClickHandler implements ActionListener {

        //EFFECTS: either saves or shows list of saved tables based on action
        @Override
        public void actionPerformed(ActionEvent e) {
            loadHand(fileLoad.getText());
            fileLoad.setText("");
        }
    }
}
