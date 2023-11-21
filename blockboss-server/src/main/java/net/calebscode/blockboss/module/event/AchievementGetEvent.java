package net.calebscode.blockboss.module.event;

public class AchievementGetEvent extends ServerEvent {

	public static final String ID = "AchievementGet";

	public final String playerName;
	public final String achievement;

	public AchievementGetEvent(String timestamp, String playerName, String achievement) {
		super(ID, timestamp);
		this.playerName = playerName;
		this.achievement = achievement;
	}

}
