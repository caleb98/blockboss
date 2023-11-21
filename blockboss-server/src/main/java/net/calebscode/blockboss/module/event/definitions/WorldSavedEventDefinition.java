package net.calebscode.blockboss.module.event.definitions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.calebscode.blockboss.module.event.ServerEvent;
import net.calebscode.blockboss.module.event.WorldSavedEvent;

public class WorldSavedEventDefinition extends ServerEventDefinition {

	public static final Pattern PATTERN = Pattern.compile(UNIVERSAL_MESSAGE_PREFIX + "Saved the game$");

	@Override
	public Pattern getPattern() {
		return PATTERN;
	}

	@Override
	public ServerEvent getEvent(Matcher matcher) {
		return new WorldSavedEvent(matcher.group(1));
	}

}
