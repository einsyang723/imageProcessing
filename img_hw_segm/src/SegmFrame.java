import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SegmFrame extends JFrame {
	String filename = "segm_2.png";
	String title = "Segmentation (Choose one of the three methods)";
	JPanel cotrolPanel;
	JPanel imagePanelLeft;
	JPanel imagePanelRight;
	JButton btnShow;
	JButton btnSegm;
	JButton btnNext;
	JButton btnPrev;

	int[][][] data;
	int height;
	int width;
	int objIndex = 1; // 表示偵測的物件的編號
	int nowIndex = 1; // 表示當前索引值
	int white = Util.makeColor(255, 255, 255); // 表示白色
	int[][] segData; // 紀錄物件編號的陣列
	Queue<int[]> queue = new LinkedList<int[]>();
	int[] seed = { 0, 0 }; // 紀錄起始位置的陣列
	boolean[][] visited; //紀錄是否有掃過的陣列
	static BufferedImage img = null;
	static BufferedImage imgNew = null;

	SegmFrame() {
		setTitle(title);
		setLayout(null);
		btnShow = new JButton("Show Original Image");
		btnSegm = new JButton("Segment");
		btnNext = new JButton("Next Object");
		btnPrev = new JButton("Prev Object");
		cotrolPanel = new JPanel();
		cotrolPanel.setBounds(0, 0, 1500, 200);
		getContentPane().add(cotrolPanel);
		cotrolPanel.add(btnShow);
		cotrolPanel.add(btnSegm);
		cotrolPanel.add(btnNext);
		cotrolPanel.add(btnPrev);
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
			}
		});

		btnSegm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				imgNew = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				for (int i = 0; i < height; i++) {
					for (int j = 0; j < width; j++) {
//						因為背景都是白色，所以白色不用掃
//						visited表示是否已有紀錄
						if (img.getRGB(j, i) != white && !visited[i][j]) {
							seed[0] = j; // 表示起始位置X
							seed[1] = i; // 表示起始位置Y
							region_growing(seed);
							objIndex += 1; // 物件編號+1
						}
						if (segData[i][j] == nowIndex) // 印出第一個物件
							imgNew.setRGB(j, i, Util.makeColor(data[i][j][0], data[i][j][1], data[i][j][2]));
					}
				}
				Graphics g = imagePanelRight.getGraphics();
				g.clearRect(0, 0, imagePanelRight.getWidth(), imagePanelRight.getHeight()); // 重新繪圖
				g.drawImage(imgNew, 0, 0, null);
			}
		});

		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				imgNew = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				nowIndex += 1;
				if (nowIndex == 14) // index超過要拉回來
					nowIndex = 1;
				for (int i = 0; i < height; i++) {
					for (int j = 0; j < width; j++) {
						if (segData[i][j] == nowIndex)
							imgNew.setRGB(j, i, Util.makeColor(data[i][j][0], data[i][j][1], data[i][j][2]));
					}
				}
				Graphics g = imagePanelRight.getGraphics();
				g.clearRect(0, 0, imagePanelRight.getWidth(), imagePanelRight.getHeight());
				g.drawImage(imgNew, 0, 0, null);
			}
		});

		btnPrev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				imgNew = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				nowIndex -= 1;
				if (nowIndex == 0) // index超過要拉回來
					nowIndex = 13;
				for (int i = 0; i < height; i++) {
					for (int j = 0; j < width; j++) {
						if (segData[i][j] == nowIndex)
							imgNew.setRGB(j, i, Util.makeColor(data[i][j][0], data[i][j][1], data[i][j][2]));
					}
				}
				Graphics g = imagePanelRight.getGraphics();
				g.clearRect(0, 0, imagePanelRight.getWidth(), imagePanelRight.getHeight());
				g.drawImage(imgNew, 0, 0, null);
			}
		});
	}// end of constructor

	void region_growing(int[] seed) {
		queue.offer(seed);
		while (!queue.isEmpty()) { //一直找相鄰點，直到陣列為空
			int[] current_pixel = queue.poll(); //把目前要找的點加入queue
			int x = current_pixel[0];
			int y = current_pixel[1];

			if (visited[y][x])
				continue;
			visited[y][x] = true; //標記已記錄

			int[][] neighbors = { { x + 1, y }, { x + 1, y - 1 }, { x, y - 1 }, { x - 1, y - 1 }, { x - 1, y },
					{ x - 1, y + 1 }, { x, y + 1 }, { x + 1, y + 1 } }; //8鄰居

			for (int i = 0; i < 8; i++) {
				int nx = neighbors[i][0];
				int ny = neighbors[i][1];

				if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
					if (img.getRGB(nx, ny) != white) { //如果相鄰點不是白色就要記錄
						segData[ny][nx] = objIndex; // 標記物件編號
						queue.offer(neighbors[i]); //把相鄰點加入queue以查詢它的相鄰點
					}
				}
			}
		}
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
		visited = new boolean[height][width];
		segData = new int[height][width];
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
		SegmFrame frame = new SegmFrame();
		frame.setSize(1500, 1500);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
