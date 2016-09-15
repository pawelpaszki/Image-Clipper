package view_controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Point;
import utils.ImageRotator;
import utils.Scalr;
/**
 * 
 * @author Pawel Paszki
 * 
 * This class is a class, which generates the GUI and controls all the actions 
 * which occur, when the application is run
 *
 */
public class ImageClipper implements ActionListener, MouseMotionListener, MouseListener, ChangeListener {

	private ArrayList<BufferedImage> addClippingButtonIcons;
	private ArrayList<BufferedImage> clippings = new ArrayList<BufferedImage>();
	private boolean clippingPasted;
	private boolean copyFromSelected;
	private boolean copyToSelected;
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
	private JLabel sliderLabel;
	private JLayeredPane imageToHighlightLayeredPane;
	private JLayeredPane imageToPasteToLayeredPane;
	private JPanel selectTabPanel;
	private JScrollPane imageToHighlightScrollPane;
	private JScrollPane imageToPasteToScrollPane;
	private JSlider zoomAdjustment;
	private JButton resizeClipping;
	private JTextArea dimensions;
	private JTextField clippingNewWidth;
	private JTextField clippingNewHeight;
	private JLabel newHeightLabel;
	private JLabel newWidthLabel;
	private JButton rotateSmall;
	private JButton rotateBig;
	private JPanel clippingsManipulation;
	private boolean clippingRotated;
	private JButton rotateTiny;
	private int currentlyRotatedBy;
	private JLabel moveClippings;
	private JButton embedClipping;
	private JPanel embedOrSavePanel;
	private JButton saveImage;
	private int currentlyPastedClippingIndex;
	private JButton flipVertical;
	private JButton flipHorizontal;
	private boolean flippedVertically;
	private boolean flippedHorizontally;

