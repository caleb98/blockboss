package net.calebscode.blockboss.module.event;

public class PlayerJoinedEvent extends ServerEvent {
	
	public static final String ID = "PlayerJoined";

	public final String playerName;
	
	public PlayerJoinedEvent(String timestamp, String playerName) {
		super(ID, timestamp);
		this.playerName = playerName;
	}
	
}
