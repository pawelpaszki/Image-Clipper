package view_controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

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

import model.Point;
import model.Scalr;

public class ImageClipper implements ActionListener, MouseMotionListener, MouseListener {

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
	private int copyToHeight;
	private int copyToWidth;
	private final int fullyTransparentColor = new Color(0, 0, 0, 0).getRGB();
	private final int highlightColor = new Color(255, 0, 0, 192).getRGB();
	// private final int semiTransparentColor = new Color(255, 255, 255,
	// 192).getRGB();
	private JCheckBox highlight;
	private JCheckBox unHighlight;
	private JButton copyToClipboard;
	private JComboBox<String> highlightSizePick;
	private HashSet<Point> highlightedPixels = new HashSet<Point>();
	private BufferedImage copyToImage;
	private JLabel imageToPasteLabel;
	private BufferedImage topPasteToLabelBackground;
	private JLabel imageToPasteTopLabel;
	private ArrayList<BufferedImage> clippings = new ArrayList<BufferedImage>();
	private JButton lowerIndexClipping;
	private JButton higherIndexClipping;
	private JButton pasteClipping;
	private boolean newClippingAdded;
	private ArrayList<BufferedImage> addClippingButtonIcons;
	private int currentClippingIconIndex;
	private int startX;
	private int endX;
	private int startY;
	private int endY;
	private int pressedX;
	private int pressedY;
	private BufferedImage pastedClipping;
	private JButton editImage;
	private JButton moveLeftByOne;
	private JButton moveLeftByTen;
	private JButton moveUpByOne;
	private Component moveUpByTen;
	private Component moveRightByOne;
	private JButton moveRightByTen;
	private JButton moveDownByOne;
	private JButton moveDownByTen;

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
		mainWindow.setSize(1200, 700);
		mainWindow.setResizable(false);
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setVisible(true);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.getContentPane().setBackground(Color.black);
		mainWindow.setLayout(null);

		selectTabPanel = new JPanel();
		selectTabPanel.setBounds(5, 75, 300, 40);
		selectTabPanel.setBorder(BorderFactory.createLineBorder(Color.white));
		selectTabPanel.setBackground(Color.black);
		selectTabPanel.setLayout(new GridLayout(1, 3));

		copyFrom = makeButton("copy from");
		copyTo = makeButton("copy to");
		editImage = makeButton("edit image");
		selectTabPanel.add(copyFrom);
		selectTabPanel.add(copyTo);
		selectTabPanel.add(editImage);

		loadImage = makeButton("load image");
		
		loadImage.setBounds(325, 80, 120, 30);
		loadImage.setIcon(new ImageIcon("src/resources/load_image.png"));
		copyFrom.setIcon(new ImageIcon("src/resources/copy_from.png"));
		copyTo.setIcon(new ImageIcon("src/resources/copy_to.png"));
		editImage.setIcon(new ImageIcon("src/resources/edit_image.png"));

		highlight = new JCheckBox("highlight");
		highlight.setBounds(455, 80, 80, 30);
		highlight.setBackground(Color.black);
		highlight.setForeground(Color.white);
		highlight.addActionListener(this);
		highlight.setVisible(false);

		unHighlight = new JCheckBox("unHighlight");
		unHighlight.setBounds(535, 80, 100, 30);
		unHighlight.setBackground(Color.black);
		unHighlight.setForeground(Color.white);
		unHighlight.addActionListener(this);
		unHighlight.setVisible(false);

		String[] highlightSizes = { "small", "medium", "large", "x large", "xxxl" };

		highlightSizePick = new JComboBox<String>(highlightSizes);
		highlightSizePick.setSelectedItem(null);
		highlightSizePick.addActionListener(this);
		highlightSizePick.setBounds(645, 85, 100, 20);
		highlightSizePick.setBackground(Color.black);
		highlightSizePick.setForeground(Color.white);
		highlightSizePick.setVisible(false);

