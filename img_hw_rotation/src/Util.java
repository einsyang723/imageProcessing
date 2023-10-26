import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Util {
	
	final static int checkPixelBounds(int value){
		if (value >255) return 255;
		if (value <0) return 0;
		return value;
 	} 
	
	//get red channel from colorspace (4 bytes)
	final static int getR(int rgb){
		  return checkPixelBounds((rgb & 0x00ff0000)>>>16);	
   }

	//get green channel from colorspace (4 bytes)
	final static int getG(int rgb){
	  return checkPixelBounds((rgb & 0x0000ff00)>>> 8);
	}
	
	//get blue channel from colorspace (4 bytes)
	final static int getB(int rgb){
		  return  checkPixelBounds(rgb & 0x000000ff);
	}
	
	final static int makeColor(int r, int g, int b){
		return (255<< 24 | r<<16 | g<<8 | b);
	}
	
	final static int covertToGray(int r, int g, int b){
		return checkPixelBounds((int) (0.2126 * r + 0.7152 * g + 0.0722 * b));		
	}

	final static double [] affine(double[][] a, double[] b){
		int aRow = a.length;
		int bRow = b.length;
		double[] result = new double[aRow];
       	
		for (int i=0; i<aRow; i++){
			for (int j=0; j<bRow; j++){
					result[i] +=  a[i][j]*b[j]; 
			}
		}
		return result;
	}
	
	final static int bilinear(int leftTop, 
			                  int rightTop, 
			                  int leftBottom, 
			                  int rightBottom,
			                  double alpha,
			                  double beta){
		double left = linear(leftTop, leftBottom, alpha);
		double right = linear(rightTop, rightBottom, alpha);
		double value = linear(left, right, beta);
		return checkPixelBounds((int)value);
	}
			
	final static double linear(double v1, double v2, double weight){
		return v1+(v2-v1)* weight; // 10 + (20-10) * 0.2
	}
	
	final static int checkImageBounds(int value, int length){
		 if (value > length-1) return length-1;
		 else if (value < 0) return 0;
		 else return value;
	}
	
}