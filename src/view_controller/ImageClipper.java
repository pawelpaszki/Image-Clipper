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
import java.util.Hashtable;

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
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Point;
import model.Scalr;

public class ImageClipper implements ActionListener, MouseMotionListener, MouseListener, ChangeListener {

	private ArrayList<BufferedImage> addClippingButtonIcons;
	private ArrayList<BufferedImage> clippings = new ArrayList<BufferedImage>();
	private boolean clippingPasted;
	private boolean copyFromSelected;
	private boolean copyToSelected;
	private boolean editImageSelected;
	private boolean newClippingAdded;
	private BufferedImage copyFromImage;
	private BufferedImage copyFromZoom2;
	private BufferedImage copyFromZoom4;
	private BufferedImage copyFromZoom8;
	private BufferedImage copyToImage;
	private BufferedImage pastedClipping;
	private BufferedImage topCopyFromLabelBackground;
	private final String userDirLocation = System.getProperty("user.dir");
	private final File userDir = new File(userDirLocation);
	private final FileNameExtensionFilter filter = new FileNameExtensionFilter("images", "jpg", "gif", "png", "bmp");
	private final int fullyTransparentColor = new Color(0, 0, 0, 0).getRGB();
	private final int highlightColor = new Color(255, 0, 0, 192).getRGB();
	private HashSet<Point> highlightedPixels = new HashSet<Point>();
	private int copyFromHeight;
	private int copyFromWidth;
	private int copyToHeight;
	private int copyToWidth;
	private int currentClippingIconIndex;
	private int endX;
	private int endY;
	private int pressedX;
	private int pressedY;
	private int primaryCopyFromHeight;
	private int primaryCopyFromWidth;
	private int primaryEndX;
	private int primaryEndY;
	private int primaryStartX;
	private int primaryStartY;
	private int startX;
	private int startY;
	private int zoomPicked;
	private JButton copyFrom;
	private JButton copyTo;
	private JButton copyToClipboard;
	private JButton editImage;
	private JButton higherIndexClipping;
	private JButton loadImage;
	private JButton lowerIndexClipping;
	private JButton moveDown;
	private JButton moveLeft;
	private JButton moveRight;
	private JButton moveUp;
	private JButton pasteClipping;
	private JCheckBox highlight;
	private JCheckBox unHighlight;
	private JComboBox<String> highlightSizePick;
	private JFrame mainWindow;
	private JLabel imageToHighlightLabel;
	private JLabel imageToHighlightTopLabel;
	private JLabel imageToPasteLabel;
	private JLabel imageToPasteTopLabel;
	private JLabel movingArrows;
	private JLabel sliderLabel;
	private JLayeredPane imageToHighlightLayeredPane;
	private JLayeredPane imageToPasteToLayeredPane;
	private JPanel selectTabPanel;
	private JScrollPane imageToHighlightScrollPane;
	private JScrollPane imageToPasteToScrollPane;
	private JSlider zoomAdjustment;

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
		selectTabPanel.setBorder(new LineBorder(Color.white, 1));
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
		highlight.setBounds(650, 80, 80, 30);
		highlight.setBackground(Color.black);
		highlight.setForeground(Color.white);
		highlight.addActionListener(this);
		highlight.setVisible(false);

		unHighlight = new JCheckBox("unHighlight");
		unHighlight.setBounds(730, 80, 100, 30);
		unHighlight.setBackground(Color.black);
		unHighlight.setForeground(Color.white);
		unHighlight.addActionListener(this);
		unHighlight.setVisible(false);

		String[] highlightSizes = { "small", "medium", "large", "x large", "xxxl" };

		highlightSizePick = new JComboBox<String>(highlightSizes);
		highlightSizePick.setSelectedItem(null);
		highlightSizePick.addActionListener(this);
		highlightSizePick.setBounds(830, 85, 100, 20);
		highlightSizePick.setBackground(Color.black);
		highlightSizePick.setForeground(Color.white);
		highlightSizePick.setVisible(false);

