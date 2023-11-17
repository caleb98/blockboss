package net.calebscode.blockboss.module.event;

public class PlayerLeftEvent extends ServerEvent {

	public static final String ID = "PlayerLeft";
	
	public final String playerName;
	
	public PlayerLeftEvent(String timestamp, String playerName) {
		super(ID, timestamp);
		this.playerName = playerName;
	}

	
}
