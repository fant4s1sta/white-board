package model;

import java.io.IOException;

public interface UserInterface {

	public int getUserID();

	public String getNickname();

	public void start();

	public void sendMessage(String message);

	public boolean isDisconnected();

	public void disconnect() throws IOException;

}
