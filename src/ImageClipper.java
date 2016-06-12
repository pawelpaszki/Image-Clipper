import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageClipper implements ActionListener, MouseListener {

	private JFrame mainWindow;
	private JPanel selectTabPanel;
	private JScrollPane imageToHighlightScrollPane;
	private JLayeredPane imageToHighlightLayeredPane;
	private JLabel imageToHighlightLabel;
	private JLabel imageToHighlightTopLabel;
	private BufferedImage topCopyFromLabelBackground;
	private JLayeredPane imageToPasteToLayeredPane;
	private JScrollPane imageToPasteToScrollPane;
	private JButton copyTo;
	private JButton copyFrom;
	private JButton loadImage;
	private boolean copyFromSelected;
	private boolean copyToSelected;
	private final String userDirLocation = System.getProperty("user.dir");
	private final File userDir = new File(userDirLocation);
	private final FileNameExtensionFilter filter = new FileNameExtensionFilter("images", "jpg", "gif", "png", "bmp");
	private BufferedImage copyFromImage;
	private int copyFromHeight;
	private int copyFromWidth;
	private final int fullyTransparentColor = new Color(0,0,0,0).getRGB();
	private JCheckBox highlightMode;

	public static void main(String[] args) {
		// standard thread invocation in swing apps
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ImageClipper clipper = new ImageClipper();
					clipper.initialise();

				} catch (Exception e) {

				}
			}
		});
	}

	private void initialise() {

		mainWindow = new JFrame("Image Clipper (developed by Pawel Paszki - pawelpaszki@gmail.com)");
		mainWindow.setSize(1200, 600);
		mainWindow.setResizable(false);
		mainWindow.setLocationRelativeTo(null);// window is centred
		mainWindow.setVisible(true); // makes the window visible
		// closes the window, when stop button is pressed
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.getContentPane().setBackground(Color.black);
		mainWindow.setLayout(null);

		selectTabPanel = new JPanel();
		selectTabPanel.setBounds(5, 5, 200, 40);
		selectTabPanel.setBorder(BorderFactory.createLineBorder(Color.white));
		selectTabPanel.setBackground(Color.black);
		selectTabPanel.setLayout(new GridLayout(1, 2));

		copyFrom = makeButton("copy from");
		copyTo = makeButton("copy to");
		selectTabPanel.add(copyFrom);
		selectTabPanel.add(copyTo);

		loadImage = makeButton("load image");
		loadImage.setBounds(225, 10, 120, 30);
		
		highlightMode = new JCheckBox("highlighting on");
		highlightMode.setBounds(355, 10, 110, 30);
		highlightMode.setBackground(Color.black);
		highlightMode.setForeground(Color.white);

		imageToHighlightLayeredPane = new JLayeredPane();

		// imageToHighlightLayeredPane.setBorder(BorderFactory.createLineBorder(Color.blue));
		// imageToHighlightLayeredPane.setPreferredSize(new Dimension(1800,
		// 1600));

		imageToHighlightScrollPane = new JScrollPane(imageToHighlightLayeredPane);
		imageToHighlightScrollPane.setBounds(5, 45, 1185, 520);
		imageToHighlightScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		imageToHighlightScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		imageToHighlightScrollPane.addMouseListener(this);
		// imageToHighlightScrollPane.setVisible(false);

		imageToPasteToLayeredPane = new JLayeredPane();

		// imageToPasteToLayeredPane.setBorder(BorderFactory.createLineBorder(Color.red));
		// imageToPasteToLayeredPane.setPreferredSize(new Dimension(1800,
		// 1600));

		imageToPasteToScrollPane = new JScrollPane(imageToPasteToLayeredPane);
		imageToPasteToScrollPane.setBounds(5, 45, 1185, 520);
		imageToPasteToScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		imageToPasteToScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		imageToPasteToScrollPane.setVisible(false);

		mainWindow.getContentPane().add(selectTabPanel);
		mainWindow.getContentPane().add(imageToHighlightScrollPane);
		mainWindow.getContentPane().add(imageToPasteToScrollPane);
		mainWindow.getContentPane().add(loadImage);
		mainWindow.getContentPane().add(highlightMode);

	}

	private JButton makeButton(String text) {
		JButton button = new JButton(text);
		button.setFocusPainted(false);
		button.addActionListener(this);
		return button;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		switch (action) {
		case "copy from":
			imageToHighlightScrollPane.setVisible(true);
			imageToPasteToScrollPane.setVisible(false);
			System.out.println("copy from");
			copyFrom.setBorder(BorderFactory.createLoweredBevelBorder());
			copyTo.setBorder(null);
			copyFromSelected = true;
			copyToSelected = false;
			break;
		case "copy to":
			imageToHighlightScrollPane.setVisible(false);
			imageToPasteToScrollPane.setVisible(true);
			System.out.println("copy to");
			copyTo.setBorder(BorderFactory.createLoweredBevelBorder());
			copyFrom.setBorder(null);
			copyFromSelected = false;
			copyToSelected = true;
			break;
		case "load image":
			if (copyFromSelected || copyToSelected) {
				JFileChooser chooser = new JFileChooser(userDir);
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(mainWindow);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					if (copyFromSelected == true) {
						File file = new File(chooser.getSelectedFile().getAbsolutePath());
						copyFromImage = null;
						try {
							copyFromImage = ImageIO.read(file);
						} catch (IOException e) {

						}
						int height = copyFromImage.getHeight();
						int width = copyFromImage.getHeight();
						imageToHighlightLayeredPane.removeAll();
						imageToHighlightLabel = new JLabel(new ImageIcon(chooser.getSelectedFile().getAbsolutePath()));
						imageToHighlightLayeredPane.setPreferredSize(new Dimension(height, width));
						setCopyFromHeight(height);
						setCopyFromWidth(width);
						imageToHighlightLabel.setSize(new Dimension(height, width));
						
						topCopyFromLabelBackground = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);
						for(int x = 0; x < width; x++) {
							for (int y = 0; y < height; y++) {
								topCopyFromLabelBackground.setRGB(x, y, fullyTransparentColor);
							}
						}
												
						imageToHighlightTopLabel = new JLabel(new ImageIcon(topCopyFromLabelBackground));
						imageToHighlightTopLabel.setSize(new Dimension(height, width));
						//imageToHighlightTopLabel.setOpaque(true);
						
						imageToHighlightLayeredPane.add(imageToHighlightLabel);
						imageToHighlightLayeredPane.add(imageToHighlightTopLabel);
						imageToHighlightLayeredPane.moveToFront(imageToHighlightTopLabel);
						imageToHighlightLayeredPane.repaint();
						imageToHighlightLayeredPane.setVisible(false);
						imageToHighlightLayeredPane.setVisible(true);

					}

				}

			}
			break;
		}

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		int x = arg0.getX();
		int y = arg0.getY();
		if (x <= getCopyFromWidth() && y <= getCopyFromHeight() && highlightMode.isSelected()) {
			System.out.println("x: " + x);
			System.out.println("y: " + y);
			System.out.println(new Color(copyFromImage.getRGB(x, y)));
		}

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public int getCopyFromHeight() {
		return copyFromHeight;
	}

	public void setCopyFromHeight(int copyFromHeight) {
		this.copyFromHeight = copyFromHeight;
	}

	public int getCopyFromWidth() {
		return copyFromWidth;
	}

	public void setCopyFromWidth(int copyFromWidth) {
		this.copyFromWidth = copyFromWidth;
	}
}
