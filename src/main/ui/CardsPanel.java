package ui;

import model.Card;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class CardsPanel extends JPanel {
    ArrayList<Card> deck;

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

    public CardsPanel(ArrayList<Card> deck) {
        super(new BorderLayout());
        this.deck = deck;
        setPreferredSize(new Dimension(GUI.WIDTH, 400));
    }

    @Override
    public void paintComponent(Graphics g) {
        //dir.isDirectory();
        super.paintComponent(g);
        int y = 0;
        for (int j = 0; j < 4; j++) {
            int x = 0;
            for (int i = 0; i < 13; i++) {
                g.drawImage(deck.get(i + 13 * j).getImage(), x, y, 59, 90, null);
                x += 60;
            }
            y += 100;
        }
        //getImage(g);
    }


//    private void getImage(Graphics g) {
//        int spacingY = 0;
//        for (String s: EXTENSIONS) {
//            int spacingX = 0;
//            for (String v: VALUES) {
//                BufferedImage sourceImage = null;
//                String ref = "images/cards/" + v + s + ".png";
//                try {
//                    // get the image location
//                    URL url = this.getClass().getClassLoader().getResource(ref);
//                    if (url == null) {
//                        System.out.println("Failed to load: " + ref);
//                        System.exit(0); // exit program if file not found
//                    }
//                    sourceImage = ImageIO.read(url); // get image
//                } catch (IOException e) {
//                    System.out.println("Failed to load: " + ref);
//                    System.exit(0); // exit program if file not loaded
//                } // catch
//
//                // create an accelerated image (correct size) to store our sprite in
//                GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
//                Image image = gc.createCompatibleImage(sourceImage.getWidth(), sourceImage.getHeight(), Transparency.BITMASK);
//
//                // draw our source image into the accelerated image
//                image.getGraphics().drawImage(sourceImage, 0, 0, null);
//
//                g.drawImage(sourceImage, spacingX, spacingY, 59, 90, null);
//                spacingX += 60;
//            }
//            spacingY += 100;
//        }
//        int spacing = 0;
//        System.out.println(dir.isDirectory());
//        if (dir.isDirectory()) { // make sure it's a directory
//            for (final File f : dir.listFiles(IMAGE_FILTER)) {
//                BufferedImage img = null;
//
//                try {
//                    img = ImageIO.read(f);
//                } catch (IOException e) {
//                    System.out.println("Failed to load");
//                    System.exit(0); // exit program if file not loaded
//                } // catch
//
//                // create an accelerated image (correct size) to store our sprite in
//                GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
//                Image image = gc.createCompatibleImage(img.getWidth(), img.getHeight(), Transparency.BITMASK);
//
//                // draw our source image into the accelerated image
//                image.getGraphics().drawImage(img, 0, 0, null);
//
//                g.drawImage(img, spacing, 0, null);
//                spacing += 50;
//            }
//        }
//    }
}
