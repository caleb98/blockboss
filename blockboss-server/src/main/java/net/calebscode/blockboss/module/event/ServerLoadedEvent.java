package net.calebscode.blockboss.module.event;

public class ServerLoadedEvent extends ServerEvent {

	public static final String ID = "ServerLoaded";

	public final String startupTime;

	public ServerLoadedEvent(String timestamp, String startupTime) {
		super(ID, timestamp);
		this.startupTime = startupTime;
	}

}
