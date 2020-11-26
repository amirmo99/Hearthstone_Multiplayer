package UtilSwing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageLoader {

    public static BufferedImage loadImage(File file) {
        BufferedImage bg = null;
        try {
            bg = ImageIO.read(file);
        } catch (IOException e) {
            System.out.println("Could not read file: " + file.getAbsolutePath());
            e.printStackTrace();
        }
        return bg;
    }

    public static BufferedImage loadImage(String path) {
        return loadImage(new File(path));
    }
}
