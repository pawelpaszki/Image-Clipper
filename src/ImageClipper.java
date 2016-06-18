import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageClipper implements ActionListener, MouseMotionListener {

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
	private final int fullyTransparentColor = new Color(0, 0, 0, 0).getRGB();
	private final int highlightColor = new Color(255, 0, 0, 192).getRGB();
	private JCheckBox highlight;
	private JCheckBox unHighlight;
	private JButton copyToClipboard;
	private JComboBox<String> highlightSizePick;
	private ArrayList<Point> highlightedPixels = new ArrayList<Point>();

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

		highlight = new JCheckBox("highlight");
		highlight.setBounds(355, 10, 80, 30);
		highlight.setBackground(Color.black);
		highlight.setForeground(Color.white);
		highlight.addActionListener(this);
		highlight.setVisible(false);

		unHighlight = new JCheckBox("unHighlight");
		unHighlight.setBounds(435, 10, 100, 30);
		unHighlight.setBackground(Color.black);
		unHighlight.setForeground(Color.white);
		unHighlight.addActionListener(this);
		unHighlight.setVisible(false);

		String[] highlightSizes = { "small", "medium", "large", "x large", "xxxl" };

		// Create the combo box, select item at index 4.
		// Indices start at 0, so 4 specifies the pig.
		highlightSizePick = new JComboBox<String>(highlightSizes);
		highlightSizePick.setSelectedItem(null);
		highlightSizePick.addActionListener(this);
		highlightSizePick.setBounds(545, 15, 100, 20);
		highlightSizePick.setVisible(false);

		copyToClipboard = makeButton("copy to clipboard");
		copyToClipboard.setBounds(655, 10, 140, 30);

		imageToHighlightLayeredPane = new JLayeredPane();

		// imageToHighlightLayeredPane.setBorder(BorderFactory.createLineBorder(Color.blue));
		// imageToHighlightLayeredPane.setPreferredSize(new Dimension(1800,
		// 1600));

		imageToHighlightScrollPane = new JScrollPane(imageToHighlightLayeredPane);
		imageToHighlightScrollPane.setBounds(5, 45, 1185, 520);
		imageToHighlightScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		imageToHighlightScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		imageToHighlightLayeredPane.addMouseMotionListener(this);
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
		mainWindow.getContentPane().add(highlight);
		mainWindow.getContentPane().add(unHighlight);
		mainWindow.getContentPane().add(copyToClipboard);
		mainWindow.getContentPane().add(highlightSizePick);

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
		case "highlight":
			if (highlight.isSelected()) {
				unHighlight.setSelected(false);
				highlight.setSelected(true);
			}
			break;
		case "unHighlight":
			if (unHighlight.isSelected()) {
				highlight.setSelected(false);
				unHighlight.setSelected(true);
			}
			break;
		case "copy from":
			imageToHighlightScrollPane.setVisible(true);
			imageToPasteToScrollPane.setVisible(false);
			copyFrom.setBorder(BorderFactory.createLoweredBevelBorder());
			copyTo.setBorder(null);
			copyFromSelected = true;
			copyToSelected = false;
			if (imageToHighlightTopLabel != null && imageToHighlightTopLabel.getHeight() > 0) {
				highlightSizePick.setVisible(true);
				highlight.setVisible(true);
				unHighlight.setVisible(true);
			}
			break;
		case "copy to":
			imageToHighlightScrollPane.setVisible(false);
			imageToPasteToScrollPane.setVisible(true);
			copyTo.setBorder(BorderFactory.createLoweredBevelBorder());
			copyFrom.setBorder(null);
			copyFromSelected = false;
			copyToSelected = true;
			highlight.setVisible(false);
			unHighlight.setVisible(false);
			highlightSizePick.setVisible(false);
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
						for (int x = 0; x < width; x++) {
							for (int y = 0; y < height; y++) {
								topCopyFromLabelBackground.setRGB(x, y, fullyTransparentColor);
							}
						}

						imageToHighlightTopLabel = new JLabel(new ImageIcon(topCopyFromLabelBackground));
						imageToHighlightTopLabel.setSize(new Dimension(height, width));

						imageToHighlightLayeredPane.add(imageToHighlightLabel);
						imageToHighlightLayeredPane.add(imageToHighlightTopLabel);
						imageToHighlightLayeredPane.moveToFront(imageToHighlightTopLabel);
						imageToHighlightLayeredPane.repaint();
						imageToHighlightLayeredPane.setVisible(false);
						imageToHighlightLayeredPane.setVisible(true);
						highlight.setVisible(true);
						unHighlight.setVisible(true);
						highlightSizePick.setVisible(true);
					}

				}

			}
			break;
		}

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

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (arg0.getX() >= 0 && arg0.getX() <= getCopyFromWidth() && arg0.getY() >= 0
				&& arg0.getY() <= getCopyFromHeight() && copyFromSelected) {
			if (highlight.isSelected()) {

				switch (String.valueOf(highlightSizePick.getSelectedItem())) {
				case "small":
					highlightedPixels.add(new Point(arg0.getX(), arg0.getY()));
					topCopyFromLabelBackground.setRGB(arg0.getX(), arg0.getY(), highlightColor);
					break;
				case "medium":
					if (arg0.getX() >= 2 & (arg0.getX() + 2) <= getCopyFromWidth() & arg0.getY() >= 2
							&& (arg0.getY() + 2) <= getCopyFromHeight()) {
						for (int x = arg0.getX() - 2; x < arg0.getX() + 2; x++) {
							for (int y = arg0.getY() - 2; y < arg0.getY() + 2; y++) {
								highlightedPixels.add(new Point(x, y));
								topCopyFromLabelBackground.setRGB(x, y, highlightColor);
							}
						}
					}
					break;
				case "large":
					if (arg0.getX() >= 4 & (arg0.getX() + 4) <= getCopyFromWidth() & arg0.getY() >= 4
							&& (arg0.getY() + 4) <= getCopyFromHeight()) {
						for (int x = arg0.getX() - 3; x < arg0.getX() + 4; x++) {
							for (int y = arg0.getY() - 3; y < arg0.getY() + 4; y++) {
								highlightedPixels.add(new Point(x, y));
								topCopyFromLabelBackground.setRGB(x, y, highlightColor);
							}
						}
					}
					break;
				case "x large":
					if (arg0.getX() >= 6 & (arg0.getX() + 6) <= getCopyFromWidth() & arg0.getY() >= 6
							&& (arg0.getY() + 6) <= getCopyFromHeight()) {
						for (int x = arg0.getX() - 6; x < arg0.getX() + 7; x++) {
							for (int y = arg0.getY() - 6; y < arg0.getY() + 7; y++) {
								highlightedPixels.add(new Point(x, y));
								topCopyFromLabelBackground.setRGB(x, y, highlightColor);
							}
						}
					}
					break;
				case "xxxl":
					if (arg0.getX() >= 20 & (arg0.getX() + 20) <= getCopyFromWidth() & arg0.getY() >= 20
							&& (arg0.getY() + 20) <= getCopyFromHeight()) {
						for (int x = arg0.getX() - 20; x < arg0.getX() + 21; x++) {
							for (int y = arg0.getY() - 20; y < arg0.getY() + 21; y++) {
								highlightedPixels.add(new Point(x, y));
								topCopyFromLabelBackground.setRGB(x, y, highlightColor);
							}
						}
					}
					break;
				}
				imageToHighlightTopLabel.setVisible(false);
				imageToHighlightTopLabel.setVisible(true);
			}
		}
		System.out.println("x: " + arg0.getX());
		System.out.println("y: " + arg0.getY());
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
