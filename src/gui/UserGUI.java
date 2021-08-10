package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.json.JSONArray;
import org.json.JSONObject;

import controller.Controller;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import model.JSON;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JTextPane;
import javax.swing.JTextArea;

public class UserGUI extends JFrame {

	private JMenu file;
	private Integer startX = null;
	private Integer startY = null;
	private Integer endX = null;
	private Integer endY = null;
	private Boolean mouseOn = false;
	private JPanel boardPanel;
	private JPanel userPanel;

	private final Lock lock = new ReentrantLock();
	private final JList<String> userList;
	private BufferedImage board;
	private String selectedTool;
	private JButton selectedButton;
	private Color selectedColor = Color.BLACK;
	private JPanel colorPanel;
	private JSpinner sizeSpinner;

	private Controller controller;

	/**
	 * Create the application.
	 */
	public UserGUI(Controller controller) {
		this.controller = controller;
		userList = new JList<>();
		userList.setVisibleRowCount(10);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setBounds(100, 100, 700, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("Client");

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		file = new JMenu("File");
		menuBar.add(file);

		JMenuItem exitButton = new JMenuItem("Exit");
		exitButton.addActionListener(e -> {
			int option = JOptionPane.showConfirmDialog(UserGUI.this, "Do you want to exit?", "Exit",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (option == JOptionPane.YES_OPTION) {
				dispose();
				System.exit(0);
			}
		});
		file.add(exitButton);

		getContentPane().setLayout(null);

		Dimension dimBtn = new Dimension(30, 30);

		JButton penButton = new JButton("");
		penButton.setToolTipText("Pen");
		penButton.setBounds(6, 6, 30, 30);
		penButton.setIcon(new ImageIcon(UserGUI.class.getResource("/pen.png")));
		penButton.setPreferredSize(dimBtn);
		penButton.setFocusPainted(false);
		penButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectTool(penButton, JSON.PEN);
			}
		});
		getContentPane().add(penButton);

