package net.calebscode.blockboss.module.event;

public class PlayerChatEvent extends ServerEvent {

	public static final String ID = "PlayerChat";

	public final String playerName;
	public final String message;

	public PlayerChatEvent(String timestamp, String playerName, String message) {
		super(ID, timestamp);
		this.playerName = playerName;
		this.message = message;
	}

}
