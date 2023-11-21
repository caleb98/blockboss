package net.calebscode.blockboss.module.event;

public abstract class ServerEvent {

	public final String id;
	public final String timestamp;

	public ServerEvent(String id, String timestamp) {
		this.id = id;
		this.timestamp = timestamp;
	}

}
