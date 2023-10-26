
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MorFrame extends JFrame {
	String filename = "mor_3.png";
	int count;
	boolean check;
	JPanel cotrolPanel;
	JPanel imagePanelLeft;
	JPanel imagePanelRight;
	int[][] binary;
	int[][] newBinary;
	int black = Util.makeColor(0, 0, 0); // 表示白色
	JButton btnShow;
	JButton btnDilate;
	JButton btnErode;
	JButton btnOpen;
	JButton btnClose;
	JButton btnReset;
	JLabel lbCount;
	JTextField tfCount;
	JLabel lbOpenCount;
	JTextField tfOpenCount;
	JLabel lbCloseCount;
	JTextField tfCloseCount;
	int[][][] data;
	int height;
	int width;
	static BufferedImage img = null;
	static BufferedImage imgMor = null;

	MorFrame() {
		setTitle("Morphological Image Processing (Homework)");
		loadImg();
		setLayout(null);
		btnShow = new JButton("Show Original Image");
		btnDilate = new JButton("Dilate");
		btnErode = new JButton("Erode");
		lbCount = new JLabel("Count");
		tfCount = new JTextField(5);
		tfCount.setEditable(false);
		btnOpen = new JButton("Open");
		lbOpenCount = new JLabel("Times (Open)");
		tfOpenCount = new JTextField(5);
		btnClose = new JButton("Close");
		lbCloseCount = new JLabel("Times (Close)");
		tfCloseCount = new JTextField(5);
		btnReset = new JButton("Reset");
		cotrolPanel = new JPanel();
		cotrolPanel.setBounds(0, 0, 1500, 200);
		getContentPane().add(cotrolPanel);
		cotrolPanel.add(btnShow);
		cotrolPanel.add(btnDilate);
		cotrolPanel.add(btnErode);
		cotrolPanel.add(lbCount);
		cotrolPanel.add(tfCount);
		cotrolPanel.add(btnOpen);
		cotrolPanel.add(lbOpenCount);
		cotrolPanel.add(tfOpenCount);
		cotrolPanel.add(btnClose);
		cotrolPanel.add(lbCloseCount);
		cotrolPanel.add(tfCloseCount);
		cotrolPanel.add(btnReset);
		imagePanelLeft = new ImagePanel();
		imagePanelLeft.setBounds(0, 120, 700, 700);
		getContentPane().add(imagePanelLeft);
		imagePanelRight = new ImagePanel();
		imagePanelRight.setBounds(750, 120, 700, 700);
		getContentPane().add(imagePanelRight);

		btnShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadImg();
				Graphics g = imagePanelLeft.getGraphics();
				g.drawImage(img, 0, 0, null);
				tfCount.setText(count + "");
			}
		});

		btnDilate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dilate();
				tfCount.setText("" + count);
				Graphics g = imagePanelRight.getGraphics();
				g.clearRect(0, 0, imagePanelRight.getWidth(), imagePanelRight.getHeight()); // 重新繪圖
				g.drawImage(imgMor, 0, 0, null);
			}
		});

		btnErode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				erode();
				tfCount.setText("" + count);
				Graphics g = imagePanelRight.getGraphics();
				g.clearRect(0, 0, imagePanelRight.getWidth(), imagePanelRight.getHeight()); // 重新繪圖
				g.drawImage(imgMor, 0, 0, null);
			}
		});

		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (int i = 0; i < Integer.parseInt(tfOpenCount.getText()); i++) {
					erode();
				}
				for (int i = 0; i < Integer.parseInt(tfOpenCount.getText()); i++) {
					dilate();
				}
				tfCount.setText("" + count);
				Graphics g = imagePanelRight.getGraphics();
				g.clearRect(0, 0, imagePanelRight.getWidth(), imagePanelRight.getHeight()); // 重新繪圖
				g.drawImage(imgMor, 0, 0, null);
			}
		});

		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (int i = 0; i < Integer.parseInt(tfCloseCount.getText()); i++) {
					dilate();
				}
				for (int i = 0; i < Integer.parseInt(tfCloseCount.getText()); i++) {
					erode();
				}
				tfCount.setText("" + count);
				Graphics g = imagePanelRight.getGraphics();
				g.clearRect(0, 0, imagePanelRight.getWidth(), imagePanelRight.getHeight()); // 重新繪圖
				g.drawImage(imgMor, 0, 0, null);
			}
		});

		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				newBinary = new int[width][height];
				imgMor = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				Graphics g = imagePanelRight.getGraphics();
				g.clearRect(0, 0, imagePanelRight.getWidth(), imagePanelRight.getHeight()); // 重新繪圖
				count = 0;
				tfCount.setText("" + count);
				tfCloseCount.setText("0");
				tfOpenCount.setText("0");
			}
		});
	}

	void dilate() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if ((x + 1) < width && (x - 1) >= 0 && (y + 1) < height && (y - 1) >= 0) {
					int[] neighborRGB = new int[8];
					if (count == 0) {
						neighborRGB[0] = img.getRGB(x + 1, y);// 8鄰居的RGB
						neighborRGB[1] = img.getRGB(x + 1, y - 1);
						neighborRGB[2] = img.getRGB(x, y - 1);
						neighborRGB[3] = img.getRGB(x - 1, y - 1);
						neighborRGB[4] = img.getRGB(x - 1, y);
						neighborRGB[5] = img.getRGB(x - 1, y + 1);
						neighborRGB[6] = img.getRGB(x, y + 1);
						neighborRGB[7] = img.getRGB(x + 1, y + 1);
						if (newBinary[x][y] == 0) {
							if (img.getRGB(x, y) == black || neighborRGB[0] == black || neighborRGB[1] == black
									|| neighborRGB[2] == black || neighborRGB[3] == black || neighborRGB[4] == black
									|| neighborRGB[5] == black || neighborRGB[6] == black || neighborRGB[7] == black) {
								newBinary[x][y] = 1;
							} else {
								newBinary[x][y] = 2;
							}
						}
					} else {
						neighborRGB[0] = imgMor.getRGB(x + 1, y);// 8鄰居的RGB
						neighborRGB[1] = imgMor.getRGB(x + 1, y - 1);
						neighborRGB[2] = imgMor.getRGB(x, y - 1);
						neighborRGB[3] = imgMor.getRGB(x - 1, y - 1);
						neighborRGB[4] = imgMor.getRGB(x - 1, y);
						neighborRGB[5] = imgMor.getRGB(x - 1, y + 1);
						neighborRGB[6] = imgMor.getRGB(x, y + 1);
						neighborRGB[7] = imgMor.getRGB(x + 1, y + 1);
						if (imgMor.getRGB(x, y) == black || neighborRGB[0] == black || neighborRGB[1] == black
								|| neighborRGB[2] == black || neighborRGB[3] == black || neighborRGB[4] == black
								|| neighborRGB[5] == black || neighborRGB[6] == black || neighborRGB[7] == black) {
							newBinary[x][y] = 1;
						} else {
							newBinary[x][y] = 2;
						}
					}
				}
			}
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (newBinary[x][y] == 1) {
					imgMor.setRGB(x, y, Util.makeColor(0, 0, 0));
				} else if (newBinary[x][y] == 2) {
					imgMor.setRGB(x, y, Util.makeColor(255, 255, 255));
				}
			}
		}

		count += 1;

	}

	void erode() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if ((x + 1) < width && (x - 1) >= 0 && (y + 1) < height && (y - 1) >= 0) {
					int[] neighborRGB = new int[8];
					if (count == 0) {
						neighborRGB[0] = img.getRGB(x + 1, y);// 8鄰居的RGB
						neighborRGB[1] = img.getRGB(x + 1, y - 1);
						neighborRGB[2] = img.getRGB(x, y - 1);
						neighborRGB[3] = img.getRGB(x - 1, y - 1);
						neighborRGB[4] = img.getRGB(x - 1, y);
						neighborRGB[5] = img.getRGB(x - 1, y + 1);
						neighborRGB[6] = img.getRGB(x, y + 1);
						neighborRGB[7] = img.getRGB(x + 1, y + 1);
						if (newBinary[x][y] == 0) {
							if (img.getRGB(x, y) == black && neighborRGB[0] == black && neighborRGB[1] == black
									&& neighborRGB[2] == black && neighborRGB[3] == black && neighborRGB[4] == black
									&& neighborRGB[5] == black && neighborRGB[6] == black && neighborRGB[7] == black) {
								newBinary[x][y] = 1;
							} else {
								newBinary[x][y] = 2;
							}
						}
					} else {
						neighborRGB[0] = imgMor.getRGB(x + 1, y);// 8鄰居的RGB
						neighborRGB[1] = imgMor.getRGB(x + 1, y - 1);
						neighborRGB[2] = imgMor.getRGB(x, y - 1);
						neighborRGB[3] = imgMor.getRGB(x - 1, y - 1);
						neighborRGB[4] = imgMor.getRGB(x - 1, y);
						neighborRGB[5] = imgMor.getRGB(x - 1, y + 1);
						neighborRGB[6] = imgMor.getRGB(x, y + 1);
						neighborRGB[7] = imgMor.getRGB(x + 1, y + 1);
						if (imgMor.getRGB(x, y) == black && neighborRGB[0] == black && neighborRGB[1] == black
								&& neighborRGB[2] == black && neighborRGB[3] == black && neighborRGB[4] == black
								&& neighborRGB[5] == black && neighborRGB[6] == black && neighborRGB[7] == black) {
							newBinary[x][y] = 1;
						} else {
							newBinary[x][y] = 2;
						}
					}
				}
			}
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (newBinary[x][y] == 1) {
					imgMor.setRGB(x, y, Util.makeColor(0, 0, 0));
				} else if (newBinary[x][y] == 2) {
					imgMor.setRGB(x, y, Util.makeColor(255, 255, 255));
				}
			}
		}

		count -= 1;
	}

	void loadImg() {
		try {
			img = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println("IO exception");
		}
		try {
			imgMor = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println("IO exception");
		}
		height = img.getHeight();
		width = img.getWidth();
		data = new int[height][width][3];
		newBinary = new int[width][height];
		imgMor = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int rgb = img.getRGB(x, y);
				data[y][x][0] = Util.getR(rgb);
				data[y][x][1] = Util.getG(rgb);
				data[y][x][2] = Util.getB(rgb);
			}
		}
	}

	public static void main(String[] args) {
		MorFrame frame = new MorFrame();
		frame.setSize(1500, 1500);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
