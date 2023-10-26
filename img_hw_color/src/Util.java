import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Util {

	final static double[] rgb2hsl(int r, int g, int b) {
		double[] hsl = new double[3];
		double newR = r  / 255.0;
		double newG =  g  / 255.0;
		double newB =  b  / 255.0;
		double Cmax = Math.max(newR, Math.max(newG, newB));
		double Cmin = Math.min(newR, Math.min(newG, newB));
		double delta = Cmax - Cmin;

		hsl[2] = (Cmax + Cmin) / 2;// lightness
		if (delta > 0 || delta < 0) {
			hsl[1] = delta / (1 - Math.abs(2 * hsl[2] - 1));// saturation
			if (Cmax == newR) {
				hsl[0] = 60 * (((newG - newB) / delta) % 6);// hue
			} else if (Cmax == newG) {
				hsl[0] = 60 * (((newB - newR) / delta) + 2);
			} else if (Cmax == newB) {
				hsl[0] = 60 * (((newR - newG) / delta) + 4);
			}
		} else {
			hsl[0] = 0;// hue
			hsl[1] = 0;// saturation
		}
		return hsl;
	}

	final static int[] hsl2rgb(double h, double s, double l) {
		double[] rgbNew = new double[3];
		double C = (1 - Math.abs(2 * l - 1)) * s;
		double X = C * (1 - Math.abs((h / 60) % 2 - 1));
		double m = l - C / 2;

		// 判斷hue角度，求(R', G', B')
		if (h >= 0 && h < 60) {
			rgbNew[0] = C;
			rgbNew[1] = X;
			rgbNew[2] = 0;
		} else if (h >= 60 && h < 120) {
			rgbNew[0] = X;
			rgbNew[1] = C;
			rgbNew[2] = 0;
		} else if (h >= 120 && h < 180) {
			rgbNew[0] = 0;
			rgbNew[1] = C;
			rgbNew[2] = X;
		} else if (h >= 180 && h < 240) {
			rgbNew[0] = 0;
			rgbNew[1] = X;
			rgbNew[2] = C;
		} else if (h >= 240 && h < 300) {
			rgbNew[0] = X;
			rgbNew[1] = 0;
			rgbNew[2] = C;
		} else if (h >= 300 && h < 360) {
			rgbNew[0] = C;
			rgbNew[1] = 0;
			rgbNew[2] = X;
		}
		int[] rgb = { checkPixelBounds((int) ((rgbNew[0] + m) * 255)), checkPixelBounds((int) ((rgbNew[1] + m) * 255)),
				checkPixelBounds(((int) ((rgbNew[2] + m) * 255))) };

		return rgb;
	}

	final static int checkPixelBounds(int value) {
		if (value > 255)
			return 255;
		if (value < 0)
			return 0;
		return value;
	}

	// get red channel from colorspace (4 bytes)
	final static int getR(int rgb) {
		return checkPixelBounds((rgb & 0x00ff0000) >>> 16);
	}

	// get green channel from colorspace (4 bytes)
	final static int getG(int rgb) {
		return checkPixelBounds((rgb & 0x0000ff00) >>> 8);
	}

	// get blue channel from colorspace (4 bytes)
	final static int getB(int rgb) {
		return checkPixelBounds(rgb & 0x000000ff);
	}

	final static int makeColor(int r, int g, int b) {
		return (255 << 24 | r << 16 | g << 8 | b);
	}

	final static int covertToGray(int r, int g, int b) {
		return checkPixelBounds((int) (0.2126 * r + 0.7152 * g + 0.0722 * b));
	}

	final static double[] affine(double[][] a, double[] b) {
		int aRow = a.length;
		int bRow = b.length;
		double[] result = new double[aRow];

		for (int i = 0; i < aRow; i++) {
			for (int j = 0; j < bRow; j++) {
				result[i] += a[i][j] * b[j];
			}
		}
		return result;
	}

	final static int bilinear(int leftTop, int rightTop, int leftBottom, int rightBottom, double alpha, double beta) {
		double left = linear(leftTop, leftBottom, alpha);
		double right = linear(rightTop, rightBottom, alpha);
		double value = linear(left, right, beta);
		return checkPixelBounds((int) value);
	}

	final static double linear(double v1, double v2, double weight) {
		return v1 + (v2 - v1) * weight; // 10 + (20-10) * 0.2
	}

	final static int checkImageBounds(int value, int length) {
		if (value > length - 1)
			return length - 1;
		else if (value < 0)
			return 0;
		else
			return value;
	}

}