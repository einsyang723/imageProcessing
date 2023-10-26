import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class ImagePanel extends JPanel {
	boolean readyToPaint;
	
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
	 
	 
}
