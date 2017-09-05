package ui;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Wall extends Actor {

    private Image image;

    public Wall(int x, int y) {
        super(x, y);
        
        BufferedImage image;
		try {
			image = ImageIO.read(new File("images/wall.jpg"));
			this.setImage(image);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
