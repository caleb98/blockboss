package net.calebscode.blockboss.module.event.definitions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.calebscode.blockboss.module.event.PlayerLeftEvent;
import net.calebscode.blockboss.module.event.ServerEvent;

public class PlayerLeftEventDefinition extends ServerEventDefinition {

	public static final Pattern PATTERN = Pattern.compile(UNIVERSAL_MESSAGE_PREFIX + "(\\w+) left the game$");

	@Override
	public Pattern getPattern() {
		return PATTERN;
	}

	@Override
	public ServerEvent getEvent(Matcher matcher) {
		return new PlayerLeftEvent(matcher.group(1), matcher.group(2));
	}

}
