package net.calebscode.blockboss.module.event.definitions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.calebscode.blockboss.module.event.PlayerJoinedEvent;
import net.calebscode.blockboss.module.event.ServerEvent;

public class PlayerJoinedEventDefinition extends ServerEventDefinition {

	public static final Pattern PATTERN = Pattern.compile(UNIVERSAL_MESSAGE_PREFIX + "([\\w]{3,16}) joined the game$");
	
	@Override
	public Pattern getPattern() {
		return PATTERN;
	}

	@Override
	public ServerEvent getEvent(Matcher matcher) {
		return new PlayerJoinedEvent(matcher.group(1), matcher.group(2));
	}

}
