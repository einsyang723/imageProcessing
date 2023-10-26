import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MosaicFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	String filename = "f16.png";
	String title = "Home Work: Mosaic Processing";
	static MosaicFrame frame;
	int SIZE = 10;// size of the Mosaic block

	enum State {
		BEGIN, DRAG_ORG, ADJUSTABLE, POINT_SELECTED, FINISH_SELECTION
	};

	enum MouseState {
		PRESSED, RELEASED
	};

	State state;
	MouseState mState;
	JPanel cotrolPanelMain = new JPanel();
	JPanel cotrolPanelShow = new JPanel();;
	ImagePanel imagePanel;
	ImagePanel imagePanel2;
	JButton btnShow = new JButton("Show Original Image");
	JButton btnMosaic = new JButton("Show Mosaic Image");
	int[][][] data;
	int[][][] newData;
	int height;
	int width;
	BufferedImage img = null;
	BufferedImage imgNew = null;
	Point[] originalPoints;
	boolean adjustable;
	int targetX;
	int targetY;
	int boundX;
	int boundY;

	MosaicFrame() {
		setBounds(0, 0, 1500, 1500);
		getContentPane().setLayout(null);
		setTitle(title);
		cotrolPanelMain = new JPanel();
		cotrolPanelMain.setLayout(new GridLayout(6, 1));
		cotrolPanelShow.add(btnShow);
		cotrolPanelShow.add(btnMosaic);
		cotrolPanelMain.add(cotrolPanelShow);
		cotrolPanelMain.setBounds(0, 0, 1200, 200);
		getContentPane().add(cotrolPanelMain);
		state = State.BEGIN;

		btnShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadImg();
				Util.drawImg(imagePanel, img);
			}
		});

		btnMosaic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				imgNew = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						if (x == targetX && y == targetY && x < boundX && y < boundY) {
							average(x, y, SIZE);// 在範圍內取平均數(模糊)
							// 每次average都表示targetX已掃完，所以把targetX位移SIZE
							targetX += SIZE;
						}
						imgNew.setRGB(x, y, Util.makeColor(newData[y][x][0], newData[y][x][1], newData[y][x][2]));
					}
					// 表示原targetY範圍已掃完，要把targetX位移回滑鼠選取的起點X，targetY位移SIZE
					if (y == targetY) {
						targetY += SIZE;
						targetX = originalPoints[0].x;
					}
				}
				Graphics g = imagePanel2.getGraphics();
				imagePanel2.paintComponent(g);
				g.drawImage(imgNew, 0, 0, null);
			}

			// 在範圍內取平均數(模糊)
			private void average(int x, int y, int size) {
				int indexR = 0;// 將rgb分別累加
				int indexG = 0;
				int indexB = 0;
				if ((x + size) < width && (y + size) < height) {
					for (int i = 0; i < size; i++) {
						for (int j = 0; j < size; j++) {
							indexR += data[y + i][x + j][0];
							indexG += data[y + i][x + j][1];
							indexB += data[y + i][x + j][2];
						}
					}
					for (int i = 0; i < size; i++) {
						for (int j = 0; j < size; j++) {
							// 把RGB分別平均後存進newData中
							newData[y + i][x + j][0] = Util.checkPixelBounds((int) indexR / (size * size));
							newData[y + i][x + j][1] = Util.checkPixelBounds((int) indexG / (size * size));
							newData[y + i][x + j][2] = Util.checkPixelBounds((int) indexB / (size * size));
						}
					}
				}
			}
		});

	}// end of the constructor

	Point[] makePoints(int left, int top, int right, int bottom) {
		Point[] points = new Point[4];
		points[0] = new Point(left, top);
		points[1] = new Point(right, top);
		points[2] = new Point(right, bottom);
		points[3] = new Point(left, bottom);
		return points;
	}

	void loadImg() {
		try {
			img = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println("IO exception");
		}

		height = img.getHeight();
		width = img.getWidth();
		data = new int[height][width][3];
		newData = new int[height][width][3];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int rgb = img.getRGB(x, y);
				data[y][x][0] = newData[y][x][0] = Util.getR(rgb);
				data[y][x][1] = newData[y][x][1] = Util.getG(rgb);
				data[y][x][2] = newData[y][x][2] = Util.getB(rgb);
			}
		}
		imagePanel = new ImagePanel();
		imagePanel.setBounds(20, 220, width, height);
		getContentPane().add(imagePanel);
		imagePanel2 = new ImagePanel();
		imagePanel2.setBounds(750, 220, width, height);
		getContentPane().add(imagePanel2);
		MA ma = new MA();
		imagePanel.addMouseListener(ma);
		imagePanel.addMouseMotionListener(ma);
	}

	class MA extends MouseAdapter {
		boolean mousePressed;
		boolean mouseReleased;
		int startX;
		int startY;
		int endX;
		int endY;
		int dragPoint = -1;

		public void mouseDragged(MouseEvent arg0) {
			// begin to draw a rectangle
			if (state == State.BEGIN) {
				startX = arg0.getX();
				startY = arg0.getY();
				state = State.DRAG_ORG;
				return;
			}
			int x = arg0.getX();
			int y = arg0.getY();

			// update original rectangle
			if (state == State.DRAG_ORG) {
				imagePanel.paintComponent(imagePanel.getGraphics(), startX, startY, x, y, img, frame);
			}
			endX = x;
			endY = y;
		}

		public void mousePressed(MouseEvent arg0) {
			mState = MouseState.PRESSED;
		}

		public void mouseReleased(MouseEvent arg0) {
			mState = MouseState.RELEASED;
			// finish the original rectangle
			if (state == State.DRAG_ORG) {
				originalPoints = makePoints(startX, startY, endX, endY);
				// 標記要開始馬賽克和結束的點
				targetX = originalPoints[0].x;
				targetY = originalPoints[0].y;
				boundX = originalPoints[2].x;
				boundY = originalPoints[2].y;
				adjustable = true;
				state = State.ADJUSTABLE;
			}
		}
	}

	public static void main(String[] args) {
		frame = new MosaicFrame();
		frame.setSize(1500, 1500);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
