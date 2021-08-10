package gui;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.JSONArray;
import org.json.JSONObject;

import model.Host;
import model.JSON;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HostGUI extends UserGUI {

	private JFrame frame;
	private File currentFile = null;
	private JMenuItem saveButton;

	/**
	 * Create the application.
	 */
	public HostGUI(Host host) {
		super(host);
		getUserList().setBounds(606, 15, 172, 128);
		setTitle("Host");

		JButton kickButton = new JButton("Kick");
		kickButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = getUserList().getSelectedIndex();
				try {
					host.kick(index);
				} catch (IOException e1) {
				}
			}
		});
		kickButton.setBounds(606, 222, 88, 30);
		kickButton.setFocusPainted(false);
		getContentPane().add(kickButton);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		getFile().removeAll();

		JMenuItem newButton = new JMenuItem("New");
		newButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JSONObject jsonObject = new JSONObject().put(JSON.OPERATION, JSON.CANVAS);
				getController().sendToServer(jsonObject);
			}
		});
		getFile().add(newButton);

		JMenuItem openButton = new JMenuItem("Open");
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(currentFile);
				fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
				fileChooser.setFileFilter(new FileNameExtensionFilter(String.format("image (*%s)", ".png"), "png"));
				int option = fileChooser.showOpenDialog(HostGUI.this);
				if (option == JFileChooser.APPROVE_OPTION) {
					currentFile = fileChooser.getSelectedFile();
					try {
						saveButton.setEnabled(true);
						BufferedImage image = ImageIO.read(currentFile);
						JSONObject json = new JSONObject().put(JSON.OPERATION, JSON.CANVAS).put(JSON.CANVAS,
								getImageAsString(image));
						getController().sendToServer(json);
					} catch (IOException exception) {
						JOptionPane.showMessageDialog(HostGUI.this, "The file can not be opened.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		getFile().add(openButton);

		saveButton = new JMenuItem("Save");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentFile == null) {
					return;
				}
				try {
					if (!currentFile.getName().endsWith(".png")) {
						currentFile = new File(currentFile.getAbsolutePath() + ".png");
					}
					ImageIO.write(getBoard(), "png", currentFile);
				} catch (IOException exception) {
				}
				saveButton.setEnabled(true);
			}
		});
		saveButton.setEnabled(false);
		getFile().add(saveButton);

		JMenuItem saveAsButton = new JMenuItem("Save As");
		saveAsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(currentFile);
				fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
				fileChooser
						.setFileFilter(new FileNameExtensionFilter(String.format("Image (*%s)", ".png"), "png"));
				int option = fileChooser.showSaveDialog(HostGUI.this);
				if (option == JFileChooser.APPROVE_OPTION) {
					currentFile = fileChooser.getSelectedFile();
					saveButton.setEnabled(true);
					if (currentFile == null) {
						return;
					}
					try {
						if (!currentFile.getName().endsWith(".png")) {
							currentFile = new File(currentFile.getAbsolutePath() + ".png");
						}
						ImageIO.write(getBoard(), "png", currentFile);
					} catch (IOException exception) {
					}
				}
			}

		});
		getFile().add(saveAsButton);
		
		JMenuItem exitButton = new JMenuItem("Exit");
		exitButton.addActionListener(e -> {
			int option = JOptionPane.showConfirmDialog(HostGUI.this, "Do you want to exit?", "Exit",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (option == JOptionPane.YES_OPTION) {
				dispose();
				System.exit(0);
			}
		});
		getFile().add(exitButton);

	}

	public String getImageAsString(BufferedImage image) {
		String imageStr = null;
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ImageIO.write(image, "png", Base64.getEncoder().wrap(stream));
			imageStr = stream.toString();
			stream.close();
		} catch (IOException | IllegalArgumentException e) {
		}
		return imageStr;
	}
	
	public void setUsers(JSONArray users) {
		super.setUsers(users);
	}

}
