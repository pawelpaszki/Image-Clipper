import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImageClipper implements ActionListener {

	private JFrame mainWindow;
	private final Dimension minimumSize = new Dimension(500,300);
	private JPanel buttonPanel;
	
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
		mainWindow.setMinimumSize(minimumSize);
		mainWindow.setLocationRelativeTo(null);// window is centred
		mainWindow.setVisible(true); // makes the window visible
		// closes the window, when stop button is pressed
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.getContentPane().setBackground(Color.black);
		mainWindow.setLayout(null);
		
		buttonPanel = new JPanel();
		buttonPanel.setBounds(5, 5, 300, 30);
		buttonPanel.setBorder(BorderFactory.createLineBorder(Color.white));
		buttonPanel.setBackground(Color.black);
		buttonPanel.setLayout(new GridLayout(1,2));
		
		
		buttonPanel.add(makeButton("copy from"));
		buttonPanel.add(makeButton("copy to"));
		
		
		mainWindow.getContentPane().add(buttonPanel);
		
	}
	
	private JButton makeButton(String text) {
		JButton button = new JButton(text);
		button.setFocusPainted(false);
		button.addActionListener(this);
		return button;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
