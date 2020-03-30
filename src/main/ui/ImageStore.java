package ui;

/* SpriteStore.java
 * Manages the sprites in the game.
 * Caches them for future use.
 */

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

//Manages the images of the poker analyzer and caches them for future use.
public class ImageStore {

    private static ImageStore single = new ImageStore();
    private HashMap images = new HashMap();  // key,value pairs that stores the card images

    //EFFECTS: returns the single instance of this class
    public static ImageStore get() {
        return single;
    } // get

    //EFFECTS: returns image specified by given string
    public Image getImage(String ref) {

        // if the image is already in the HashMap
        // then return it
        // Note:
        if (images.get(ref) != null) {
            return (Image) images.get(ref);
        } // if

        // else, load the image into the HashMap off the
        // hard drive (and hence, into memory)

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
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
        Image image = gc.createCompatibleImage(sourceImage.getWidth(), sourceImage.getHeight(), Transparency.BITMASK);

        // draw our source image into the accelerated image
        image.getGraphics().drawImage(sourceImage, 0, 0, null);

        // create a sprite, add it to the cache and return it
        images.put(ref, image);

        return image;
    } // getImage

} // ImageStore