	public static void main(String[] args) {

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

	/**
	 * initialisation of the GUI
	 */
	private void initialise() {

		mainWindow = new JFrame("Image Clipper (developed by Pawel Paszki - pawelpaszki@gmail.com)");
		mainWindow.setSize(1200, 700);
		mainWindow.setResizable(false);
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setVisible(true);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.getContentPane().setBackground(Color.black);
		mainWindow.setLayout(null);

		dimensions = new JTextArea();
		dimensions.setBounds(15, 15, 300, 50);
		dimensions.setEditable(false);
		dimensions.setBackground(Color.black);
		dimensions.setForeground(Color.white);
		dimensions.setFont(new Font("Arial", Font.ITALIC, 16));

		selectTabPanel = new JPanel();
		selectTabPanel.setBounds(5, 75, 300, 40);
		selectTabPanel.setBorder(new LineBorder(Color.white, 1));
		selectTabPanel.setBackground(Color.black);
		selectTabPanel.setLayout(new GridLayout(1, 2));

		copyFrom = makeButton("copy from", false);
		copyTo = makeButton("copy to", false);
		selectTabPanel.add(copyFrom);
		selectTabPanel.add(copyTo);

		loadImage = makeButton("load image", true);

		loadImage.setBounds(325, 80, 120, 30);
		try {
			Image img = ImageIO.read(getClass().getResource("resources/load_image.png"));
			loadImage.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}
		try {
			Image img = ImageIO.read(getClass().getResource("resources/copy_from.png"));
			copyFrom.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}
		try {
			Image img = ImageIO.read(getClass().getResource("resources/copy_to.png"));
			copyTo.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}
		

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

		copyToClipboard = makeButton("copy to clipboard", true);
		copyToClipboard.setBounds(940, 80, 140, 30);
		copyToClipboard.setVisible(false);
		try {
			Image img = ImageIO.read(getClass().getResource("resources/copy_to_clipboard.png"));
			copyToClipboard.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}

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

		lowerIndexClipping = makeButton("<", true);
		lowerIndexClipping.setBounds(470, 10, 20, 110);
		lowerIndexClipping.setVisible(false);
		try {
			Image img = ImageIO.read(getClass().getResource("resources/lower_index.png"));
			lowerIndexClipping.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}

		higherIndexClipping = makeButton(">", true);
		higherIndexClipping.setBounds(620, 10, 20, 110);
		higherIndexClipping.setVisible(false);
		try {
			Image img = ImageIO.read(getClass().getResource("resources/higher_index.png"));
			higherIndexClipping.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}

		pasteClipping = makeButton("paste clipping", true);
		pasteClipping.setVisible(false);
		pasteClipping.setBounds(500, 10, 110, 110);

		clippingsManipulation = new JPanel();
		clippingsManipulation.setBounds(645, 5, 400, 118);
		clippingsManipulation.setBackground(Color.black);
		clippingsManipulation.setLayout(null);

		moveLeft = makeButton("left", false);
		moveLeft.setBounds(5, 55, 30, 30);
		try {
			Image img = ImageIO.read(getClass().getResource("resources/left.png"));
			moveLeft.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}
		clippingsManipulation.add(moveLeft);

		moveUp = makeButton("up", false);
		moveUp.setBounds(35, 25, 30, 30);
		try {
			Image img = ImageIO.read(getClass().getResource("resources/up.png"));
			moveUp.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}
		clippingsManipulation.add(moveUp);

		moveRight = makeButton("right", false);
		moveRight.setBounds(65, 55, 30, 30);
		try {
			Image img = ImageIO.read(getClass().getResource("resources/right.png"));
			moveRight.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}
		clippingsManipulation.add(moveRight);

		moveDown = makeButton("down", false);
		moveDown.setBounds(35, 85, 30, 30);
		try {
			Image img = ImageIO.read(getClass().getResource("resources/down.png"));
			moveDown.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}
		clippingsManipulation.add(moveDown);

		moveClippings = new JLabel("move clipping: ", JLabel.CENTER);
		moveClippings.setBounds(5, 5, 100, 20);
		moveClippings.setFont(new Font("Arial", Font.BOLD, 12));
		moveClippings.setForeground(Color.white);
		clippingsManipulation.add(moveClippings);

		resizeClipping = makeButton("resize clipping", false);
		resizeClipping.setBounds(145, 40, 120, 30);
		try {
			Image img = ImageIO.read(getClass().getResource("resources/resize.png"));
			resizeClipping.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}
		resizeClipping.addActionListener(this);
		clippingsManipulation.add(resizeClipping);

		rotateBig = makeButton("rotate big", false);
		rotateBig.setBounds(145, 73, 40, 40);
		try {
			Image img = ImageIO.read(getClass().getResource("resources/rotateBig.png"));
			rotateBig.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}
		rotateBig.addActionListener(this);
		clippingsManipulation.add(rotateBig);

		rotateSmall = makeButton("rotate small", false);
		rotateSmall.setBounds(195, 78, 30, 30);
		try {
			Image img = ImageIO.read(getClass().getResource("resources/rotateSmall.png"));
			rotateSmall.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}
		rotateSmall.addActionListener(this);
		clippingsManipulation.add(rotateSmall);

		rotateTiny = makeButton("rotate tiny", false);
		rotateTiny.setBounds(235, 83, 20, 20);
		try {
			Image img = ImageIO.read(getClass().getResource("resources/rotateTiny.png"));
			rotateTiny.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}
		rotateTiny.addActionListener(this);
		clippingsManipulation.add(rotateTiny);

		newWidthLabel = makeLabel("w:");
		newWidthLabel.setBounds(145, 15, 15, 20);
		newWidthLabel.setVisible(true);
		clippingsManipulation.add(newWidthLabel);
		clippingNewWidth = new JTextField();
		clippingNewWidth.setBounds(165, 15, 35, 20);
		clippingsManipulation.add(clippingNewWidth);

		newHeightLabel = makeLabel("h:");
		newHeightLabel.setBounds(205, 15, 15, 20);
		clippingsManipulation.add(newHeightLabel);
		clippingNewHeight = new JTextField();
		clippingNewHeight.setBounds(225, 15, 35, 20);
		clippingsManipulation.add(clippingNewHeight);
		
		flipVertical = makeButton("vertically", false);
		flipVertical.setBounds(290, 20, 100, 30);
		try {
			Image img = ImageIO.read(getClass().getResource("resources/flip_vertical.png"));
			flipVertical.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}
		clippingsManipulation.add(flipVertical);

		flipHorizontal = makeButton("horizontally", false);
		flipHorizontal.setBounds(290, 70, 100, 30);
		try {
			Image img = ImageIO.read(getClass().getResource("resources/flip_horizontal.png"));
			flipHorizontal.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}
		clippingsManipulation.add(flipHorizontal);
		
		clippingsManipulation.setVisible(false);

		embedClipping = makeButton("embed", false);
		embedClipping.setBounds(5, 5, 120, 30);
		try {
			Image img = ImageIO.read(getClass().getResource("resources/embed.png"));
			embedClipping.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}
		embedClipping.addActionListener(this);

		saveImage = makeButton("save", false);
		saveImage.setBounds(5, 45, 120, 30);
		try {
			Image img = ImageIO.read(getClass().getResource("resources/save_image.png"));
			saveImage.setIcon(new ImageIcon(img));
		} catch (IOException ex) {
		}
		saveImage.addActionListener(this);

		embedOrSavePanel = new JPanel();
		embedOrSavePanel.setBounds(1050, 20, 130, 80);
		embedOrSavePanel.setBackground(Color.black);
		embedOrSavePanel.setLayout(null);
		embedOrSavePanel.add(embedClipping);
		embedOrSavePanel.add(saveImage);
		embedOrSavePanel.setVisible(false);

		mainWindow.getContentPane().add(selectTabPanel);
		mainWindow.getContentPane().add(imageToHighlightScrollPane);
		mainWindow.getContentPane().add(imageToPasteToScrollPane);
		mainWindow.getContentPane().add(highlight);
		mainWindow.getContentPane().add(unHighlight);
		mainWindow.getContentPane().add(highlightSizePick);
		mainWindow.getContentPane().add(clippingsManipulation);
		mainWindow.getContentPane().add(dimensions);
		mainWindow.getContentPane().add(embedOrSavePanel);

	}
	
	/**
	 * This method takes one argument: 
	 * @param arg true if there are any clippings copied from an image,
	 * false otherwise. IF there are clippings - buttons to navigate 
	 * between them will be visible
	 */
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

	/**
	 * 
	 * @param text - text of the label
	 * @return created label with black background and white font
	 */
	private JLabel makeLabel(String text) {
		JLabel label = new JLabel(text);
		label.setBackground(Color.black);
		label.setForeground(Color.white);
		label.setFont(new Font("Arial", Font.ITALIC, 16));
		return label;
	}

	/**
	 * 
	 * @param text - text of JButton
	 * @param mainFrameButton - true, if the button belongs to mainFrame itself
	 * false - if it belongs to some inner component
	 * @return instance of a button 
	 */
	private JButton makeButton(String text, boolean mainFrameButton) {
		JButton button = new JButton(text);
		button.setFocusPainted(false);
		button.addActionListener(this);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setFont(new Font("Arial", Font.BOLD, 0));
		if (mainFrameButton) {
			mainWindow.getContentPane().add(button);
		}
		return button;
	}

	/**
	 * this method control the whole app - decides what to do, when a button is pressed
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		/*
		 * moving a clipping by one pixel up / down / left / right
		 */
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
		/*
		 * rotation of a clipping
		 */
		case "rotate big":
		case "rotate small":
		case "rotate tiny":
			BufferedImage rotatedClipping = null;
			switch (action) {
			case "rotate big":
				rotatedClipping = ImageRotator.rotate(clippings.get(getCurrentlyPastedClippingIndex()),
						12 + getCurrentlyRotatedBy());
				break;
			case "rotate small":
				rotatedClipping = ImageRotator.rotate(clippings.get(getCurrentlyPastedClippingIndex()),
						3 + getCurrentlyRotatedBy());
				break;
			case "rotate tiny":
				rotatedClipping = ImageRotator.rotate(clippings.get(getCurrentlyPastedClippingIndex()),
						1 + getCurrentlyRotatedBy());
				break;
			}
			int newHeight = rotatedClipping.getHeight();
			int newWidth = rotatedClipping.getWidth();
			while (!(newHeight <= copyToImage.getHeight() && newWidth <= copyToImage.getWidth())) {
				rotatedClipping = Scalr.resize(rotatedClipping, (int) (newWidth * 0.9), (int) (newHeight * 0.9));
				newHeight = rotatedClipping.getHeight();
				newWidth = rotatedClipping.getWidth();
			}
			if (newHeight <= copyToImage.getHeight() && newWidth <= copyToImage.getWidth()) {
				if (imageToPasteTopLabel != null) {
					imageToPasteToLayeredPane.remove(imageToPasteTopLabel);
				}
				imageToPasteTopLabel = null;
				repaintCopyTo();
				if(isFlippedVertically()) {
					rotatedClipping = ImageRotator.flipVertically(rotatedClipping);
				}
				if(isFlippedHorizontally()) {
					rotatedClipping = ImageRotator.flipHorizontally(rotatedClipping);
				}
				pastedClipping = rotatedClipping;

				imageToPasteTopLabel = new JLabel(new ImageIcon(pastedClipping));
				imageToPasteTopLabel.setBounds(0, 0, newWidth, newHeight);
				imageToPasteToLayeredPane.add(imageToPasteTopLabel);
				imageToPasteToLayeredPane.moveToFront(imageToPasteTopLabel);
				setStartX(0);
				setStartY(0);
				setEndX(newWidth - 1);
				setEndY(newHeight - 1);
				imageToPasteTopLabel.setVisible(false);
				imageToPasteTopLabel.setVisible(true);
				dimensions.setText("image dimensions: " + copyToImage.getWidth() + " x " + copyToImage.getHeight());
				dimensions.append("\nclipping dimensions: " + newWidth + " x " + newHeight);
				setClippingPasted(true);
				setClippingRotated(true);
				switch (action) {
				case "rotate big":
					setCurrentlyRotatedBy(getCurrentlyRotatedBy() + 12);
					break;
				case "rotate small":
					setCurrentlyRotatedBy(getCurrentlyRotatedBy() + 3);
					break;
				case "rotate tiny":
					setCurrentlyRotatedBy(getCurrentlyRotatedBy() + 1);
					break;
				}
			}
			break;
		/*
		 * flipping a pasted clipping 
		 */
		case "vertically":
		case "horizontally":
			newHeight = pastedClipping.getHeight();
			newWidth = pastedClipping.getWidth();
			if (imageToPasteTopLabel != null) {
				imageToPasteToLayeredPane.remove(imageToPasteTopLabel);
			}
			imageToPasteTopLabel = null;
			repaintCopyTo();
			switch (action) {
			case "vertically":
				pastedClipping = ImageRotator.flipVertically(pastedClipping);
				if(isFlippedVertically()) {
					setFlippedVertically(false);
				} else {
					setFlippedVertically(true);
				}
				break;
			case "horizontally":
				pastedClipping = ImageRotator.flipHorizontally(pastedClipping);
				if (isFlippedHorizontally()) {
					setFlippedHorizontally(false);
				} else {
					setFlippedHorizontally(true);
				}
				break;
			}

			imageToPasteTopLabel = new JLabel(new ImageIcon(pastedClipping));
			imageToPasteTopLabel.setBounds(0, 0, newWidth, newHeight);
			imageToPasteToLayeredPane.add(imageToPasteTopLabel);
			imageToPasteToLayeredPane.moveToFront(imageToPasteTopLabel);
			setStartX(0);
			setStartY(0);
			setEndX(newWidth - 1);
			setEndY(newHeight - 1);
			imageToPasteTopLabel.setVisible(false);
			imageToPasteTopLabel.setVisible(true);
			break;
		/*
		 * save an image
		 */
		case "save":
			JFileChooser saveChooser = new JFileChooser(userDir);
			int returnVal = saveChooser.showSaveDialog(mainWindow);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				
				try {
				    File outputfile = new File(saveChooser.getSelectedFile() + ".png");
				    ImageIO.write(copyToImage, "png", outputfile);
				} catch (IOException e) {
				    
				}
			}
			break;
		/*
		 * resize the clipping - only if new height / width does not exceed current height / width
		 */
		case "resize clipping":
			if (copyToSelected && isClippingPasted()) {
				newHeight = 0;
				newWidth = 0;
				try {
					newHeight = Integer.valueOf(clippingNewHeight.getText());
					newWidth = Integer.valueOf(clippingNewWidth.getText());
				} catch (Exception e) {
					clippingNewHeight.setText("");
					clippingNewWidth.setText("");
				}
				if (newWidth > 0 && newWidth <= copyToImage.getWidth() && newHeight > 0
						&& newHeight <= copyToImage.getHeight()) {
					if (imageToPasteTopLabel != null) {
						imageToPasteToLayeredPane.remove(imageToPasteTopLabel);
					}

					imageToPasteTopLabel = null;
					repaintCopyTo();
					int currentHeight = 0;
					int currentWidth = 0;
					if (!isClippingRotated()) {
						currentHeight = clippings.get(getCurrentlyPastedClippingIndex()).getHeight();
						currentWidth = clippings.get(getCurrentlyPastedClippingIndex()).getWidth();
					} else {
						currentHeight = pastedClipping.getHeight();
						currentWidth = pastedClipping.getWidth();
					}
					BufferedImage tempClipping = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
					if (!isClippingRotated()) {
						for (int x = 0; x < newWidth; x++) {
							for (int y = 0; y < newHeight; y++) {
								tempClipping.setRGB(x, y,
										clippings.get(getCurrentlyPastedClippingIndex()).getRGB(
												(currentWidth * 100 / newWidth) * x / 100,
												(currentHeight * 100 / newHeight) * y / 100));
							}
						}
					} else {
						for (int x = 0; x < newWidth; x++) {
							for (int y = 0; y < newHeight; y++) {
								tempClipping.setRGB(x, y,
										pastedClipping.getRGB((currentWidth * 100 / newWidth) * x / 100,
												(currentHeight * 100 / newHeight) * y / 100));
							}
						}
					}

					pastedClipping = null;
					pastedClipping = tempClipping;

					imageToPasteTopLabel = new JLabel(new ImageIcon(pastedClipping));
					imageToPasteTopLabel.setBounds(0, 0, newWidth, newHeight);
					imageToPasteToLayeredPane.add(imageToPasteTopLabel);
					imageToPasteToLayeredPane.moveToFront(imageToPasteTopLabel);
					setStartX(0);
					setStartY(0);
					setEndX(newWidth - 1);
					setEndY(newHeight - 1);
					imageToPasteTopLabel.setVisible(false);
					imageToPasteTopLabel.setVisible(true);
					dimensions.setText("image dimensions: " + copyToImage.getWidth() + " x " + copyToImage.getHeight());
					dimensions.append("\nclipping dimensions: " + newWidth + " x " + newHeight);
					setClippingPasted(true);
					rotateBig.setVisible(false);
					rotateSmall.setVisible(false);
					rotateTiny.setVisible(false);
				}
				clippingNewHeight.setText("");
				clippingNewWidth.setText("");

			}
			break;
		/*
		 * embeds pasted clipping at its current position into the "copy to" image
		 */
		case "embed":
			for (int x = getStartX(); x < getEndX(); x++) {
				for (int y = getStartY(); y < getEndY(); y++) {
					if (pastedClipping.getRGB(x - getStartX(), y - getStartY()) != fullyTransparentColor) {
						copyToImage.setRGB(x, y, pastedClipping.getRGB(x - getStartX(), y - getStartY()));
					}
				}
			}
			int copyToHeight = copyToImage.getHeight();
			int copyToWidth = copyToImage.getWidth();
			imageToPasteToLayeredPane.removeAll();
			imageToPasteLabel = new JLabel(new ImageIcon(copyToImage));
			imageToPasteToLayeredPane.setPreferredSize(new Dimension(copyToWidth, copyToHeight));
			setCopyToHeight(copyToHeight);
			setCopyToWidth(copyToWidth);
			imageToPasteLabel.setSize(new Dimension(copyToWidth, copyToHeight));