		copyToClipboard = makeButton("copy to clipboard");
		copyToClipboard.setBounds(755, 80, 140, 30);
		copyToClipboard.setVisible(false);
		copyToClipboard.setIcon(new ImageIcon("src/resources/copy_to_clipboard.png"));

		imageToHighlightLayeredPane = new JLayeredPane();

		imageToHighlightScrollPane = new JScrollPane(imageToHighlightLayeredPane);
		imageToHighlightScrollPane.setBounds(5, 125, 1185, 540);
		imageToHighlightScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		imageToHighlightScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		imageToHighlightScrollPane.setOpaque(true);
		imageToHighlightScrollPane.getViewport().setBackground(Color.black);

		imageToHighlightLayeredPane.addMouseMotionListener(this);
		imageToPasteToLayeredPane = new JLayeredPane();
		imageToPasteToLayeredPane.addMouseMotionListener(this);
		imageToPasteToLayeredPane.addMouseListener(this);

		imageToPasteToScrollPane = new JScrollPane(imageToPasteToLayeredPane);
		imageToPasteToScrollPane.setBounds(5, 125, 1185, 540);
		imageToPasteToScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		imageToPasteToScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		imageToPasteToScrollPane.setVisible(false);

		lowerIndexClipping = makeButton("<");
		lowerIndexClipping.setBounds(470, 10, 20, 110);
		lowerIndexClipping.setIcon(new ImageIcon("src/resources/lower_index.png"));

		higherIndexClipping = makeButton(">");
		higherIndexClipping.setBounds(620, 10, 20, 110);
		higherIndexClipping.setIcon(new ImageIcon("src/resources/higher_index.png"));
		
		pasteClipping = makeButton("paste clipping");
		pasteClipping.setFont(new Font("Arial", Font.BOLD, 0));
		pasteClipping.setBounds(500, 10, 110, 110);

		moveLeftByOne = makeButton("left1");
		moveLeftByOne.setBounds(680, 50, 20, 30);
		moveLeftByTen = makeButton("left10");
		moveLeftByTen.setBounds(650, 50, 20, 30);
		moveUpByOne = makeButton("up1");
		moveUpByOne.setBounds(710, 10, 30, 20);
		moveUpByTen = makeButton("up10");
		moveUpByTen.setBounds(710, 35, 30, 20);
		moveRightByOne = makeButton("right1");
		moveRightByOne.setBounds(750, 50, 20, 30);
		moveRightByTen = makeButton("right10");
		moveRightByTen.setBounds(780, 50, 20, 30);
		moveDownByOne = makeButton("down1");
		moveDownByOne.setBounds(710, 75, 30, 20);
		moveDownByTen = makeButton("down10");
		moveDownByTen.setBounds(710, 100, 30, 20);
		
		showClippingChoice(false);

