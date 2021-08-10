package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.Host;
import model.JSON;
import model.User;
import model.UserInterface;

public class Server {

	private int portNumber;
	private String address;
	private String nickname;
	private Integer userID;

	private Host host;
	private List<UserInterface> users;

	private Lock lock = new ReentrantLock();

	private int currentUserID;

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Usage: <address> <port number> <nickname>");
			System.exit(1);
		}

		String address = args[0];
		int portNumber = Integer.parseInt(args[1]);
		String nickname = args[2];

		try {
			Server server = new Server(address, portNumber, nickname);
			server.start();
		} catch (NumberFormatException e) {
			System.out.println("Number format is wrong.");
		}
	}

	public Server(String address, int portNumber, String nickname) {
		this.address = address;
		this.portNumber = portNumber;
		this.nickname = nickname;
		this.users = new ArrayList<>();
		this.currentUserID = 0;
	}

	public void start() {
		try {
			ServerSocket serverSocket = new ServerSocket(this.portNumber, 10, InetAddress.getByName(this.address));
			System.out.format("The server socket is running at %s:%d\n", address, portNumber);
			
			this.host = new Host(this, this.currentUserID, this.nickname);
			addUserToList(this.host);
			
			this.currentUserID++;

			new Thread(new Runnable() {

				@Override
				public void run() {
					host.start();
				}

			}).start();

			while (true) {
				Socket clientSocket = serverSocket.accept();
				new Thread(() -> {
					addUser(clientSocket);
				}).start();
			}
		} catch (UnknownHostException e) {
			System.out.format(e.getMessage());
		} catch (IOException e) {
			System.out.format(e.getMessage());
		}
	}

	public void addUser(Socket socket) {
		int newUserID = this.currentUserID;
		this.currentUserID++;
		try {
			User user = new User(newUserID, socket, this);

			if (host.approve(user.getNickname())) {
				addUserToList(user);
				user.sendMessage(getJSONBoardMessage());
				updateMessage(getUserListMessage());
				user.start();
			} else {
				user.disconnect();
			}
		} catch (IOException ioe) {
			System.err.format("There was an error with a client (%s)\n", ioe.getMessage());
		}

	}

	public void receive(String message, UserInterface user) {
		try {
			updateMessage(message);
		} catch (JSONException exception) {
		}
	}

	public String getJSONBoardMessage() {
		return new JSONObject().put(JSON.OPERATION, JSON.CANVAS).put(JSON.CANVAS, host.getImageAsString()).toString();
	}

	public void updateMessage(String message) {
		lock.lock();
		try {
			for (int i = 0; i < users.size(); i++) {
				users.get(i).sendMessage(message);
			}
			updateUserList();
		} finally {
			lock.unlock();
		}
	}
	
	public void updateUserList() {
		lock.lock();
		try {
			if (users.removeIf(UserInterface::isDisconnected)) {
				updateMessage(getUserListMessage());
			}
		} finally {
			lock.unlock();
		}
	}

	public void addUserToList(UserInterface user) {
		lock.lock();
		try {
			users.add(user);
		} finally {
			lock.unlock();
		}
		updateMessage(getUserListMessage());
	}

	public String getUserListMessage() {
		List<JSONObject> usernames = new ArrayList<>();

		lock.lock();
		try {
			for (UserInterface user : users) {
				usernames.add(
						new JSONObject().put(JSON.NICKNAME, user.getNickname()).put(JSON.USERID, user.getUserID()));
			}
		} finally {
			lock.unlock();
		}

		return new JSONObject().put(JSON.OPERATION, JSON.USERS).put(JSON.USERS, new JSONArray(usernames)).toString();
	}

	public void kick(int index) throws IOException {
		if (index == 0) return;
		users.get(index).disconnect();
		updateUserList();
	}

}