			imageToPasteToLayeredPane.add(imageToPasteLabel);
			repaintCopyTo();
			setClippingPasted(false);
			if (clippings.size() > 0) {
				showClippingChoice(true);
			}
			dimensions.setText("image dimensions: " + copyToWidth + " x " + copyToHeight);
			saveImage.setEnabled(true);
			resetPasteClippingsButtons();
			break;
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
		/*
		 * selects "copy from" tab
		 */
		case "copy from":
			showClippingChoice(clippings.size() > 0);
			clippingsManipulation.setVisible(false);
			embedOrSavePanel.setVisible(false);
			copyTo.setEnabled(true);
			copyFrom.setEnabled(false);
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
				zoomAdjustment.setVisible(true);
				if (sliderLabel != null) {
					sliderLabel.setVisible(true);
				}
			}
			if (copyFromImage != null) {
				dimensions.setText("image dimensions: " + copyFromImage.getWidth() + " x " + copyFromImage.getHeight());
			} else {
				dimensions.setText("");
			}
			if (clippings.size() > 0) {
				resetPasteClippingsButtons();
			}
			break;
		/*
		 * selects "copy to" tab
		 */
		case "copy to":
			embedOrSavePanel.setVisible(true);
			if (isClippingPasted()) {
				embedClipping.setEnabled(true);
			} else {
				embedClipping.setEnabled(false);
			}

			if (zoomAdjustment != null && sliderLabel != null) {
				zoomAdjustment.setVisible(false);
				sliderLabel.setVisible(false);
			}
			if (copyToImage != null) {
				saveImage.setEnabled(true);
				dimensions.setText("image dimensions: " + copyToImage.getWidth() + " x " + copyToImage.getHeight());
				if (isClippingPasted()) {
					dimensions.append("\nclipping dimensions: " + getPastedClipping().getWidth() + " x "
							+ getPastedClipping().getHeight());
				}
			} else {
				saveImage.setEnabled(false);
				dimensions.setText("");
			}
			clippingsManipulation.setVisible(isClippingPasted());
			showClippingChoice(clippings.size() > 0 && copyToImage != null);
			imageToHighlightScrollPane.setVisible(false);
			imageToPasteToScrollPane.setVisible(true);
			copyTo.setBorder(BorderFactory.createLoweredBevelBorder());
			copyTo.setEnabled(false);
			copyFrom.setEnabled(true);
			copyFrom.setBorder(null);
			copyFromSelected = false;
			copyToSelected = true;
			highlight.setVisible(false);
			unHighlight.setVisible(false);
			highlightSizePick.setVisible(false);
			copyToClipboard.setVisible(false);
			if (clippings.size() > 0) {
				resetPasteClippingsButtons();
			}
			break;
		/*
		 * copies highlighted clipping into the ArrayList of clippings
		 */
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
		/*
		 * loads an image - either into "copy to" tab or "copy from" tab
		 */
		case "load image":
			if (copyFromSelected || copyToSelected) {
				JFileChooser chooser = new JFileChooser(userDir);
				chooser.setFileFilter(filter);
				returnVal = chooser.showOpenDialog(mainWindow);

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
						if (clippings.size() > 0) {
							showClippingChoice(true);
						}
						dimensions.setText("image dimensions: " + width + " x " + height);
						saveImage.setEnabled(true);
					}

				}

			}
			break;
		/*
		 * flicks into next clipping (+1)
		 */
		case ">":
			setCurrentClippingIconIndex(getCurrentClippingIconIndex() + 1);
			lowerIndexClipping.setEnabled(true);
			if ((getCurrentClippingIconIndex() + 1) == clippings.size()) {
				higherIndexClipping.setEnabled(false);
			}
			pasteClipping.setIcon(new ImageIcon(addClippingButtonIcons.get(getCurrentClippingIconIndex())));
			break;
			/*
			 * flicks into previous clipping (-1)
			 */
		case "<":
			setCurrentClippingIconIndex(getCurrentClippingIconIndex() - 1);
			higherIndexClipping.setEnabled(true);
			if (getCurrentClippingIconIndex() == 0) {
				lowerIndexClipping.setEnabled(false);
			}
			pasteClipping.setIcon(new ImageIcon(addClippingButtonIcons.get(getCurrentClippingIconIndex())));
			break;
		/*
		 * pastes clipping into a "copy to" image and scales down the clipping, if
		 * its height or width or both exceed the size of the "copy to" image
		 */
		case "paste clipping":
			if (copyToSelected) {
				if (imageToPasteLabel != null && imageToPasteLabel.getWidth() > 0) {

					if (imageToPasteTopLabel != null) {
						imageToPasteToLayeredPane.remove(imageToPasteTopLabel);
					}
					imageToPasteTopLabel = null;
					repaintCopyTo();

					pastedClipping = null;

					setClippingRotated(false);
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
						clippings.set(currentClippingIconIndex, pastedClipping);

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
					dimensions.setText("image dimensions: " + copyToImage.getWidth() + " x " + copyToImage.getHeight());
					dimensions.append("\nclipping dimensions: " + getPastedClipping().getWidth() + " x "
							+ getPastedClipping().getHeight());
					setClippingPasted(true);
					clippingsManipulation.setVisible(isClippingPasted());
					rotateBig.setVisible(true);
					rotateSmall.setVisible(true);
					rotateTiny.setVisible(true);
					setCurrentlyRotatedBy(0);
					setClippingRotated(false);
					embedClipping.setEnabled(true);
					setCurrentlyPastedClippingIndex(currentClippingIconIndex);
					setFlippedHorizontally(false);
					setFlippedVertically(false);
				}
			} else if (copyFromSelected) {
				insertImageIntoCopyFrom(null);
			}
			break;
		}

	}

	/**
	 * sets the paste clipping button to the first of the clippings
	 */
	private void resetPasteClippingsButtons() {
		pasteClipping.setIcon(new ImageIcon(addClippingButtonIcons.get(0)));
		setCurrentClippingIconIndex(0);
		lowerIndexClipping.setEnabled(false);
		if ((getCurrentClippingIconIndex() + 1) == clippings.size()) {
			higherIndexClipping.setEnabled(false);
		} else {
			higherIndexClipping.setEnabled(true);
		}
	}

	/**
	 * this method deals with pasting an image / clipping into "copy from" tab.
	 * depending on size - different sizes of an image can also be created in 
	 * order to zoom in, if needed
	 * @param path - if null - clipping is pasted
	 */
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
		dimensions.setText("image dimensions: " + width + " x " + height);
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

	/**
	 * repaints copyTo Tab
	 */
	private void repaintCopyTo() {
		imageToPasteToLayeredPane.repaint();
		imageToPasteToLayeredPane.setVisible(false);
		imageToPasteToLayeredPane.setVisible(true);
	}

	/**
	 * repaints copyFrom Tab
	 */
	private void repaintCopyFrom() {
		imageToHighlightLayeredPane.repaint();
		imageToHighlightLayeredPane.setVisible(false);
		imageToHighlightLayeredPane.setVisible(true);
	}
	
	/**
	 * makes the top copy from label transparent
	 */
	private void makeCopyFromTopTransparent() {
		for (int x = 0; x < getCopyFromWidth(); x++) {
			for (int y = 0; y < getCopyFromHeight(); y++) {
				topCopyFromLabelBackground.setRGB(x, y, fullyTransparentColor);
			}
		}
	}

	/**
	 * 
	 * @return copy from height
	 */
	public int getCopyFromHeight() {
		return copyFromHeight;
	}

	/**
	 * sets copy from height as per: 
	 * @param copyFromHeight
	 */
	public void setCopyFromHeight(int copyFromHeight) {
		this.copyFromHeight = copyFromHeight;
	}

	/**
	 * 
	 * @return copy from width 
	 */
	public int getCopyFromWidth() {
		return copyFromWidth;
	}

	/**
	 * sets copy from width as per: 
	 * @param copyFromWidth
	 */
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

	/**
	 * deals with dragging a mouse inside a JscrollPane
	 */
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

	/**
	 * deals with pressing mouse inside JscrollPane, either if copyFrom or
	 * copyTo tabs are selected and there is an image loaded
	 */
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
		moveClippings.setVisible(clippingPasted);

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

	/**
	 * reacts to a change of state of the JSlider used to zoom in / out
	 */
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
	 * @return the clippingRotated
	 */
	public boolean isClippingRotated() {
		return clippingRotated;
	}

	/**
	 * @param clippingRotated
	 *            the clippingRotated to set
	 */
	public void setClippingRotated(boolean clippingRotated) {
		this.clippingRotated = clippingRotated;
	}

	/**
	 * @return the currentlyRotatedBy
	 */
	public int getCurrentlyRotatedBy() {
		return currentlyRotatedBy;
	}

	/**
	 * @param currentlyRotatedBy
	 *            the currentlyRotatedBy to set
	 */
	public void setCurrentlyRotatedBy(int currentlyRotatedBy) {
		this.currentlyRotatedBy = currentlyRotatedBy;
	}

	/**
	 * @return the currentlyPastedClippingIndex
	 */
	public int getCurrentlyPastedClippingIndex() {
		return currentlyPastedClippingIndex;
	}

	/**
	 * @param currentlyPastedClippingIndex
	 *            the currentlyPastedClippingIndex to set
	 */
	public void setCurrentlyPastedClippingIndex(int currentlyPastedClippingIndex) {
		this.currentlyPastedClippingIndex = currentlyPastedClippingIndex;
	}

	/**
	 * @return the flippedVertically
	 */
	public boolean isFlippedVertically() {
		return flippedVertically;
	}

	/**
	 * @param flippedVertically the flippedVertically to set
	 */
	public void setFlippedVertically(boolean flippedVertically) {
		this.flippedVertically = flippedVertically;
	}

	/**
	 * @return the flippedHorizontally
	 */
	public boolean isFlippedHorizontally() {
		return flippedHorizontally;
	}

	/**
	 * @param flippedHorizontally the flippedHorizontally to set
	 */
	public void setFlippedHorizontally(boolean flippedHorizontally) {
		this.flippedHorizontally = flippedHorizontally;
	}
}
