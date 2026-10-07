package util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;

public class ImageHelper {

    /**
     * Load an image from the classpath (e.g., "/images/logo.png")
     */
    public static ImageIcon loadIcon(String path) {
        try {
            URL url = ImageHelper.class.getResource(path);
            if (url == null) {
                System.err.println("Image not found: " + path);
                return null;
            }
            return new ImageIcon(url);
        } catch (Exception e) {
            System.err.println("Error loading image: " + path + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Load and scale an image to a target size
     */
    public static ImageIcon loadScaledIcon(String path, int width, int height) {
        try {
            URL url = ImageHelper.class.getResource(path);
            if (url == null) {
                System.err.println("Image not found: " + path);
                return null;
            }
            BufferedImage original = ImageIO.read(url);
            Image scaled = original.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            System.err.println("Error scaling image: " + path + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Set the window icon on any JFrame
     */
    public static void setWindowIcon(JFrame frame) {
        ImageIcon icon = loadScaledIcon("/images/icon.png", 32, 32);
        if (icon != null) frame.setIconImage(icon.getImage());
    }
}