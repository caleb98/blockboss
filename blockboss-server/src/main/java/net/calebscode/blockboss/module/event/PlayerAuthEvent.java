package net.calebscode.blockboss.module.event;

public class PlayerAuthEvent extends ServerEvent {

	public static final String ID = "PlayerAuth";
	
	public final String playerName;
	public final String uuid;
	
	public PlayerAuthEvent(String timestamp, String playerName, String uuid) {
		super(ID, timestamp);
		this.playerName = playerName;
		this.uuid = uuid;
	}

}
