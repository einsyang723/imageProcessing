import java.awt.Graphics;
import java.awt.GridLayout;
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

public class AffineFrame_hw extends JFrame {
	JPanel cotrolPanelMain = new JPanel();
	JPanel cotrolPanelShow = new JPanel();;
	JPanel cotrolPanelBackColor = new JPanel();;
	JPanel cotrolPanelTranslate = new JPanel();;
	JPanel cotrolPanelScale = new JPanel();
	JPanel cotrolPanelRotate = new JPanel();
	JPanel cotrolPanelShear = new JPanel();;
	ImagePanel imagePanel;
	JButton btnShow;
	JButton btnTranslate;
	JButton btnScale;
	BufferedImage img;
	BufferedImage newImg;
	JTextField tfDeltaX = new JTextField(5);
	JTextField tfDeltaY = new JTextField(5);
	JTextField tfAmpX = new JTextField(5);
	JTextField tfAmpY = new JTextField(5);
	JLabel lbDeltaY = new JLabel("y軸位移");
	JLabel lbDeltaX = new JLabel("x軸位移");
	JLabel lbAmpX = new JLabel("x軸倍率");
	JLabel lbAmpY = new JLabel("y軸倍率");
	final int[][][] data;
	int height;
	int width;

	AffineFrame_hw() {
		setBounds(0, 0, 1500, 1500);
		getContentPane().setLayout(null);
		tfDeltaX.setText("0");
		tfDeltaY.setText("0");
		tfAmpX.setText("1.0");
		tfAmpY.setText("1.0");
		setTitle("Affine Transform Homework");
		try {
			img = ImageIO.read(new File("plate.png"));
		} catch (IOException e) {
			System.out.println("IO exception");
		}
		height = img.getHeight();
		width = img.getWidth();
		data = new int[height][width][3];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int rgb = img.getRGB(x, y);
				data[y][x][0] = Util.getR(rgb);
				data[y][x][1] = Util.getG(rgb);
				data[y][x][2] = Util.getB(rgb);
			}
		}

		btnShow = new JButton("顯示");
		btnTranslate = new JButton("平移");
		btnScale = new JButton("放大/縮小");
		cotrolPanelMain = new JPanel();
		cotrolPanelMain.setLayout(new GridLayout(1, 7));
		cotrolPanelShow.add(btnShow);
		cotrolPanelMain.add(cotrolPanelShow);
		cotrolPanelMain.add(cotrolPanelBackColor);
		cotrolPanelTranslate.add(lbDeltaX);
		cotrolPanelTranslate.add(tfDeltaX);
		cotrolPanelTranslate.add(lbDeltaY);
		cotrolPanelTranslate.add(tfDeltaY);
		cotrolPanelTranslate.add(btnTranslate);
		cotrolPanelMain.add(cotrolPanelTranslate);
		cotrolPanelScale.add(lbAmpX);
		cotrolPanelScale.add(tfAmpX);
		cotrolPanelScale.add(lbAmpY);
		cotrolPanelScale.add(tfAmpY);
		cotrolPanelScale.add(btnScale);
		cotrolPanelMain.add(cotrolPanelScale);
		cotrolPanelMain.add(cotrolPanelRotate);
		cotrolPanelMain.add(cotrolPanelShear);
		cotrolPanelMain.add(new JPanel());
		cotrolPanelMain.add(new JPanel());
		cotrolPanelMain.add(new JPanel());
		cotrolPanelMain.setBounds(0, 0, 1200, 150);
		getContentPane().add(cotrolPanelMain);
		imagePanel = new ImagePanel();
		imagePanel.setBounds(0, 180, 1500, 1500);
		getContentPane().add(imagePanel);

		btnShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					img = ImageIO.read(new File("plate.png"));
				} catch (IOException e) {
					System.out.println("IO exception");
				}
				Graphics g = imagePanel.getGraphics();
				imagePanel.paintComponent(g);
				g.drawImage(img, 0, 0, null);
			}
		});

		btnTranslate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int DeltaX = Integer.valueOf(tfDeltaX.getText());
				int DeltaY = Integer.valueOf(tfDeltaY.getText());
				Graphics g = imagePanel.getGraphics();
				imagePanel.paintComponent(g);
				g.drawImage(img, DeltaX, DeltaY, null);
			}
		});

		btnScale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				double AmpX = Double.parseDouble(tfAmpX.getText());
				double AmpY = Double.parseDouble(tfAmpY.getText());
				int newWidth = (int) (width * AmpX);
				int newHeight = (int) (height * AmpY);
				int rgb;
				newImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
				// 伴隨矩陣是{{AmpY, 0, 0}, {0, AmpX, 0}, {0, 0, AmpX*AmpY}}
				// 行列式值是AmpX * AmpY * 1
				// 反矩陣 = adj(A) / det(A)
				double[][] matrix = { { 1 / AmpX, 0.0, 0.0 }, { 0.0, 1 / AmpY, 0.0 }, { 0.0, 0.0, 1 } };
				for (int y = 0; y < newHeight; y++) {
					for (int x = 0; x < newWidth; x++) {
						double[] position = { x, y, 1.0 };
						double oldX = Util.affine(matrix, position)[0];
						double oldY = Util.affine(matrix, position)[1];
						if (oldX < width && oldX >= 0 && oldY < height && oldY >= 0) {
							int leftTopY = (int) Math.floor(oldY);
							int leftTopX = (int) Math.floor(oldX);
							int rightBottomY = (int) Math.ceil(oldY);
							int rightBottomX = (int) Math.ceil(oldX);
							if (rightBottomY >= height)
								rightBottomY = leftTopY;
							if (rightBottomX >= width)
								rightBottomX = leftTopX;
							int leftTop = img.getRGB(leftTopX, leftTopY);
							int rightTop = img.getRGB(rightBottomX, leftTopY);
							int leftBottom = img.getRGB(leftTopX, rightBottomY);
							int rightBottom = img.getRGB(rightBottomX, rightBottomY);
//							// 判斷是不是整數
//							// 不是的話要用Bilinear
							if (leftTopX == rightBottomX || leftTopY == rightBottomY) {
								newImg.setRGB(x, y, img.getRGB((int) oldX, (int) oldY));
							} else {
								rgb = Util.bilinear(leftTop, rightTop, leftBottom, rightBottom, x - leftTopX, leftTopY);
								newImg.setRGB(x, y, rgb);
							}
						}
					}
				}
				Graphics g = imagePanel.getGraphics();
				imagePanel.paintComponent(g);
				g.drawImage(newImg, 0, 0, null);
			}
		});
	}

