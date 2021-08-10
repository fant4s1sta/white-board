package model;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import controller.Controller;
import gui.HostGUI;
import gui.UserGUI;
import server.Server;

public class Host extends Controller implements UserInterface {

	private Server server;
	private int userID;
	private String nickname;

	private HostGUI hostGUI;

	public Host(Server server, int userID, String nickname) {
		this.server = server;
		this.userID = userID;
		this.nickname = nickname;
		this.hostGUI = new HostGUI(this);
	}

	public void start() {
		hostGUI.setVisible(true);
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
	public void sendMessage(String message) {
		receiveFromServer(message);
	}

	@Override
	public boolean isDisconnected() {
		return false;
	}

	@Override
	public void disconnect() {

	}

	@Override
	public UserGUI getGUI() {
		return hostGUI;
	}

	@Override
	public void sendToServer(Object message) {
		server.receive(message.toString(), this);
	}

	public String getImageAsString() {
		return hostGUI.getImageAsString(hostGUI.getBoard());
	}

	public void receiveFromServer(String message) {
		super.receiveFromServer(message);
	}

	public boolean approve(String nickname) {
		return JOptionPane.showConfirmDialog(hostGUI, String.format("%s want to share your whiteboard", nickname),
				"Request", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}

	public void kick(int index) throws IOException {
		server.kick(index);
	}

}