		copyToClipboard = makeButton("copy to clipboard");
		copyToClipboard.setBounds(940, 80, 140, 30);
		copyToClipboard.setVisible(false);
		copyToClipboard.setIcon(new ImageIcon("src/resources/copy_to_clipboard.png"));

		imageToHighlightLayeredPane = new JLayeredPane();

		imageToHighlightScrollPane = new JScrollPane(imageToHighlightLayeredPane);
		imageToHighlightScrollPane.setBounds(5, 125, 1185, 540);
		imageToHighlightScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		imageToHighlightScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		imageToHighlightScrollPane.getViewport().setBackground(Color.black);

		imageToHighlightLayeredPane.addMouseMotionListener(this);
		imageToPasteToLayeredPane = new JLayeredPane();
		imageToPasteToLayeredPane.addMouseMotionListener(this);
		imageToPasteToLayeredPane.addMouseListener(this);
		imageToHighlightLayeredPane.addMouseListener(this);

		imageToPasteToScrollPane = new JScrollPane(imageToPasteToLayeredPane);
		imageToPasteToScrollPane.setBounds(5, 125, 1185, 540);
		imageToPasteToScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		imageToPasteToScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		imageToPasteToScrollPane.setVisible(false);
		imageToPasteToScrollPane.getViewport().setBackground(Color.black);

		lowerIndexClipping = makeButton("<");
		lowerIndexClipping.setBounds(470, 10, 20, 110);
		lowerIndexClipping.setIcon(new ImageIcon("src/resources/lower_index.png"));

		higherIndexClipping = makeButton(">");
		higherIndexClipping.setBounds(620, 10, 20, 110);
		higherIndexClipping.setIcon(new ImageIcon("src/resources/higher_index.png"));

		pasteClipping = makeButton("paste clipping");
		pasteClipping.setBounds(500, 10, 110, 110);

		moveLeft = makeButton("left");
		moveLeft.setBounds(650, 50, 30, 30);
		moveLeft.setIcon(new ImageIcon("src/resources/left.png"));
		moveLeft.setVisible(false);

		moveUp = makeButton("up");
		moveUp.setBounds(690, 10, 30, 30);
		moveUp.setIcon(new ImageIcon("src/resources/up.png"));
		moveUp.setVisible(false);

		moveRight = makeButton("right");
		moveRight.setBounds(730, 50, 30, 30);
		moveRight.setIcon(new ImageIcon("src/resources/right.png"));
		moveRight.setVisible(false);

		moveDown = makeButton("down");
		moveDown.setBounds(690, 90, 30, 30);
		moveDown.setIcon(new ImageIcon("src/resources/down.png"));
		moveDown.setVisible(false);

		movingArrows = new JLabel(new ImageIcon("src/resources/arrows.png"));
		movingArrows.setBounds(680, 40, 50, 50);
		movingArrows.setVisible(false);

		showClippingChoice(false);

