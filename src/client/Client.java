package client;

import java.awt.EventQueue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JOptionPane;

import org.json.JSONObject;

import controller.Controller;
import gui.UserGUI;
import model.JSON;
import server.Server;

public class Client extends Controller {

	private String address;
	private int portNumber;
	private String nickname;

	private UserGUI userGUI;

	private DataInputStream input;
	private DataOutputStream output;

	private Socket socket;

	private Lock sendLock = new ReentrantLock();

	private Thread thread;

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Usage: <address> <port number> <nickname>");
			System.exit(1);
		}

		String address = args[0];
		int portNumber = Integer.parseInt(args[1]);
		String nickname = args[2];
		
		try {
			Client client = new Client(address, portNumber, nickname);
			client.start();
		} catch (NumberFormatException e) {
			System.out.println("Number format is wrong.");
		}
	}

	public Client(String address, int portNumber, String nickname) {
		this.address = address;
		this.portNumber = portNumber;
		this.nickname = nickname;
	}

	public void start() {
		try {
			this.socket = new Socket(address, portNumber);
			this.input = new DataInputStream(socket.getInputStream());
			this.output = new DataOutputStream(socket.getOutputStream());

			sendToServer(new JSONObject().put(JSON.OPERATION, JSON.NICKNAME).put(JSON.NICKNAME, nickname));

			this.userGUI = new UserGUI(this);

			this.thread = new Thread(() -> {

				while (true) {
					try {
						String inputMessage = input.readUTF();
						receiveFromServer(inputMessage);
					} catch (IOException exception) {
						if (exception != null) {
							String errorMessage = "The connection is lost.\n";
							if (exception.getMessage() != null) {
								errorMessage += "Error: " + exception.getMessage();
							} else {
								errorMessage += "You are kicked out of the room.";
							}
							JOptionPane.showMessageDialog(userGUI, errorMessage, "Warning", JOptionPane.ERROR_MESSAGE);
							System.exit(0);
						}
					}
				}
			});

			EventQueue.invokeLater(() -> {
				try {
					userGUI.setVisible(true);
					this.thread.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

		} catch (IOException exception) {
			System.err.format("There was an error setting up the socket (%s)\n", exception.getMessage());
			System.exit(1);
		}

	}

	@Override
	public UserGUI getGUI() {
		return userGUI;
	}

	@Override
	public void sendToServer(Object message) {
		sendLock.lock();

		try {
			try {
				this.output.writeUTF(message.toString());
			} catch (IOException exception) {
				if (exception != null) {
					String errorMessage = "The connection is lost.\n";
					if (exception.getMessage() != null) {
						errorMessage += "Error: " + exception.getMessage();
					} else {
						errorMessage += "You are kicked out of the room.";
					}
					JOptionPane.showMessageDialog(userGUI, errorMessage, "Warning", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}
				try {
					thread.interrupt();
				} catch (SecurityException ignored) {
				}

			}
		} finally {
			sendLock.unlock();
		}
	}

}
