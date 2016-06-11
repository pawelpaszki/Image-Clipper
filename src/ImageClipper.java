import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageClipper implements ActionListener {

	private JFrame mainWindow;
	private JPanel selectTabPanel;
	private JScrollPane imageToHighlightScrollPane;
	private JLayeredPane imageToHighlightLayeredPane;
	private JLabel imageToHighlightLabel;
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

	public static void main(String[] args) {
		// standard thread invocation in swing apps
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ImageClipper clipper = new ImageClipper();
					clipper.initialise();

				} catch (Exception e) {
					e.printStackTrace();
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

		imageToHighlightLayeredPane = new JLayeredPane();

		// imageToHighlightLayeredPane.setBorder(BorderFactory.createLineBorder(Color.blue));
		//imageToHighlightLayeredPane.setPreferredSize(new Dimension(1800, 1600));

		imageToHighlightScrollPane = new JScrollPane(imageToHighlightLayeredPane);
		imageToHighlightScrollPane.setBounds(5, 45, 1185, 520);
		imageToHighlightScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		imageToHighlightScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		// imageToHighlightScrollPane.setVisible(false);

		imageToPasteToLayeredPane = new JLayeredPane();

		// imageToPasteToLayeredPane.setBorder(BorderFactory.createLineBorder(Color.red));
		//imageToPasteToLayeredPane.setPreferredSize(new Dimension(1800, 1600));

		imageToPasteToScrollPane = new JScrollPane(imageToPasteToLayeredPane);
		imageToPasteToScrollPane.setBounds(5, 45, 1185, 520);
		imageToPasteToScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		imageToPasteToScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		imageToPasteToScrollPane.setVisible(false);

		mainWindow.getContentPane().add(selectTabPanel);
		mainWindow.getContentPane().add(imageToHighlightScrollPane);
		mainWindow.getContentPane().add(imageToPasteToScrollPane);
		mainWindow.getContentPane().add(loadImage);

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
						BufferedImage bi = null;
						try {
							bi = ImageIO.read(file);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						int height = bi.getHeight();
						int width = bi.getHeight();
						imageToHighlightLayeredPane.removeAll();
						imageToHighlightLabel = new JLabel(new ImageIcon(chooser.getSelectedFile().getAbsolutePath()));
						imageToHighlightLayeredPane.setPreferredSize(new Dimension(height, width));
						imageToHighlightLabel.setSize(new Dimension(height, width));
						imageToHighlightLayeredPane.add(imageToHighlightLabel);
						imageToHighlightLayeredPane.moveToFront(imageToHighlightLabel);
						imageToHighlightLayeredPane.repaint();
						imageToHighlightLayeredPane.setVisible(false);
						imageToHighlightLayeredPane.setVisible(true);
						
					}

				}

			}
			break;
		}

	}
}
