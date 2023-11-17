package net.calebscode.blockboss.module.event;

public class WorldSavedEvent extends ServerEvent {

	public static final String ID = "WorldSaved";
	
	public WorldSavedEvent(String timestamp) {
		super(ID, timestamp);
	}

}
