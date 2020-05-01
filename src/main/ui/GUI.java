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

//Graphic user interface for poker analyzer
public class GUI extends JFrame {
    public static final int WIDTH = 1800;
    public static final int HEIGHT = 800;
    private CardsPanel cardPanel;
    private JPanel container;
    private JPanel buttons;
    JButton save;
    JButton load;
    JButton reset;
    JButton addPlayer;
    JTextField fileLoad;
    JTextField fileSave;

    public GUI() {
        super("Poker Hand Analyzer");
        container = new JPanel();
        container.setLayout(new BorderLayout());
        container.setBackground(Color.getHSBColor(140,100,31));
        setLayout(new GridBagLayout());
        add(container, new GridBagConstraints());
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
        Table table = new Table(new Player("user"), new Player("opponent"));
        cardPanel = new CardsPanel(table);
        cardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.setBackground(new Color(0,77,0));
        save = new JButton("Save");
        load = new JButton("Load");
        reset = new JButton("Reset");
        addPlayer = new JButton("Add Player");
        fileLoad = new JTextField(10);
        fileLoad.setActionCommand("JTextField");
        fileSave = new JTextField(10);
        save.addActionListener(new SaveButtonClickHandler());
        load.addActionListener(new LoadButtonClickHandler());
        reset.addActionListener(new ResetButtonClickHandler());
        addPlayer.addActionListener(new AddPlayerButtonClickHandler());
        buttons = new JPanel();
        buttons.setPreferredSize(new Dimension(200, GUI.HEIGHT));
        buttons.add(addPlayer);
        buttons.add(reset);
        buttons.add(save);
        buttons.add(fileSave);
        buttons.add(load);
        buttons.add(fileLoad);
        container.add(cardPanel, BorderLayout.LINE_START);
        container.add(buttons, BorderLayout.LINE_END);
    }


    // if c != null, sets c.isSelected to true and all other cards isSelected to false
    private void handleMouseClicked(MouseEvent e) {
        Card c = cardPanel.getCardAtPoint(e.getPoint());
        if (c != null) {
            c.setIsSelected(true);
            for (Card card : cardPanel.deck) {
                if (!card.equals(c)) {
                    card.setIsSelected(false);
                }
            }
        } else {
            for (Card card : cardPanel.deck) {
                card.setIsSelected(false);
            }
        }
        repaint();
    }

    private void handleMousePressed(MouseEvent e) {
        Player p = cardPanel.getPlayerAtPoint(e.getPoint());
        Table t = cardPanel.getTableAtPoint(e.getPoint());
        Card card = cardPanel.findSelectedCard();
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
            cardPanel.changeBoard(card);
        }
        cardPanel.calculateOdds();
        repaint();
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
            return SwingUtilities.convertMouseEvent(e.getComponent(), e, cardPanel);
        }
    }

    private class SaveButtonClickHandler implements ActionListener {

        //EFFECTS: either saves or shows list of saved tables based on action
        @Override
        public void actionPerformed(ActionEvent e) {
            cardPanel.saveHand(fileSave.getText());
            fileSave.setText("");
        }
    }

    private class ResetButtonClickHandler implements ActionListener {

        //EFFECTS: either saves or shows list of saved tables based on action
        @Override
        public void actionPerformed(ActionEvent e) {
            cardPanel.reset();
            cardPanel.update();
            repaint();
        }
    }

    private class LoadButtonClickHandler implements ActionListener {

        //EFFECTS: either saves or shows list of saved tables based on action
        @Override
        public void actionPerformed(ActionEvent e) {
            cardPanel.loadHand(fileLoad.getText());
            fileLoad.setText("");
        }
    }

    private class AddPlayerButtonClickHandler implements ActionListener {

        //EFFECTS: either saves or shows list of saved tables based on action
        @Override
        public void actionPerformed(ActionEvent e) {
            cardPanel.addPlayer();
        }
    }
}
