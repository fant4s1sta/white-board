package controller;

import javax.swing.SwingUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import gui.UserGUI;

import model.JSON;

public abstract class Controller {

	public abstract UserGUI getGUI();

	public abstract void start();

	public abstract void sendToServer(Object message);

	public void receiveFromServer(Object message) {
		SwingUtilities.invokeLater(() -> {
			try {

				JSONObject jsonObject;
				if (message instanceof JSONObject) {
					jsonObject = (JSONObject) message;
				} else {
					jsonObject = new JSONObject(message.toString());
				}
				
				String operation = jsonObject.optString(JSON.OPERATION);
				UserGUI gui = getGUI();
				if (operation.equals(JSON.PAINT)) {
					gui.draw(jsonObject.optJSONObject(JSON.PAINT));
				} else if (operation.equals(JSON.USERS)) {
					gui.setUsers(jsonObject.optJSONArray(JSON.USERS));
				} else if (operation.equals(JSON.CANVAS)) {
					gui.setBoard(jsonObject.optString(JSON.CANVAS));
				}
			} catch (JSONException e) {
				System.out.println(e.getMessage());
			}
		});
	}

}
