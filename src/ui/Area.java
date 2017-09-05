package ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Area extends Actor {

    public Area(int x, int y) {
        super(x, y);
        
        BufferedImage image;
		try {
			image = ImageIO.read(new File("images/area.png"));
			this.setImage(image);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}