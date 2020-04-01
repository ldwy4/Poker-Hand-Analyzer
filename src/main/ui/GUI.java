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
        container.add(cardPanel, BorderLayout.PAGE_START);
        save = new JButton("Save");
        load = new JButton("Load");
        reset = new JButton("Reset");
        fileLoad = new JTextField(10);
        fileSave = new JTextField(10);
        save.addActionListener(new SaveButtonClickHandler());
        load.addActionListener(new LoadButtonClickHandler());
        reset.addActionListener(new ResetButtonClickHandler());
        container.add(reset);
        container.add(save);
        container.add(fileSave);
        container.add(load);
        container.add(fileLoad);
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
}