		JButton eraserButton = new JButton("");
		eraserButton.setToolTipText("Eraser");
		eraserButton.setBounds(6, 36, 30, 30);
		eraserButton.setIcon(new ImageIcon(UserGUI.class.getResource("/eraser.png")));
		eraserButton.setPreferredSize(dimBtn);
		eraserButton.setFocusPainted(false);
		eraserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectTool(eraserButton, JSON.ERASER);
			}
		});
		getContentPane().add(eraserButton);

		JButton lineButton = new JButton("");
		lineButton.setToolTipText("Line");
		lineButton.setBounds(6, 66, 30, 30);
		lineButton.setIcon(new ImageIcon(UserGUI.class.getResource("/line.png")));
		lineButton.setPreferredSize(dimBtn);
		lineButton.setFocusPainted(false);
		lineButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectTool(lineButton, JSON.LINE);
			}
		});
		getContentPane().add(lineButton);

		JButton rectangleButton = new JButton("");
		rectangleButton.setToolTipText("Rectangle");
		rectangleButton.setBounds(6, 96, 30, 30);
		rectangleButton.setIcon(new ImageIcon(UserGUI.class.getResource("/rectangle.png")));
		rectangleButton.setPreferredSize(dimBtn);
		rectangleButton.setFocusPainted(false);
		rectangleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectTool(rectangleButton, JSON.RECTANGLE);
			}
		});
		getContentPane().add(rectangleButton);

		JButton pickerButton = new JButton("");
		pickerButton.setToolTipText("Picker");
		pickerButton.setBounds(36, 6, 30, 30);
		pickerButton.setIcon(new ImageIcon(UserGUI.class.getResource("/picker.png")));
		pickerButton.setPreferredSize(dimBtn);
		pickerButton.setFocusPainted(false);
		pickerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectTool(pickerButton, JSON.PICKER);
			}
		});
		getContentPane().add(pickerButton);

		JButton textButton = new JButton("");
		textButton.setToolTipText("Text");
		textButton.setBounds(36, 36, 30, 30);
		textButton.setIcon(new ImageIcon(UserGUI.class.getResource("/text.png")));
		textButton.setPreferredSize(dimBtn);
		textButton.setFocusPainted(false);
		textButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectTool(textButton, JSON.TEXT);
			}
		});
		getContentPane().add(textButton);

		JButton circleButton = new JButton("");
		circleButton.setToolTipText("Circle");
		circleButton.setBounds(36, 66, 30, 30);
		circleButton.setIcon(new ImageIcon(UserGUI.class.getResource("/circle.png")));
		circleButton.setPreferredSize(dimBtn);
		circleButton.setFocusPainted(false);
		circleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectTool(circleButton, JSON.CIRCLE);
			}
		});
		getContentPane().add(circleButton);

		JButton eclipseButton = new JButton("");
		eclipseButton.setToolTipText("Eclipse");
		eclipseButton.setBounds(36, 96, 30, 30);
		eclipseButton.setIcon(new ImageIcon(UserGUI.class.getResource("/eclipse.png")));
		eclipseButton.setPreferredSize(dimBtn);
		eclipseButton.setFocusPainted(false);
		eclipseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectTool(eclipseButton, JSON.OVAL);
			}
		});
		getContentPane().add(eclipseButton);

		JButton recoverButton = new JButton("");
		recoverButton.setToolTipText("Recover");
		recoverButton.setBounds(36, 126, 30, 30);
		recoverButton.setIcon(new ImageIcon(UserGUI.class.getResource("/recover.png")));
		recoverButton.setPreferredSize(dimBtn);
		recoverButton.setFocusPainted(false);
		recoverButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectTool(recoverButton, JSON.RECOVER);
				JSONObject jsonObject = new JSONObject().put(JSON.OPERATION, JSON.CANVAS);
				getController().sendToServer(jsonObject);
			}
		});
		getContentPane().add(recoverButton);

		JButton colorButton = new JButton("");
		colorButton.setToolTipText("Color");
		colorButton.setBounds(6, 126, 30, 30);
		colorButton.setIcon(new ImageIcon(UserGUI.class.getResource("/color.png")));
		colorButton.setPreferredSize(dimBtn);
		colorButton.setFocusPainted(false);
		getContentPane().add(colorButton);
		colorButton.addActionListener(new ActionListener() {
			private JDialog dialog = null;
			private JColorChooser colorChooser = null;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (dialog == null) {
					colorChooser = new JColorChooser(selectedColor);
					dialog = JColorChooser.createDialog(UserGUI.this, "Pick your color", true, colorChooser, null,
							null);
				}

				colorChooser.setColor(selectedColor);
				dialog.setVisible(true);
				dialog.dispose();

				Color newColor = colorChooser.getColor();
				if (newColor != null) {
					setColor(newColor);
				}
			}
		});

		colorPanel = new JPanel();
		colorPanel.setBounds(8, 160, 56, 56);
		getContentPane().add(colorPanel);
		colorPanel.setBackground(Color.black);

		SpinnerModel spinnerModal = new SpinnerNumberModel(1, 1, 100, 1);
		sizeSpinner = new JSpinner(spinnerModal);
		sizeSpinner.setBounds(6, 226, 58, 26);
		sizeSpinner.setValue(12);
		sizeSpinner.setEditor(new JSpinner.DefaultEditor(sizeSpinner));
		getContentPane().add(sizeSpinner);

		userPanel = new JPanel();
		userPanel.setBounds(606, 6, 88, 210);
		userPanel.setBorder(new TitledBorder(null, null, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(userPanel);
		
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrollPane = new JScrollPane(getUserList());
		this.userList.setFixedCellWidth(88);
		this.userList.setFixedCellHeight(21);
		userPanel.add(scrollPane);

		boardPanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				getLock().lock();
				try {
					BufferedImage board = getBoard();
					if (board != null) {
						g.drawImage(board, 0, 0, board.getWidth(this), board.getHeight(this), this);
					}
					drawPreview(g);
				} finally {
					getLock().unlock();
				}

				g.dispose();
			}
		};

		boardPanel.setBounds(74, 6, 520, 438);
		boardPanel.setBackground(Color.white);
		getContentPane().add(boardPanel);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(435, 458, 1, 16);
		getContentPane().add(textArea);

		boardPanel.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (selectedTool == null)
					return;
				repaintBoard();
				if (getSelectedTool().equals(JSON.PEN) || getSelectedTool().equals(JSON.ERASER) || startX == null
						|| startY == null) {
					startX = endX = e.getX();
					startY = endY = e.getY();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (selectedTool == null)
					return;
				repaintBoard();
				endX = e.getX();
				endY = e.getY();
				if (getSelectedTool().equals(JSON.PICKER) || getSelectedTool().equals(JSON.TEXT)) {
					isPickerText();
					return;
				}
				isPenEraser();
				if (endX.equals(startX) && endY.equals(startY)) {
					return;
				}
				isShape();
				resetXY();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				repaintBoard();
				resetXY();
				mouseOn = false;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				repaintBoard();
				resetXY();
				mouseOn = true;
			}
		});

		boardPanel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				repaintBoard();
				if (!mouseOn)
					return;
				if (startX == null || startY == null) {
					startX = e.getX();
					startY = e.getY();
					return;
				}
				endX = e.getX();
				endY = e.getY();
				if (isPenEraser()) {
					startX = endX;
					startY = endY;
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				repaintBoard();
				endX = e.getX();
				endY = e.getY();
			}
		});

		resetBoard();
	}

	public MouseAdapter toolSelector(JButton button, String tool) {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				selectTool(button, tool);
			}
		};
	}

	public Lock getLock() {
		return lock;
	}

	public BufferedImage getBoard() {
		return board;
	}

	public void setBoard(BufferedImage board) {
		this.board = board;
	}

	public Color getColor() {
		return selectedColor;
	}

	public void setColor(Color color) {
		this.selectedColor = color;
		colorPanel.setBackground(color);
	}

	public String getSelectedTool() {
		return selectedTool;
	}

	public JMenu getFile() {
		return file;
	}

	public void repaintBoard() {
		boardPanel.repaint();
	}

	public boolean isCoordinatesValid() {
		return startX != null && startY != null && endX != null && endY != null;
	}

	public void resetXY() {
		startX = null;
		startY = null;
		endX = null;
		endY = null;
	}

	public Controller getController() {
		return controller;
	}

	public void selectTool(JButton button, String tool) {
		if (selectedButton != null)
			selectedButton.setEnabled(true);
		button.setEnabled(false);
		selectedButton = button;
		selectedTool = tool;
		if (selectedTool.equals(JSON.RECOVER)) button.setEnabled(true);
	}

	public void setBoard(String imageStr) {
		resetBoard();
		try {
			BufferedImage newImage = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(imageStr)));
			getLock().lock();
			try {
				board.getGraphics().drawImage(newImage, 0, 0, board.getWidth(null), board.getHeight(null), null);
			} finally {
				getLock().unlock();
			}
		} catch (IOException ignored) {
		}
		repaintBoard();
	}

	public void resetBoard() {
		getLock().lock();
		try {
			Dimension dim = boardPanel.getSize();
			if (getBoard() == null) {
				setBoard(new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB));
			}
			Graphics g = getBoard().getGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, dim.width, dim.height);
			g.dispose();
			repaintBoard();
		} finally {
			getLock().unlock();
		}
	}

	public void setUsers(JSONArray users) {
		synchronized (userList) {
			DefaultListModel<String> listModel = new DefaultListModel<>();
			this.userList.clearSelection();
			for (int i = 0; i < users.length(); i++) {
				JSONObject user = users.getJSONObject(i);
				listModel.addElement(String.format("%s [id: %d]", user.optString(JSON.NICKNAME), user.optInt(JSON.USERID)));
			}
			this.userList.setModel(listModel);
		}
	}

	public JList<String> getUserList() {
		return userList;
	}

	public void draw(JSONObject jsonObject) {
		Graphics2D g = (Graphics2D) getBoard().getGraphics();
		g.setColor(new Color(jsonObject.optInt(JSON.COLOR), true));
		String tool = jsonObject.optString(JSON.TOOL);

		int x0 = jsonObject.optInt(JSON.X0);
		int y0 = jsonObject.optInt(JSON.Y0);
		int width = jsonObject.optInt(JSON.WIDTH);
		int height = jsonObject.optInt(JSON.HEIGHT);
		int size = jsonObject.optInt(JSON.SIZE);
		int x1 = jsonObject.optInt(JSON.X1);
		int y1 = jsonObject.optInt(JSON.Y1);

		if (tool.equals(JSON.PEN)) {
			g.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
			g.drawLine(x0, y0, x1, y1);
		} else if (tool.equals(JSON.ERASER)) {
			g.setColor(Color.WHITE);
			g.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
			g.drawLine(x0, y0, x1, y1);
		} else if (tool.equals(JSON.TEXT)) {
			String text = jsonObject.optString(JSON.TEXT);
			g.setFont(new Font("Calibri", Font.PLAIN, size));
			g.drawString(text, x0, y0);
		} else if (tool.equals(JSON.LINE)) {
			g.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
			g.drawLine(x0, y0, x1, y1);
		} else if (tool.equals(JSON.RECTANGLE)) {
			g.fillRect(x0, y0, width, height);
		} else if (tool.equals(JSON.CIRCLE)) {
			g.fillOval(x0 - size, y0 - size, 2 * size, 2 * size);
		} else if (tool.equals(JSON.OVAL)) {
			g.fillOval(x0, y0, width, height);
		}
		g.dispose();
		boardPanel.repaint();
	}

	public void drawPreview(Graphics g) {
		if (selectedTool == null)
			return;

		if (isCoordinatesValid()) {
			int x = Math.min(startX, endX);
			int y = Math.min(startY, endY);
			int width = Math.abs(endX - startX);
			int height = Math.abs(endY - startY);
			int radius = (int) Math.sqrt(width * width + height * height);

			getLock().lock();
			try {
				g.setColor(selectedColor);
				if (selectedTool.equals(JSON.LINE)) {
					Graphics2D g2d = (Graphics2D) g;
					g2d.setStroke(new BasicStroke((int) sizeSpinner.getValue(), BasicStroke.CAP_ROUND,
							BasicStroke.JOIN_BEVEL));
					g.drawLine(startX, startY, endX, endY);
				} else if (selectedTool.equals(JSON.RECTANGLE)) {
					g.fillRect(x, y, width, height);
				} else if (selectedTool.equals(JSON.OVAL)) {
					g.fillOval(x, y, width, height);
				} else if (selectedTool.equals(JSON.CIRCLE)) {
					g.fillOval(startX - radius, startY - radius, 2 * radius, 2 * radius);
				} else {

				}
			} finally {
				getLock().unlock();
			}
		}
	}

	public boolean isShape() {
		if (selectedTool == null)
			return false;
		if (!isCoordinatesValid())
			return false;

		int x = Math.min(startX, endX);
		int y = Math.min(startY, endY);
		int w = Math.abs(endX - startX);
		int h = Math.abs(endY - startY);

		JSONObject jsonObject = new JSONObject().put(JSON.TOOL, selectedTool).put(JSON.COLOR, selectedColor.getRGB())
				.put(JSON.X0, startX).put(JSON.Y0, startY);

		if (selectedTool.equals(JSON.LINE)) {
			jsonObject.put(JSON.X1, endX).put(JSON.Y1, endY).put(JSON.SIZE, sizeSpinner.getValue());
		} else if (selectedTool.equals(JSON.RECTANGLE)) {
			jsonObject.put(JSON.HEIGHT, h).put(JSON.WIDTH, w).put(JSON.X0, x).put(JSON.Y0, y);
		} else if (selectedTool.equals(JSON.OVAL)) {
			jsonObject.put(JSON.HEIGHT, h).put(JSON.WIDTH, w).put(JSON.X0, x).put(JSON.Y0, y);
		} else if (selectedTool.equals(JSON.CIRCLE)) {
			jsonObject.put(JSON.SIZE, (int) Math.sqrt(w * w + h * h));
		} else {
			return false;
		}

		JSONObject message = new JSONObject().put(JSON.OPERATION, JSON.PAINT).put(JSON.PAINT, jsonObject);
		getController().sendToServer(message);

		return true;
	}

	public boolean isPickerText() {
		if (selectedTool == null)
			return false;
		if (!isCoordinatesValid())
			return false;

		JSONObject drawing = new JSONObject().put(JSON.TOOL, selectedTool).put(JSON.X0, endX).put(JSON.Y0, endY)
				.put(JSON.COLOR, selectedColor.getRGB()).put(JSON.SIZE, sizeSpinner.getValue());

		if (selectedTool.equals(JSON.TEXT)) {
			String text = JOptionPane.showInputDialog(this, "Input Text");
			if (text != null && !text.isEmpty()) {
				drawing.put(JSON.TEXT, text);
			} else {
				return false;
			}
		} else if (selectedTool.equals(JSON.PICKER)) {
			setColor(new Color(getBoard().getRGB(endX, endY)));
			return false;
		} else {
			return false;
		}

		JSONObject message = new JSONObject().put(JSON.OPERATION, JSON.PAINT).put(JSON.PAINT, drawing);
		getController().sendToServer(message);

		return true;
	}

	public boolean isPenEraser() {
		if (selectedTool == null)
			return false;
		if (!isCoordinatesValid())
			return false;

		JSONObject drawing = new JSONObject().put(JSON.TOOL, selectedTool).put(JSON.X0, startX).put(JSON.Y0, startY)
				.put(JSON.X1, endX).put(JSON.Y1, endY).put(JSON.SIZE, sizeSpinner.getValue());

		if (selectedTool.equals(JSON.PEN)) {
			drawing.put(JSON.COLOR, selectedColor.getRGB());
		} else if (selectedTool.equals(JSON.ERASER)) {
			drawing.put(JSON.COLOR, selectedColor.getRGB());
		} else {
			return false;
		}

		JSONObject message = new JSONObject().put(JSON.OPERATION, JSON.PAINT).put(JSON.PAINT, drawing);
		getController().sendToServer(message);

		return true;
	}
}
