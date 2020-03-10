package ui;

import model.Card;
import model.EquityCalculator;
import model.Player;
import model.Table;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class GUI extends JFrame {
    public static final int WIDTH = 1400;
    public static final int HEIGHT = 1000;
    private TablePanel table;
    private CardsPanel cards;
    private Table pokerTable;
    private Scanner input;
    private boolean runProgram;
    Player user;
    Player opponent;
    ArrayList<Card> deck;
    ArrayList<Card> usedCards;
    ArrayList<Card> boardCards;
    EquityCalculator equityCalculator;

    public GUI() {
        super("Poker Hand Analyzer");
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        setLayout(new GridBagLayout());
        reset();
        cards = new CardsPanel(pokerTable.getDeck());
        Graphics g = cards.getGraphics();
        cards.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(cards);
        table = new TablePanel();
        table.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(table, new GridBagConstraints());
        //centreOnScreen();
        add(container, new GridBagConstraints());
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
    }



    // Centres frame on desktop
    // modifies: this
    // effects:  location of frame is set so frame is centred on desktop
    private void centreOnScreen() {
        Dimension scrn = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((scrn.width - getWidth()) / 2, (scrn.height - getHeight()) / 2);
    }

    private void reset() {
        input = new Scanner(System.in);
        runProgram = true;
        user = new Player("user");
        opponent = new Player("opponent");
        pokerTable = new Table(user, opponent);
    }
}
