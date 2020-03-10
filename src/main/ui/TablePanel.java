package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class TablePanel extends JPanel {
    public static final int WIDTH = 856;
    public static final int HEIGHT = 856;

    public TablePanel() {
        super(new BorderLayout());
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.gray);

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image background = getImage("images/poker_table.jpeg");
        Image card = getImage("images/freeslot.png");
        g.drawImage(background, 0, 0, null);
        g.drawImage(card, 0, HEIGHT / 2 - 100, null);
        g.drawImage(card, WIDTH - 144, HEIGHT / 2 - 100, null);
    }

    private Image getImage(String ref) {
        BufferedImage sourceImage = null;

        try {
            // get the image location
            URL url = this.getClass().getClassLoader().getResource(ref);
            if (url == null) {
                System.out.println("Failed to load: " + ref);
                System.exit(0); // exit program if file not found
            }
            sourceImage = ImageIO.read(url); // get image
        } catch (IOException e) {
            System.out.println("Failed to load: " + ref);
            System.exit(0); // exit program if file not loaded
        } // catch

        // create an accelerated image (correct size) to store our sprite in
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        Image image = gc.createCompatibleImage(sourceImage.getWidth(), sourceImage.getHeight(), Transparency.BITMASK);

        // draw our source image into the accelerated image
        image.getGraphics().drawImage(sourceImage, 0, 0, null);

        return image;
    }
}
