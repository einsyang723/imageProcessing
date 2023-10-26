
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FilterFrame_hw extends JFrame {
	JPanel cotrolPanelMain = new JPanel();
	JPanel cotrolPanelShow = new JPanel();;
	JPanel cotrolPanelLP = new JPanel();
	JPanel cotrolPanelHP = new JPanel();
	ImagePanel imagePanel;
	ImagePanel imagePanel2;
	JButton btnShow;
	JButton btnLP = new JButton("Low-Pass(Blur)");
	JButton btnHP = new JButton("High-Pass(Sharp)");
	int[][][] data;
	int[][][] newData;
	int height;
	int width;
	BufferedImage img = null;
	BufferedImage imgNew = null;

	FilterFrame_hw() {
		setBounds(0, 0, 1500, 1500);
		getContentPane().setLayout(null);
		setTitle("HW 5: Image Filters 2023/04/06");
		try {
			img = ImageIO.read(new File("Munich.png"));
		} catch (IOException e) {
			System.out.println("IO exception");
		}
		btnShow = new JButton("Show");
		cotrolPanelMain = new JPanel();
		cotrolPanelMain.setBounds(0, 0, 1200, 200);
		getContentPane().add(cotrolPanelMain);
		cotrolPanelShow.add(btnShow);
		cotrolPanelShow.add(btnLP);
		cotrolPanelShow.add(btnHP);
		cotrolPanelMain.add(cotrolPanelShow);
		imagePanel = new ImagePanel();
		imagePanel.setBounds(20, 220, 700, 700);
		getContentPane().add(imagePanel);
		imagePanel2 = new ImagePanel();
		imagePanel2.setBounds(720, 220, 700, 700);
		getContentPane().add(imagePanel2);

		btnShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					img = ImageIO.read(new File("Munich.png"));
				} catch (IOException e) {
					System.out.println("IO exception");
				}
				height = img.getHeight();
				width = img.getWidth();
				data = new int[height][width][3];
				newData = new int[height + 2][width + 2][3];
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int rgb = img.getRGB(x, y);
						data[y][x][0] = Util.getR(rgb);
						data[y][x][1] = Util.getG(rgb);
						data[y][x][2] = Util.getB(rgb);
						// 邊緣補0(x+1, y+1)
						newData[y + 1][x + 1][0] = Util.checkPixelBounds((int) data[y][x][0] / 9);
						newData[y + 1][x + 1][1] = Util.checkPixelBounds((int) data[y][x][1] / 9);
						newData[y + 1][x + 1][2] = Util.checkPixelBounds((int) data[y][x][2] / 9);
					}
				}
				Graphics g = imagePanel.getGraphics();
				imagePanel.paintComponent(g);
				g.drawImage(img, 0, 0, null);
			}
		});

		btnLP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				imgNew = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int rgb = 0;
						// 在邊緣補0，用3*3的mask(全部除以9，再相加)
						rgb = Util.makeColor(newData[y][x][0], newData[y][x][1], newData[y][x][2])
								+ Util.makeColor(newData[y][x + 1][0], newData[y][x + 1][1], newData[y][x + 1][2])
								+ Util.makeColor(newData[y][x + 2][0], newData[y][x + 2][1], newData[y][x + 2][2])
								+ Util.makeColor(newData[y + 1][x][0], newData[y + 1][x][1], newData[y + 1][x][2])
								+ Util.makeColor(newData[y + 1][x + 1][0], newData[y + 1][x + 1][1],
										newData[y + 1][x + 1][2])
								+ Util.makeColor(newData[y + 1][x + 2][0], newData[y + 1][x + 2][1],
										newData[y + 1][x + 2][2])
								+ Util.makeColor(newData[y + 2][x][0], newData[y + 2][x][1], newData[y + 2][x][2])
								+ Util.makeColor(newData[y + 2][x + 1][0], newData[y + 2][x + 1][1],
										newData[y + 2][x + 1][2])
								+ Util.makeColor(newData[y + 2][x + 2][0], newData[y + 2][x + 2][1],
										newData[y + 2][x + 2][2]);
						imgNew.setRGB(x, y, rgb);
					}
				}
				Graphics g = imagePanel2.getGraphics();
				imagePanel2.paintComponent(g);
				g.drawImage(imgNew, 0, 0, null);
			}
		});

		btnHP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				imgNew = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						// e-e'是高頻訊號，分r g b三個通道做
						int r = newData[y][x][0] + newData[y][x + 1][0] + newData[y][x + 2][0]
								+ newData[y + 1][x + 1][0] + newData[y + 1][x + 2][0] + newData[y + 1][x + 2][0]
								+ newData[y + 2][x][0] + newData[y + 2][x + 1][0] + newData[y + 2][x + 2][0];
						int g = newData[y][x][1] + newData[y][x + 1][1] + newData[y][x + 2][1]
								+ newData[y + 1][x + 1][1] + newData[y + 1][x + 2][1] + newData[y + 1][x + 2][1]
								+ newData[y + 2][x][1] + newData[y + 2][x + 1][1] + newData[y + 2][x + 2][1];
						int b = newData[y][x][2] + newData[y][x + 1][2] + newData[y][x + 2][2]
								+ newData[y + 1][x + 1][2] + newData[y + 1][x + 2][2] + newData[y + 1][x + 2][2]
								+ newData[y + 2][x][2] + newData[y + 2][x + 1][2] + newData[y + 2][x + 2][2];
						// 高頻訊號+加上原始訊號=清晰訊號
						r = Util.checkPixelBounds(Util.checkPixelBounds(data[y][x][0] - r) + data[y][x][0]);
						g = Util.checkPixelBounds(Util.checkPixelBounds(data[y][x][1] - g) + data[y][x][1]);
						b = Util.checkPixelBounds(Util.checkPixelBounds(data[y][x][2] - b) + data[y][x][2]);
						int newRGB = Util.makeColor(r, g, b);
						imgNew.setRGB(x, y, newRGB);
					}
				}
				Graphics g = imagePanel2.getGraphics();
				imagePanel2.paintComponent(g);
				g.drawImage(imgNew, 0, 0, null);
			}
		});
	}// end of the constructor

	public static void main(String[] args) {
		FilterFrame_hw frame = new FilterFrame_hw();
		frame.setSize(1500, 1500);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