		mainWindow.getContentPane().add(selectTabPanel);
		mainWindow.getContentPane().add(imageToHighlightScrollPane);
		mainWindow.getContentPane().add(imageToPasteToScrollPane);
		mainWindow.getContentPane().add(highlight);
		mainWindow.getContentPane().add(unHighlight);
		mainWindow.getContentPane().add(highlightSizePick);
		mainWindow.getContentPane().add(movingArrows);

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
		if (isClippingPasted()) {
			switch (action) {
			case "left":
				if (getStartX() > 0) {
					setStartX((getStartX() - 1));
					setEndX((getEndX() - 1));
				}
				break;
			case "right":
				if (getEndX() < imageToPasteLabel.getWidth()) {
					setStartX((getStartX() + 1));
					setEndX((getEndX() + 1));
				}
				break;
			case "up":
				if (getStartY() > 0) {
					setStartY((getStartY() - 1));
					setEndY((getEndY() - 1));
				}
				break;
			case "down":
				if (getEndY() < imageToPasteLabel.getHeight()) {
					setStartY((getStartY() + 1));
					setEndY((getEndY() + 1));
				}
				break;
			}
			imageToPasteTopLabel.setBounds(getStartX(), getStartY(), imageToPasteTopLabel.getWidth(),
					imageToPasteTopLabel.getHeight());
		}
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
			showClippingChoice(clippings.size() > 0);
			copyTo.setEnabled(true);
			copyFrom.setEnabled(false);
			imageToHighlightScrollPane.setVisible(true);
			imageToPasteToScrollPane.setVisible(false);
			copyFrom.setBorder(BorderFactory.createLoweredBevelBorder());
			copyTo.setBorder(null);
			copyFromSelected = true;
			copyToSelected = false;
			setEditImageSelected(false);
			if (imageToHighlightTopLabel != null && imageToHighlightTopLabel.getHeight() > 0) {
				highlightSizePick.setVisible(true);
				highlight.setVisible(true);
				unHighlight.setVisible(true);
				copyToClipboard.setVisible(true);
				zoomAdjustment.setVisible(true);
				sliderLabel.setVisible(true);
			}
			moveLeft.setVisible(false);
			moveUp.setVisible(false);
			moveRight.setVisible(false);
			moveDown.setVisible(false);
			movingArrows.setVisible(false);
			break;
		case "copy to":
			if (zoomAdjustment != null && sliderLabel != null) {
				zoomAdjustment.setVisible(false);
				sliderLabel.setVisible(false);
			}
			showClippingChoice(copyToImage != null);
			imageToHighlightScrollPane.setVisible(false);
			imageToPasteToScrollPane.setVisible(true);
			copyTo.setBorder(BorderFactory.createLoweredBevelBorder());
			copyTo.setEnabled(false);
			copyFrom.setEnabled(true);
			editImage.setEnabled(true);
			copyFrom.setBorder(null);
			copyFromSelected = false;
			copyToSelected = true;
			setEditImageSelected(false);
			highlight.setVisible(false);
			unHighlight.setVisible(false);
			highlightSizePick.setVisible(false);
			copyToClipboard.setVisible(false);
			if (isClippingPasted()) {
				moveLeft.setVisible(true);
				moveUp.setVisible(true);
				moveRight.setVisible(true);
				moveDown.setVisible(true);
				movingArrows.setVisible(true);
			}
			break;
		case "edit image":
			setEditImageSelected(true);
			copyToSelected = false;
			copyFromSelected = false;
			copyTo.setEnabled(true);
			copyFrom.setEnabled(true);
			editImage.setEnabled(false);
		case "copy to clipboard":
			if (highlightedPixels.size() > 100) {

				int maxX = 0;
				int maxY = 0;
				int minX = getCopyFromWidth();
				int minY = getCopyFromHeight();
				ArrayList<Point> tempPixels = new ArrayList<Point>();
				for (int x = 0; x < getPrimaryCopyFromWidth(); x++) {
					for (int y = 0; y < getPrimaryCopyFromHeight(); y++) {
						if (topCopyFromLabelBackground.getRGB(x, y) != fullyTransparentColor) {
							tempPixels.add(new Point(x, y));
						}
					}
				}
				ArrayList<Point> pixels = tempPixels;
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
				setCurrentClippingIconIndex(0);
				showClippingChoice(true);
				int clippingsSize = clippings.size();
				if (clippings.size() == 1) {
					addClippingButtonIcons = new ArrayList<>();
				}
				if (clippings.get(clippingsSize - 1).getHeight() > 110
						|| clippings.get(clippingsSize - 1).getWidth() > 110) {
					double ratio = 0;
					double width = 0;
					double height = 0;

					if (clippings.get(clippingsSize - 1).getHeight() > clippings.get(clippingsSize - 1).getWidth()) {
						ratio = clippings.get(clippingsSize - 1).getHeight() * 1.0 / 108.0;
					} else {
						ratio = clippings.get(clippingsSize - 1).getWidth() * 1.0 / 108.0;
					}
					width = clippings.get(clippingsSize - 1).getWidth() / ratio;
					height = clippings.get(clippingsSize - 1).getHeight() / ratio;
					addClippingButtonIcons
							.add(Scalr.resize(clippings.get(clippingsSize - 1), ((int) width), ((int) height)));
					pasteClipping.setIcon(new ImageIcon(addClippingButtonIcons.get(0)));
				} else {
					addClippingButtonIcons.add(clippings.get(clippingsSize - 1));
					pasteClipping.setIcon(new ImageIcon(addClippingButtonIcons.get(0)));
				}
			}

