public class Util {
	final static int checkPixelBounds(int value) {
		if (value > 255) {
			return 255;
		}
		if (value < 0) {
			return 0;
		}
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
		return checkPixelBounds((rgb & 0x0000ff));
	}

	// make ARGB color format from R, G, and B channels
	final static int makeColor(int r, int g, int b) {
		return (255 << 24 | r << 16 | g << 8 | b);
	}

	final static double[] affine(double[][] a, double[] b) {
		int aLength = a.length;
		int bLength = b.length;
		double[] ret = new double[aLength];
		// 二維的把每一個點經過affine的副程式
		for (int i = 0; i < aLength; i++) {
			for (int j = 0; j < bLength; j++) {
				ret[i] += a[i][j] * b[j];
			}
		}
		return ret;
	}

	final static int bilinear(int leftTop, int rightTop, int leftBottom, int rightBottom, double alpha, double beta) {
		double top = linear(leftTop, rightTop, alpha);
		double bottom = linear(leftBottom, rightBottom, alpha);
		return (int) linear(top, bottom, beta);
//		return checkPixelBounds((int) linear(top, bottom, beta));
	}

	final static double linear(double v1, double v2, double weight) {
		return v1 + (weight * (v2 - v1));
	}
}