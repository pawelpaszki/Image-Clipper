package utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

public class ImageRotator {
	
	private static final int fullyTransparentColor = new Color(0, 0, 0, 0).getRGB();
	public static BufferedImage rotate(BufferedImage image, double degreeAngle) {
		double angle = Math.toRadians(degreeAngle);
	    double sin = Math.abs(Math.sin(degreeAngle)), cos = Math.abs(Math.cos(degreeAngle));
	    int w = image.getWidth(), h = image.getHeight();
	    int neww = (int)Math.floor(w*cos+h*sin), newh = (int) Math.floor(h * cos + w * sin);
	    GraphicsConfiguration gc = getDefaultConfiguration();
	    BufferedImage tempImage = gc.createCompatibleImage(neww, newh, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = tempImage.createGraphics();
	    g.translate((neww - w) / 2, (newh - h) / 2);
	    g.rotate(angle, w / 2, h / 2);
	    g.drawRenderedImage(image, null);
	    g.dispose();
	    int maxX = 0, minX = tempImage.getWidth(), maxY = 0, minY = tempImage.getHeight();
	    for(int x = 0; x < tempImage.getWidth(); x++) {
	    	for (int y = 0; y < tempImage.getHeight(); y++) {
	    		if(tempImage.getRGB(x, y) != fullyTransparentColor) {
	    			if(x > maxX) {
	    				maxX = x;
	    			}
	    			if(x < minX) {
	    				minX = x;
	    			}
	    			if(y > maxY) {
	    				maxY = y;
	    			}
	    			if(y < minY) {
	    				minY = y;
	    			}
	    		}
	    	}
	    }
	    BufferedImage finalImage = new BufferedImage(maxX - minX + 1, maxY - minY + 1, BufferedImage.TYPE_INT_ARGB);
	    for(int x = minX, x1 = 0; x <= maxX; x++, x1++) {
	    	for(int y = minY, y1 = 0; y <= maxY; y++, y1++) {
	    		finalImage.setRGB(x1, y1, tempImage.getRGB(x, y));
	    	}
	    }
	    return finalImage;
	}

	private static GraphicsConfiguration getDefaultConfiguration() {
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice gd = ge.getDefaultScreenDevice();
	    return gd.getDefaultConfiguration();
	}
}