//	public static void intOrNot(double x, double y) {
//		if ((x * 10) % 10 > 0 || (y * 10) % 10 > 0) {
//			int leftTop = Util.makeColor(data[(int) Math.floor(y)][(int) Math.floor(x)][0],
//					data[(int) Math.floor(y)][(int) Math.floor(x)][1],
//					data[(int) Math.floor(y)][(int) Math.floor(x)][2]);
//			int rightTop = Util.makeColor(data[(int) Math.floor(y)][(int) Math.ceil(x)][0],
//					data[(int) Math.floor(y)][(int) Math.ceil(x)][1], data[(int) Math.floor(y)][(int) Math.ceil(x)][2]);
//			int leftBottom = Util.makeColor(data[(int) Math.ceil(y)][(int) Math.floor(x)][0],
//					data[(int) Math.ceil(y)][(int) Math.floor(x)][1], data[(int) Math.ceil(y)][(int) Math.floor(x)][2]);
//			int rightBottom = Util.makeColor(data[(int) Math.ceil(y)][(int) Math.ceil(x)][0],
//					data[(int) Math.ceil(y)][(int) Math.ceil(x)][1], data[(int) Math.ceil(y)][(int) Math.ceil(x)][2]);
//			rgb = Util.bilinear(leftTop, rightTop, leftBottom, rightBottom, x - Math.floor(x), y - Math.floor(y));
//		} else {
//			int r = data[(int) oldY][(int) oldX][0];
//			int g = data[(int) oldY][(int) oldX][1];
//			int b = data[(int) oldY][(int) oldX][2];
//			rgb = Util.makeColor(r, g, b);
//		}
//	}

	public static void main(String[] args) {
		AffineFrame_hw frame = new AffineFrame_hw();
		frame.setSize(1500, 1500);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