			break;
		case "load image":
			if (copyFromSelected || copyToSelected) {
				JFileChooser chooser = new JFileChooser(userDir);
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(mainWindow);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					if (copyFromSelected) {

						String path = chooser.getSelectedFile().getAbsolutePath();
						insertImageIntoCopyFrom(path);

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

						imageToPasteToLayeredPane.add(imageToPasteLabel);
						repaintCopyTo();
						setClippingPasted(false);
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
			if (copyToSelected) {
				if (imageToPasteLabel != null && imageToPasteLabel.getWidth() > 0) {

					if (imageToPasteTopLabel != null) {
						imageToPasteToLayeredPane.remove(imageToPasteTopLabel);
					}
					imageToPasteTopLabel = null;
					repaintCopyTo();

					pastedClipping = null;

					int clippingHeight = clippings.get(currentClippingIconIndex).getHeight();
					int clippingWidth = clippings.get(currentClippingIconIndex).getWidth();

					if (clippingHeight > imageToPasteLabel.getHeight()
							|| clippingWidth > imageToPasteLabel.getWidth()) {
						double widthRatio = Math.round(clippingWidth * 100.0 / imageToPasteLabel.getWidth()) / 100.0
								+ 0.1;
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
					imageToPasteTopLabel = new JLabel(new ImageIcon(pastedClipping));
					imageToPasteTopLabel.setBounds(0, 0, pastedClipping.getWidth(), pastedClipping.getHeight());
					imageToPasteToLayeredPane.add(imageToPasteTopLabel);
					imageToPasteToLayeredPane.moveToFront(imageToPasteTopLabel);
					setStartX(0);
					setStartY(0);
					setEndX(pastedClipping.getWidth() - 1);
					setEndY(pastedClipping.getHeight() - 1);
					imageToPasteTopLabel.setVisible(false);
					imageToPasteTopLabel.setVisible(true);

					setClippingPasted(true);
				}
			} else if (copyFromSelected) {
				insertImageIntoCopyFrom(null);
			}
			break;
		}

	}

	private void insertImageIntoCopyFrom(String path) {
		if (zoomAdjustment != null) {
			zoomAdjustment.setVisible(false);
			sliderLabel.setVisible(false);
			zoomAdjustment = null;
			sliderLabel = null;
		}
		if (path != null) {
			File file = new File(path);
			copyFromImage = null;
			try {
				copyFromImage = ImageIO.read(file);
			} catch (IOException e) {

			}
		} else {
			copyFromImage = clippings.get(currentClippingIconIndex);
		}

		mainWindow.repaint();
		int height = copyFromImage.getHeight();
		int width = copyFromImage.getWidth();

		/*
		 * // test// ArrayList<Color> colors = new ArrayList<Color>(); for (int
		 * x = 0; x < width; x++) { for (int y = 0; y < height; y++) { Color
		 * color = new Color(copyFromImage.getRGB(x, y)); if
		 * (!colors.contains(color)) { colors.add(color); } } }
		 * 
		 * System.out.println("number of colors: " + colors.size()); // test //
		 * 
		 */
		setPrimaryCopyFromHeight(height);
		setPrimaryCopyFromWidth(width);
		imageToHighlightLayeredPane.removeAll();
		if (path != null) {
			imageToHighlightLabel = new JLabel(new ImageIcon(path));
		} else {
			imageToHighlightLabel = new JLabel(new ImageIcon(clippings.get(currentClippingIconIndex)));
		}
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

		int resolution = height * width;
		System.out.println(resolution);
		int MAX_ZOOM = 1;
		int noOfZoomOptions = 0;
		if (resolution <= 120000) {
			MAX_ZOOM = 8;
			noOfZoomOptions = 4;
		} else if (resolution <= 480000) {
			MAX_ZOOM = 4;
			noOfZoomOptions = 3;
		} else if (resolution <= 2000000) {
			MAX_ZOOM = 2;
			noOfZoomOptions = 2;
		}
		copyFromZoom8 = null;
		copyFromZoom4 = null;
		copyFromZoom2 = null;
		if (MAX_ZOOM > 1) {
			System.out.println("true");
			switch (MAX_ZOOM) {
			case 8:
				copyFromZoom8 = new BufferedImage(width * 8, height * 8, BufferedImage.TYPE_INT_ARGB);
				for (int x = 0; x < imageToHighlightTopLabel.getWidth() * 8; x++) {
					for (int y = 0; y < imageToHighlightTopLabel.getHeight() * 8; y++) {
						copyFromZoom8.setRGB(x, y, copyFromImage.getRGB(x / 8, y / 8));
					}
				}
			case 4:
				copyFromZoom4 = new BufferedImage(width * 4, height * 4, BufferedImage.TYPE_INT_ARGB);
				for (int x = 0; x < imageToHighlightTopLabel.getWidth() * 4; x++) {
					for (int y = 0; y < imageToHighlightTopLabel.getHeight() * 4; y++) {
						copyFromZoom4.setRGB(x, y, copyFromImage.getRGB(x / 4, y / 4));
					}
				}
			case 2:
				copyFromZoom2 = new BufferedImage(width * 2, height * 2, BufferedImage.TYPE_INT_ARGB);
				for (int x = 0; x < imageToHighlightTopLabel.getWidth() * 2; x++) {
					for (int y = 0; y < imageToHighlightTopLabel.getHeight() * 2; y++) {
						copyFromZoom2.setRGB(x, y, copyFromImage.getRGB(x / 2, y / 2));
					}
				}
			}
			final int NO_ZOOM = 1;

			zoomAdjustment = new JSlider(JSlider.HORIZONTAL, NO_ZOOM, noOfZoomOptions, NO_ZOOM);
			zoomAdjustment.setBounds(655, 35, 80, 40);
			zoomAdjustment.setMinorTickSpacing(1);
			zoomAdjustment.setPaintTicks(true);
			zoomAdjustment.setPaintLabels(true);
			zoomAdjustment.setValue(0);
			zoomAdjustment.addChangeListener(this);
			Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
			table.put(1, new JLabel("1"));
			table.put(2, new JLabel("2"));
			if (MAX_ZOOM >= 4) {
				table.put(3, new JLabel("4"));
			}
			if (MAX_ZOOM == 8) {
				table.put(4, new JLabel("8"));
			}
			zoomAdjustment.setLabelTable(table);

			sliderLabel = new JLabel("zoom in: ", JLabel.CENTER);
			sliderLabel.setBounds(655, 10, 80, 20);
			sliderLabel.setFont(new Font("Arial", Font.BOLD, 14));
			sliderLabel.setForeground(Color.white);
			zoomAdjustment.setAlignmentX(Component.CENTER_ALIGNMENT);

			zoomAdjustment.setVisible(true);
			sliderLabel.setVisible(true);
			zoomAdjustment.setValue(1);
			setZoomPicked(1);
			mainWindow.getContentPane().add(zoomAdjustment);
			mainWindow.getContentPane().add(sliderLabel);
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
						int xStart = arg0.getX() - 6;
						int yStart = arg0.getY() - 6;
						int xEnd = arg0.getX() + 7;
						int yEnd = arg0.getY() + 7;
						for (int x = xStart; x < xEnd; x++) {
							for (int y = yStart; y < yEnd; y++) {
								if (checkXandY(x - xStart, y - yStart, "x large")) {
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
					}
					break;
				case "xxxl":
					if (arg0.getX() >= 20 & (arg0.getX() + 20) <= getCopyFromWidth() & arg0.getY() >= 20
							&& (arg0.getY() + 20) <= getCopyFromHeight()) {
						int xStart = arg0.getX() - 20;
						int yStart = arg0.getY() - 20;
						int xEnd = arg0.getX() + 20;
						int yEnd = arg0.getY() + 20;
						for (int x = xStart; x < xEnd; x++) {
							for (int y = yStart; y < yEnd; y++) {
								if (checkXandY(x - xStart, y - yStart, "xxxl")) {
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
					}
					break;
				}
				imageToHighlightTopLabel.setVisible(false);
				imageToHighlightTopLabel.setVisible(true);
			}
		} else if (copyToSelected) {
			if (isClippingPasted()) {
				if (getPressedX() >= getPrimaryStartX() && getPressedX() < getPrimaryEndX()
						&& getPressedY() >= getPrimaryStartY() && getPressedY() < getPrimaryEndY()) {
					int currentX = arg0.getX();
					int currentY = arg0.getY();
					int changeX = currentX - getPressedX();
					int changeY = currentY - getPressedY();
					int x = 0;
					int y = 0;
					if (getPrimaryStartX() + changeX < 0) {
						x = 0;
					} else if (getPrimaryEndX() + changeX > imageToPasteToLayeredPane.getWidth()) {
						x = imageToPasteToLayeredPane.getWidth() - imageToPasteTopLabel.getWidth();
					} else {
						x = getPrimaryStartX() + changeX;
					}
					if (getPrimaryStartY() + changeY < 0) {
						y = 0;
					} else if (getPrimaryEndY() + changeY > imageToPasteToLayeredPane.getHeight()) {
						y = imageToPasteToLayeredPane.getWidth() - imageToPasteTopLabel.getHeight();
					} else {
						y = getPrimaryStartY() + changeY;
					}
					setStartX(x);
					setStartY(y);
					setEndX(x + imageToPasteTopLabel.getWidth());
					setEndY(y + imageToPasteTopLabel.getHeight());
					imageToPasteTopLabel.setBounds(x, y, imageToPasteTopLabel.getWidth(),
							imageToPasteTopLabel.getHeight());
				}
				repaintCopyTo();
			}
		}

	}

	/**
	 * This method is used to provide specific shape of highlight or
	 * unhighlight, when x large or large highlight size is picked
	 * 
	 * @param x
	 *            - x position value of image to be highlighted
	 * @param y
	 *            - y position value of image to be highlighted
	 * @param highlightSize
	 *            - specifies size of highlight
	 * @return true, if a pixel specified with x and y is to be highlighted or
	 *         unhighlighted
	 */
	private boolean checkXandY(int x, int y, String highlightSize) {
		switch (highlightSize) {
		case "x large":
			if (x < 4 || x > 8) {
				if (y < 4 && x + y < 5) {
					return false;
				} else if (y == 9 && (x < 1 || x > 12)) {
					return false;
				} else if (y == 10 && (x < 2 || x > 11)) {
					return false;
				} else if (y == 11 && (x < 3 || x > 10)) {
					return false;
				} else if (y == 12 && (x < 4 || x > 9)) {
					return false;
				} else if (x == 9 && y > 12) {
					return false;
				} else if (x == 10 && y > 11) {
					return false;
				} else if (x == 11 && y > 10) {
					return false;
				} else if (x == 12 && y > 9) {
					return false;
				}
			}
		case "xxxl":
			if (x < 10 || x > 30 || y < 10 || y > 30) {
				switch (x) {
				case 0:
				case 40:
					if (y < 10 || y > 30) {
						return false;
					}
				case 1:
				case 39:
					if (y < 9 || y > 31) {
						return false;
					}
				case 2:
				case 38:
					if (y < 8 || y > 32) {
						return false;
					}
				case 3:
				case 37:
					if (y < 7 || y > 33) {
						return false;
					}
				case 4:
				case 36:
					if (y < 6 || y > 34) {
						return false;
					}
				case 5:
				case 35:
					if (y < 5 || y > 35) {
						return false;
					}
				case 6:
				case 34:
					if (y < 4 || y > 36) {
						return false;
					}
				case 7:
				case 33:
					if (y < 3 || y > 37) {
						return false;
					}
				case 8:
				case 32:
					if (y < 2 || y > 38) {
						return false;
					}
				case 9:
				case 31:
					if (y < 1 || y > 39) {
						return false;
					}
				}
			}
		}
		return true;
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
		if (imageToPasteTopLabel != null) {
			if (copyToSelected && (arg0.getX() >= getStartX() && arg0.getX() <= getEndX() && arg0.getY() >= getStartY()
					&& arg0.getY() <= getEndY())) {
				setPressedX(arg0.getX());
				setPressedY(arg0.getY());
				setPrimaryStartX(getStartX());
				setPrimaryEndX(getEndX());
				setPrimaryStartY(getStartY());
				setPrimaryEndY(getEndY());
			}
		} else if (copyFromImage != null && copyFromSelected && getZoomPicked() == 1
				&& arg0.getX() < copyFromImage.getWidth() && arg0.getY() < copyFromImage.getHeight()) {
			int x = arg0.getX();
			int y = arg0.getY();
			Color newColor = new Color(copyFromImage.getRGB(x, y));
			System.out.println(newColor);
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

	/**
	 * @return the clippingPasted
	 */
	public boolean isClippingPasted() {
		return clippingPasted;
	}

	/**
	 * @param clippingPasted
	 *            the clippingPasted to set
	 */
	public void setClippingPasted(boolean clippingPasted) {
		this.clippingPasted = clippingPasted;
		moveLeft.setVisible(clippingPasted);
		moveUp.setVisible(clippingPasted);
		moveRight.setVisible(clippingPasted);
		moveDown.setVisible(clippingPasted);
		movingArrows.setVisible(clippingPasted);

	}

	/**
	 * @return the primaryStartX
	 */
	public int getPrimaryStartX() {
		return primaryStartX;
	}

	/**
	 * @param primaryStartX
	 *            the primaryStartX to set
	 */
	public void setPrimaryStartX(int primaryStartX) {
		this.primaryStartX = primaryStartX;
	}

	/**
	 * @return the primaryEndX
	 */
	public int getPrimaryEndX() {
		return primaryEndX;
	}

	/**
	 * @param primaryEndX
	 *            the primaryEndX to set
	 */
	public void setPrimaryEndX(int primaryEndX) {
		this.primaryEndX = primaryEndX;
	}

	/**
	 * @return the primaryStartY
	 */
	public int getPrimaryStartY() {
		return primaryStartY;
	}

	/**
	 * @param primaryStartY
	 *            the primaryStartY to set
	 */
	public void setPrimaryStartY(int primaryStartY) {
		this.primaryStartY = primaryStartY;
	}

	/**
	 * @return the primaryEndY
	 */
	public int getPrimaryEndY() {
		return primaryEndY;
	}

	/**
	 * @param primaryEndY
	 *            the primaryEndY to set
	 */
	public void setPrimaryEndY(int primaryEndY) {
		this.primaryEndY = primaryEndY;
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		JSlider source = (JSlider) arg0.getSource();
		System.out.println(source.getValue());
		int zoomValue = 0;
		if (source.getValue() == 3) {
			zoomValue = 4;
		} else if (source.getValue() == 4) {
			zoomValue = 8;
		} else {
			zoomValue = source.getValue();
		}

		setZoomPicked(zoomValue);
		int height = getPrimaryCopyFromHeight() * zoomValue;
		int width = getPrimaryCopyFromWidth() * zoomValue;
		switch (zoomValue) {
		case 1:
			imageToHighlightLabel = new JLabel(new ImageIcon(copyFromImage));
			copyToClipboard.setEnabled(true);
			break;
		case 2:
			imageToHighlightLabel = new JLabel(new ImageIcon(copyFromZoom2));
			copyToClipboard.setEnabled(false);
			break;
		case 4:
			imageToHighlightLabel = new JLabel(new ImageIcon(copyFromZoom4));
			copyToClipboard.setEnabled(false);
			break;
		case 8:
			imageToHighlightLabel = new JLabel(new ImageIcon(copyFromZoom8));
			copyToClipboard.setEnabled(false);
			break;
		}

		imageToHighlightLayeredPane.setPreferredSize(new Dimension(width * zoomValue, height * zoomValue));
		setCopyFromHeight(height);
		setCopyFromWidth(width);
		imageToHighlightLabel.setSize(new Dimension(width, height));
		if (highlightedPixels.size() == 0) {
			topCopyFromLabelBackground = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			makeCopyFromTopTransparent();
		} else {
			BufferedImage tempTopCopyFromLabelBackground = topCopyFromLabelBackground;
			topCopyFromLabelBackground = Scalr.resize(tempTopCopyFromLabelBackground, width, height);
		}

		imageToHighlightTopLabel = new JLabel(new ImageIcon(topCopyFromLabelBackground));
		imageToHighlightTopLabel.setSize(new Dimension(width, height));
		imageToHighlightLayeredPane.removeAll();
		imageToHighlightLayeredPane.add(imageToHighlightLabel);
		imageToHighlightLayeredPane.add(imageToHighlightTopLabel);
		imageToHighlightLayeredPane.moveToFront(imageToHighlightTopLabel);
		repaintCopyFrom();

	}

	/**
	 * @return the zoomPicked
	 */
	public int getZoomPicked() {
		return zoomPicked;
	}

	/**
	 * @param zoomPicked
	 *            the zoomPicked to set
	 */
	public void setZoomPicked(int zoomPicked) {
		this.zoomPicked = zoomPicked;
	}

	/**
	 * @return the primaryCopyFromWidth
	 */
	public int getPrimaryCopyFromWidth() {
		return primaryCopyFromWidth;
	}

	/**
	 * @param primaryCopyFromWidth
	 *            the primaryCopyFromWidth to set
	 */
	public void setPrimaryCopyFromWidth(int primaryCopyFromWidth) {
		this.primaryCopyFromWidth = primaryCopyFromWidth;
	}

	/**
	 * @return the primaryCopyFromHeight
	 */
	public int getPrimaryCopyFromHeight() {
		return primaryCopyFromHeight;
	}

	/**
	 * @param primaryCopyFromHeight
	 *            the primaryCopyFromHeight to set
	 */
	public void setPrimaryCopyFromHeight(int primaryCopyFromHeight) {
		this.primaryCopyFromHeight = primaryCopyFromHeight;
	}

	/**
	 * @return the editImageSelected
	 */
	public boolean isEditImageSelected() {
		return editImageSelected;
	}

	/**
	 * @param editImageSelected
	 *            the editImageSelected to set
	 */
	public void setEditImageSelected(boolean editImageSelected) {
		this.editImageSelected = editImageSelected;
	}
}