		mainWindow.getContentPane().add(selectTabPanel);
		mainWindow.getContentPane().add(imageToHighlightScrollPane);
		mainWindow.getContentPane().add(imageToPasteToScrollPane);
		mainWindow.getContentPane().add(highlight);
		mainWindow.getContentPane().add(unHighlight);
		mainWindow.getContentPane().add(highlightSizePick);

	}

	private JButton makeButton(String text) {
		JButton button = new JButton(text);
		button.setFocusPainted(false);
		button.addActionListener(this);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setFont(new Font("Arial", Font.BOLD, 0));
		mainWindow.getContentPane().add(button);
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
				copyToClipboard.setVisible(true);
			}
			showClippingChoice(false);
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
			copyToClipboard.setVisible(false);
			if (clippings.size() > 0) {
				setCurrentClippingIconIndex(0);
				showClippingChoice(true);
				if (isNewClippingAdded()) {
					setNewClippingAdded(false);
					addClippingButtonIcons = new ArrayList<>();
					double ratio = 0;
					double width = 0;
					double height = 0;
					for (int i = 0; i < clippings.size(); i++) {
						if (clippings.get(i).getHeight() > clippings.get(i).getWidth()) {
							ratio = clippings.get(i).getHeight() * 1.0 / 108.0;
						} else {
							ratio = clippings.get(i).getWidth() * 1.0 / 108.0;
						}
						width = clippings.get(i).getWidth() / ratio;
						height = clippings.get(i).getHeight() / ratio;
						addClippingButtonIcons.add(Scalr.resize(clippings.get(i), ((int) width), ((int) height)));
					}
					pasteClipping.setIcon(new ImageIcon(addClippingButtonIcons.get(0)));
				} else {
					pasteClipping.setIcon(new ImageIcon(addClippingButtonIcons.get(0)));
				}
			} else {
				showClippingChoice(false);
			}
			break;
		case "copy to clipboard":
			if (highlightedPixels.size() > 100) {
				ArrayList<Point> pixels = new ArrayList<Point>(highlightedPixels);
				int maxX = 0;
				int maxY = 0;
				int minX = getCopyFromWidth();
				int minY = getCopyFromHeight();
				for (int i = 0; i < pixels.size(); i++) {
					if (pixels.get(i).getX() > maxX) {
						maxX = pixels.get(i).getX();
					}
					if (pixels.get(i).getX() < minX) {
						minX = pixels.get(i).getX();
					}
					if (pixels.get(i).getY() > maxY) {
						maxY = pixels.get(i).getY();
					}
					if (pixels.get(i).getY() < minY) {
						minY = pixels.get(i).getY();
					}
				}
				System.out.println(maxX);
				System.out.println(minX);
				System.out.println(maxY);
				System.out.println(minY);
				BufferedImage clipping = new BufferedImage((maxX - minX + 1), (maxY - minY + 1),
						BufferedImage.TYPE_INT_ARGB);
				System.out.println(clipping.getHeight() + " : " + clipping.getWidth());
				for (int x = minX, clipX = 0; x < maxX; x++, clipX++) {
					for (int y = minY, clipY = 0; y < maxY; y++, clipY++) {
						if (topCopyFromLabelBackground.getRGB(x, y) != fullyTransparentColor) {
							clipping.setRGB(clipX, clipY, copyFromImage.getRGB(x, y));
						} else {
							clipping.setRGB(clipX, clipY, fullyTransparentColor);
						}
					}
				}
				clippings.add(clipping);
				makeCopyFromTopTransparent();
				highlightedPixels = new HashSet<Point>();
				repaintCopyFrom();
				setNewClippingAdded(true);
			}
			break;
		case "load image":
			if (copyFromSelected || copyToSelected) {
				JFileChooser chooser = new JFileChooser(userDir);
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(mainWindow);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					if (copyFromSelected) {
						File file = new File(chooser.getSelectedFile().getAbsolutePath());
						copyFromImage = null;
						try {
							copyFromImage = ImageIO.read(file);
						} catch (IOException e) {

						}
						int height = copyFromImage.getHeight();
						int width = copyFromImage.getWidth();
						System.out.println("height " + height);
						System.out.println("width " + width);
						imageToHighlightLayeredPane.removeAll();
						imageToHighlightLabel = new JLabel(new ImageIcon(chooser.getSelectedFile().getAbsolutePath()));
						imageToHighlightLayeredPane.setPreferredSize(new Dimension(width, height));
						setCopyFromHeight(height);
						setCopyFromWidth(width);
						imageToHighlightLabel.setSize(new Dimension(width, height));

						topCopyFromLabelBackground = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

						makeCopyFromTopTransparent();

						imageToHighlightTopLabel = new JLabel(new ImageIcon(topCopyFromLabelBackground));
						imageToHighlightTopLabel.setSize(new Dimension(width, height));

						imageToHighlightLayeredPane.add(imageToHighlightLabel);
						imageToHighlightLayeredPane.add(imageToHighlightTopLabel);
						imageToHighlightLayeredPane.moveToFront(imageToHighlightTopLabel);
						repaintCopyFrom();
						highlight.setVisible(true);
						unHighlight.setVisible(true);
						highlightSizePick.setVisible(true);
						copyToClipboard.setVisible(true);
					} else if (copyToSelected) {
						File file = new File(chooser.getSelectedFile().getAbsolutePath());
						copyToImage = null;
						try {
							copyToImage = ImageIO.read(file);
						} catch (IOException e) {

						}
						int height = copyToImage.getHeight();
						int width = copyToImage.getWidth();
						imageToPasteToLayeredPane.removeAll();
						imageToPasteLabel = new JLabel(new ImageIcon(chooser.getSelectedFile().getAbsolutePath()));
						imageToPasteToLayeredPane.setPreferredSize(new Dimension(width, height));
						setCopyToHeight(height);
						setCopyToWidth(width);
						imageToPasteLabel.setSize(new Dimension(width, height));

						topPasteToLabelBackground = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
						setCopyToTopLayerTransparent(width, height);

						imageToPasteTopLabel = new JLabel(new ImageIcon(topPasteToLabelBackground));
						imageToPasteTopLabel.setSize(new Dimension(width, height));

						imageToPasteToLayeredPane.add(imageToPasteLabel);
						imageToPasteToLayeredPane.add(imageToPasteTopLabel);
						imageToPasteToLayeredPane.moveToFront(imageToPasteTopLabel);
						repaintCopyTo();
					}

				}

			}
			break;
		case ">":
			setCurrentClippingIconIndex(getCurrentClippingIconIndex() + 1);
			lowerIndexClipping.setEnabled(true);
			if ((getCurrentClippingIconIndex() + 1) == clippings.size()) {
				higherIndexClipping.setEnabled(false);
			}
			pasteClipping.setIcon(new ImageIcon(addClippingButtonIcons.get(getCurrentClippingIconIndex())));
			break;
		case "<":
			setCurrentClippingIconIndex(getCurrentClippingIconIndex() - 1);
			higherIndexClipping.setEnabled(true);
			if (getCurrentClippingIconIndex() == 0) {
				lowerIndexClipping.setEnabled(false);
			}
			pasteClipping.setIcon(new ImageIcon(addClippingButtonIcons.get(getCurrentClippingIconIndex())));
			break;
		case "paste clipping":
			if (imageToPasteLabel != null && imageToPasteLabel.getWidth() > 0) {

				setCopyToTopLayerTransparent(imageToPasteLabel.getWidth(), imageToPasteLabel.getHeight());
				pastedClipping = null;

				int clippingHeight = clippings.get(currentClippingIconIndex).getHeight();
				int clippingWidth = clippings.get(currentClippingIconIndex).getWidth();

				if (clippingHeight > imageToPasteLabel.getHeight() || clippingWidth > imageToPasteLabel.getWidth()) {
					double widthRatio = Math.round(clippingWidth * 100.0 / imageToPasteLabel.getWidth()) / 100.0 + 0.1;
					double heightRatio = Math.round(clippingHeight * 100.0 / imageToPasteLabel.getHeight()) / 100.0
							+ 0.1;
					System.out.println("width ratio:" + widthRatio);
					System.out.println("height ratio:" + heightRatio);
					double resizeRatio = 0;
					if (widthRatio > heightRatio) {
						resizeRatio = widthRatio;
					} else {
						resizeRatio = heightRatio;
					}
					double width = clippingWidth / resizeRatio;
					double height = clippingHeight / resizeRatio;
					pastedClipping = Scalr.resize(clippings.get(currentClippingIconIndex), ((int) width),
							((int) height));

					System.out.println("image height: " + pastedClipping.getHeight());
					System.out.println("image width: " + pastedClipping.getWidth());
				} else {
					pastedClipping = clippings.get(currentClippingIconIndex);
				}
				for (int x = 0; x < pastedClipping.getWidth(); x++) {
					for (int y = 0; y < pastedClipping.getHeight(); y++) {
						topPasteToLabelBackground.setRGB(x, y, pastedClipping.getRGB(x, y));
					}
				}
				setStartX(0);
				setStartY(0);
				setEndX(pastedClipping.getWidth() - 1);
				setEndY(pastedClipping.getHeight() - 1);
				imageToPasteTopLabel.setVisible(false);
				imageToPasteTopLabel.setVisible(true);
			}
			break;
		}

	}

	private void setCopyToTopLayerTransparent(int width, int height) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				topPasteToLabelBackground.setRGB(x, y, fullyTransparentColor);
			}
		}

	}

	private void showClippingChoice(boolean arg) {
		lowerIndexClipping.setVisible(arg);
		higherIndexClipping.setVisible(arg);
		pasteClipping.setVisible(arg);
		if (clippings.size() > 1) {
			higherIndexClipping.setEnabled(true);
		} else {
			higherIndexClipping.setEnabled(false);
		}
		lowerIndexClipping.setEnabled(false);

	}

	private void repaintCopyTo() {
		imageToPasteToLayeredPane.repaint();
		imageToPasteToLayeredPane.setVisible(false);
		imageToPasteToLayeredPane.setVisible(true);
	}

	private void repaintCopyFrom() {
		imageToHighlightLayeredPane.repaint();
		imageToHighlightLayeredPane.setVisible(false);
		imageToHighlightLayeredPane.setVisible(true);
	}

	private void makeCopyFromTopTransparent() {
		for (int x = 0; x < getCopyFromWidth(); x++) {
			for (int y = 0; y < getCopyFromHeight(); y++) {
				topCopyFromLabelBackground.setRGB(x, y, fullyTransparentColor);
			}
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

	/**
	 * @return the copyToHeight
	 */
	public int getCopyToHeight() {
		return copyToHeight;
	}

	/**
	 * @param copyToHeight
	 *            the copyToHeight to set
	 */
	public void setCopyToHeight(int copyToHeight) {
		this.copyToHeight = copyToHeight;
	}

	/**
	 * @return the copyToWidth
	 */
	public int getCopyToWidth() {
		return copyToWidth;
	}

	/**
	 * @param copyToWidth
	 *            the copyToWidth to set
	 */
	public void setCopyToWidth(int copyToWidth) {
		this.copyToWidth = copyToWidth;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (arg0.getX() >= 0 && arg0.getX() <= getCopyFromWidth() && arg0.getY() >= 0
				&& arg0.getY() <= getCopyFromHeight() && copyFromSelected) {
			if (highlight.isSelected() || unHighlight.isSelected()) {

				switch (String.valueOf(highlightSizePick.getSelectedItem())) {
				case "small":
					if (highlight.isSelected()) {
						highlightedPixels.add(new Point(arg0.getX(), arg0.getY()));
						topCopyFromLabelBackground.setRGB(arg0.getX(), arg0.getY(), highlightColor);
					} else if (unHighlight.isSelected()) {
						highlightedPixels.remove(new Point(arg0.getX(), arg0.getY()));
						topCopyFromLabelBackground.setRGB(arg0.getX(), arg0.getY(), fullyTransparentColor);
					}
					break;
				case "medium":
					if (arg0.getX() >= 2 & (arg0.getX() + 2) <= getCopyFromWidth() & arg0.getY() >= 2
							&& (arg0.getY() + 2) <= getCopyFromHeight()) {
						for (int x = arg0.getX() - 2; x < arg0.getX() + 2; x++) {
							for (int y = arg0.getY() - 2; y < arg0.getY() + 2; y++) {
								if (highlight.isSelected()) {
									highlightedPixels.add(new Point(x, y));
									topCopyFromLabelBackground.setRGB(x, y, highlightColor);
								} else if (unHighlight.isSelected()) {
									highlightedPixels.remove(new Point(x, y));
									topCopyFromLabelBackground.setRGB(x, y, fullyTransparentColor);
								}
							}
						}
					}
					break;
				case "large":
					if (arg0.getX() >= 4 & (arg0.getX() + 4) <= getCopyFromWidth() & arg0.getY() >= 4
							&& (arg0.getY() + 4) <= getCopyFromHeight()) {
						for (int x = arg0.getX() - 3; x < arg0.getX() + 4; x++) {
							for (int y = arg0.getY() - 3; y < arg0.getY() + 4; y++) {
								if (highlight.isSelected()) {
									highlightedPixels.add(new Point(x, y));
									topCopyFromLabelBackground.setRGB(x, y, highlightColor);
								} else if (unHighlight.isSelected()) {
									highlightedPixels.remove(new Point(x, y));
									topCopyFromLabelBackground.setRGB(x, y, fullyTransparentColor);
								}
							}
						}
					}
					break;
				case "x large":
					if (arg0.getX() >= 6 & (arg0.getX() + 6) <= getCopyFromWidth() & arg0.getY() >= 6
							&& (arg0.getY() + 6) <= getCopyFromHeight()) {
						for (int x = arg0.getX() - 6; x < arg0.getX() + 7; x++) {
							for (int y = arg0.getY() - 6; y < arg0.getY() + 7; y++) {
								/*
								 * if ((x < (arg0.getX() - 3) && y <
								 * (arg0.getY() - 3)) || (x > (arg0.getX() + 3)
								 * && (y > (arg0.getY() - 3)))) { continue; }
								 * else {
								 */
								if (highlight.isSelected()) {
									highlightedPixels.add(new Point(x, y));
									topCopyFromLabelBackground.setRGB(x, y, highlightColor);
								} else if (unHighlight.isSelected()) {
									highlightedPixels.remove(new Point(x, y));
									topCopyFromLabelBackground.setRGB(x, y, fullyTransparentColor);
								}
								// }
							}
						}
					}
					break;
				case "xxxl":
					if (arg0.getX() >= 20 & (arg0.getX() + 20) <= getCopyFromWidth() & arg0.getY() >= 20
							&& (arg0.getY() + 20) <= getCopyFromHeight()) {
						for (int x = arg0.getX() - 20; x < arg0.getX() + 20; x++) {
							for (int y = arg0.getY() - 20; y < arg0.getY() + 20; y++) {
								if (highlight.isSelected()) {
									highlightedPixels.add(new Point(x, y));
									topCopyFromLabelBackground.setRGB(x, y, highlightColor);
								} else if (unHighlight.isSelected()) {
									highlightedPixels.remove(new Point(x, y));
									topCopyFromLabelBackground.setRGB(x, y, fullyTransparentColor);
								}
							}
						}
					}
					break;
				}
				imageToHighlightTopLabel.setVisible(false);
				imageToHighlightTopLabel.setVisible(true);
			}
		} else if (copyToSelected) {
			if (getPressedX() >= getStartX() && getPressedX() < getEndX() && getPressedY() >= getStartY()
					&& getPressedY() < getEndY()) {
				int currentX = arg0.getX();
				int currentY = arg0.getY();
				if ((currentX > getPressedX()
						&& currentX - getPressedX() + getEndX() < topPasteToLabelBackground.getWidth())
						|| (currentX < getPressedX() && getStartX() - (getPressedX() - currentX) >= 0)
								&& (currentY > getPressedY()
										&& currentY - getPressedY() + getEndY() < topPasteToLabelBackground.getHeight())
						|| (currentY < getPressedY() && getStartY() - (getPressedY() - currentY) > 0)) {
					int x = getStartX() + currentX - getPressedX();
					int y = getStartY() + currentY - getPressedY();
					setCopyToTopLayerTransparent(imageToPasteLabel.getWidth(), imageToPasteLabel.getHeight());
					System.out.println("x: " + x);
					System.out.println("y: " + y);
					if (x >= 0 && x + getPastedClipping().getWidth() < imageToPasteTopLabel.getWidth() && y >= 0
							&& y + getPastedClipping().getHeight() < imageToPasteTopLabel.getHeight()) {
						for (int xPos = x, xRGB = 0; xPos < x + getPastedClipping().getWidth(); xPos++, xRGB++) {
							for (int yPos = y, yRGB = 0; yPos < y + getPastedClipping().getHeight(); yPos++, yRGB++) {
								topPasteToLabelBackground.setRGB(xPos, yPos, pastedClipping.getRGB(xRGB, yRGB));
							}
						}
					} else {
						if (x < 0) {
							x = 0;
						}
						if (y < 0) {
							y = 0;
						}
						if (x > imageToPasteTopLabel.getWidth() - getPastedClipping().getWidth()) {
							x = imageToPasteTopLabel.getWidth() - getPastedClipping().getWidth();
						}
						if (y > imageToPasteTopLabel.getHeight() - getPastedClipping().getHeight()) {
							y = imageToPasteTopLabel.getHeight() - getPastedClipping().getHeight();
						}
						for (int xPos = x, xRGB = 0; xPos < x + getPastedClipping().getWidth(); xPos++, xRGB++) {
							for (int yPos = y, yRGB = 0; yPos < y + getPastedClipping().getHeight(); yPos++, yRGB++) {
								topPasteToLabelBackground.setRGB(xPos, yPos, pastedClipping.getRGB(xRGB, yRGB));
							}
						}
					}
					setStartX(x);
					setStartY(y);
					setEndX(x + getPastedClipping().getWidth());
					setEndY(y + getPastedClipping().getHeight());
					imageToPasteTopLabel.setVisible(false);
					imageToPasteTopLabel.setVisible(true);
				}
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// not used
	}

	/**
	 * @return the newClippingAdded
	 */
	public boolean isNewClippingAdded() {
		return newClippingAdded;
	}

	/**
	 * @param newClippingAdded
	 *            the newClippingAdded to set
	 */
	public void setNewClippingAdded(boolean newClippingAdded) {
		this.newClippingAdded = newClippingAdded;
	}

	/**
	 * @return the currentClippingIconIndex
	 */
	public int getCurrentClippingIconIndex() {
		return currentClippingIconIndex;
	}

	/**
	 * @param currentClippingIconIndex
	 *            the currentClippingIconIndex to set
	 */
	public void setCurrentClippingIconIndex(int currentClippingIconIndex) {
		this.currentClippingIconIndex = currentClippingIconIndex;
	}

	/**
	 * @return the startX
	 */
	public int getStartX() {
		return startX;
	}

	/**
	 * @param startX
	 *            the startX to set
	 */
	public void setStartX(int startX) {
		this.startX = startX;
	}

	/**
	 * @return the endX
	 */
	public int getEndX() {
		return endX;
	}

	/**
	 * @param endX
	 *            the endX to set
	 */
	public void setEndX(int endX) {
		this.endX = endX;
	}

	/**
	 * @return the startY
	 */
	public int getStartY() {
		return startY;
	}

	/**
	 * @param startY
	 *            the startY to set
	 */
	public void setStartY(int startY) {
		this.startY = startY;
	}

	/**
	 * @return the endY
	 */
	public int getEndY() {
		return endY;
	}

	/**
	 * @param endY
	 *            the endY to set
	 */
	public void setEndY(int endY) {
		this.endY = endY;
	}

	/**
	 * @return the pressedX
	 */
	public int getPressedX() {
		return pressedX;
	}

	/**
	 * @param pressedX
	 *            the pressedX to set
	 */
	public void setPressedX(int pressedX) {
		this.pressedX = pressedX;
	}

	/**
	 * @return the pressedY
	 */
	public int getPressedY() {
		return pressedY;
	}

	/**
	 * @param pressedY
	 *            the pressedY to set
	 */
	public void setPressedY(int pressedY) {
		this.pressedY = pressedY;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// not used
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// not used
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// not used
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if (topPasteToLabelBackground != null) {
			if (copyToSelected && (arg0.getX() < topPasteToLabelBackground.getWidth()
					&& arg0.getY() < topPasteToLabelBackground.getHeight())) {
				setPressedX(arg0.getX());
				setPressedY(arg0.getY());
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// not used
	}

	/**
	 * @return the pastedClipping
	 */
	public BufferedImage getPastedClipping() {
		return pastedClipping;
	}

	/**
	 * @param pastedClipping
	 *            the pastedClipping to set
	 */
	public void setPastedClipping(BufferedImage pastedClipping) {
		this.pastedClipping = pastedClipping;
	}
}
