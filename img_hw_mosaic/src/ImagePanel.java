import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class ImagePanel extends JPanel {
	boolean readyToPaint;
	int prevX;
	int prevY;

	public void paintComponent(Graphics g) { 
		super.paintComponent(g);
	}
	 public void paintComponent(Graphics g, Point[] originalPoints, Point[] mappedPoints){
		 paintComponent(g, originalPoints, new Color(255, 0, 0));
		 paintComponent(g, mappedPoints, new Color(0, 255, 0));
	 }
	 
	 public void paintComponent(Graphics g, Point [] points, Color color){
		 g.setColor(color);
		 g.drawLine(points[0].x, points[0].y, points[1].x, points[1].y);
		 g.drawLine(points[1].x, points[1].y, points[2].x, points[2].y);
		 g.drawLine(points[2].x, points[2].y, points[3].x, points[3].y);
		 g.drawLine(points[3].x, points[3].y, points[0].x, points[0].y);
	 }
	 
	 public void paintComponent(Graphics g, int startX, int startY, int x, int y, BufferedImage image, MosaicFrame frame) {
			if (frame == null || x < startX || y < startY) {
				g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(),null);
				return;
			}
			//super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			int showX;
			int showY;
			
			showX = Math.max(x, prevX);
			showY = Math.max(y, prevY);
			g.drawImage(image, startX, startY, showX+1 , showY+1, startX, startY, showX+1, showY+1, null);
			
			if (x < image.getWidth()|| y < image.getHeight()) {
			g2.setColor(Color.RED);
			g2.setStroke(new BasicStroke(1));
			g2.drawLine(startX, startY, startX, y);
			g2.drawLine(startX, startY, x, startY);
			g2.drawLine(x, startY, x, y);
			g2.drawLine(startX, y, x, y);
			
			}
			prevX = x;
			prevY = y;
		}
		public void paintComponent(Graphics g, Point[] originalPoints, Point[] MappedPoints, MosaicFrame frame) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.RED);
			g2.setStroke(new BasicStroke(1));
			g2.drawLine(originalPoints[0].x, originalPoints[0].y, originalPoints[1].x, originalPoints[1].y);
			g2.drawLine(originalPoints[1].x, originalPoints[1].y, originalPoints[2].x, originalPoints[2].y);
			g2.drawLine(originalPoints[2].x, originalPoints[2].y, originalPoints[3].x, originalPoints[3].y);
			g2.drawLine(originalPoints[3].x, originalPoints[3].y, originalPoints[0].x, originalPoints[0].y);
			
			g2.setColor(Color.GREEN);
			g2.drawLine(MappedPoints[0].x, MappedPoints[0].y, MappedPoints[1].x, MappedPoints[1].y);
			g2.drawLine(MappedPoints[1].x, MappedPoints[1].y, MappedPoints[2].x, MappedPoints[2].y);
			g2.drawLine(MappedPoints[2].x, MappedPoints[2].y, MappedPoints[3].x, MappedPoints[3].y);
			g2.drawLine(MappedPoints[3].x, MappedPoints[3].y, MappedPoints[0].x, MappedPoints[0].y);
		}
		
		public void paintComponent(Graphics g, BufferedImage img, Point[] originalPoints, MosaicFrame frame, Color color) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(color);
			g.drawImage(img, 0,  0, img.getWidth(), img.getHeight(), null);
			g2.setStroke(new BasicStroke(1));
			g2.drawLine(originalPoints[0].x, originalPoints[0].y, originalPoints[1].x, originalPoints[1].y);
			g2.drawLine(originalPoints[1].x, originalPoints[1].y, originalPoints[2].x, originalPoints[2].y);
			g2.drawLine(originalPoints[2].x, originalPoints[2].y, originalPoints[3].x, originalPoints[3].y);
			g2.drawLine(originalPoints[3].x, originalPoints[3].y, originalPoints[0].x, originalPoints[0].y);
			
			
		}
		
}
