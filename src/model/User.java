package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import server.Server;

public class User implements UserInterface {

	private int userID;
	private String nickname;
	private Server server;
	private Socket socket;

	private DataInputStream input;
	private DataOutputStream output;

	private ExecutorService executor;
	private Thread thread;

	private Boolean terminated = false;
	private Boolean started = false;

	public User(int userID, Socket socket, Server server) throws IOException {
		this.userID = userID;
		this.socket = socket;
		this.server = server;

		this.input = new DataInputStream(socket.getInputStream());
		this.output = new DataOutputStream(socket.getOutputStream());

		this.executor = Executors.newSingleThreadExecutor();

		JSONObject jsonObject = new JSONObject(this.input.readUTF());
		this.nickname = jsonObject.optString(JSON.NICKNAME);

		thread = new Thread(() -> {

			while (true) {
				if (isDisconnected()) {
					break;
				}

				try {
					String inputMessage = input.readUTF();

					if (!isStarted()) {
						return;
					}

					server.receive(inputMessage, this);
				} catch (IOException ioe) {
					break;
				}
			}

			try {
				disconnect();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			server.updateUserList();

		});

		thread.start();
	}

	@Override
	public int getUserID() {
		return userID;
	}

	@Override
	public String getNickname() {
		return nickname;
	}

	@Override
	public void start() {
		synchronized (started) {
			started = true;
		}
	}

	@Override
	public void sendMessage(String message) {
		if (isDisconnected()) {
			return;
		}

		this.executor.execute(() -> {
			try {
				this.output.writeUTF(message);
			} catch (IOException exception) {
				try {
					disconnect();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
				server.updateUserList();
			}
		});
	}

	@Override
	public boolean isDisconnected() {
		return terminated;
	}

	@Override
	public void disconnect() throws IOException {
		synchronized (terminated) {
			terminated = true;
			thread.interrupt();
			input.close();
			output.close();
			socket.close();
			executor.shutdown();
		}
	}

	public synchronized boolean isStarted() {
		return started;
	}

}
