package net.calebscode.blockboss.module.event.definitions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.calebscode.blockboss.module.event.PlayerAuthEvent;
import net.calebscode.blockboss.module.event.ServerEvent;

public class PlayerAuthEventDefinition extends ServerEventDefinition {

	public static final Pattern PATTERN = Pattern.compile(UNIVERSAL_MESSAGE_PREFIX
			+ "UUID of player (\\w+) is ([\\dabcdef]{8}-[\\dabcdef]{4}-[\\dabcdef]{4}-[\\dabcdef]{4}-[\\dabcdef]{12})$");

	@Override
	public Pattern getPattern() {
		return PATTERN;
	}

	@Override
	public ServerEvent getEvent(Matcher matcher) {
		return new PlayerAuthEvent(matcher.group(1), matcher.group(2), matcher.group(3));
	}

}
