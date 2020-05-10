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
    JButton removePlayer;
    JTextField fileLoad;
    JTextField fileSave;

    public GUI() {
        super("Poker Hand Analyzer");
        container = new JPanel();
        container.setLayout(new BorderLayout());
        container.setBackground(new Color(0,77,0));
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
        createButtons();
        addButtons();
    }

    private void addButtons() {
        buttons = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        buttons.setLayout(gridBagLayout);
        JButton[] buttonArray = {addPlayer, removePlayer, save, load, reset};
        JTextField[] textFields = {null, null, fileSave, fileLoad, null};
        addSideMenu(buttonArray, textFields, gridBagLayout, buttons);
        buttons.setPreferredSize(new Dimension(220, GUI.HEIGHT));
//        buttons.add(addPlayer, c);
//        buttons.add(removePlayer, c);
//        buttons.add(save);
//        buttons.add(fileSave);
//        buttons.add(load);
//        buttons.add(fileLoad);
//        buttons.add(reset);
        container.add(cardPanel, BorderLayout.LINE_START);
        container.add(buttons, BorderLayout.LINE_END);
    }

    private void addSideMenu(JButton[] buttons,
                                  JTextField[] textFields,
                                  GridBagLayout gridbag,
                                  Container container) {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.PAGE_START;
        int numLabels = buttons.length;
        c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
        c.fill = GridBagConstraints.NONE;      //reset to default
        c.weightx = 0.0;                       //reset to default
        container.add(buttons[0], c);
        c.gridwidth = GridBagConstraints.REMAINDER;     //end row
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        container.add(buttons[1], c);
        for (int i = 2; i < numLabels - 1; i++) {
            c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
            c.fill = GridBagConstraints.NONE;      //reset to default
            c.weightx = 0.5;                       //reset to default
            container.add(buttons[i], c);

            c.gridwidth = GridBagConstraints.REMAINDER;     //end row
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 0.5;
            container.add(textFields[i], c);
        }
        c.gridwidth = GridBagConstraints.REMAINDER;     //end row
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        container.add(buttons[4], c);
    }

    private void createButtons() {
        save = new JButton("Save");
        load = new JButton("Load");
        reset = new JButton("Reset");
        addPlayer = new JButton("Add Player");
        removePlayer = new JButton("Remove Player");
        fileLoad = new JTextField(10);
        fileLoad.setActionCommand("JTextField");
        fileSave = new JTextField(10);
        save.addActionListener(new SaveButtonClickHandler());
        load.addActionListener(new LoadButtonClickHandler());
        reset.addActionListener(new ResetButtonClickHandler());
        addPlayer.addActionListener(new AddPlayerButtonClickHandler());
        removePlayer.addActionListener(new RemovePlayerButtonClickHandler());
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
                    p.setFirstCard(cardPanel.getTable().addCard(card));
                } else {
                    p.setSecondCard(cardPanel.getTable().addCard(card));
                }
            } else if (p != null && card == null) {
                if (p.getSecondCard() == null) {
                    cardPanel.getTable().returnCard(p.getFirstCard());
                    p.setFirstCard(null);
                } else {
                    cardPanel.getTable().returnCard(p.getSecondCard());
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

    private class RemovePlayerButtonClickHandler implements ActionListener {

        //EFFECTS: either saves or shows list of saved tables based on action
        @Override
        public void actionPerformed(ActionEvent e) {
            cardPanel.removePlayer();
        }
    }
}
