package net.calebscode.blockboss.module.event.definitions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.calebscode.blockboss.module.event.PlayerChatEvent;
import net.calebscode.blockboss.module.event.ServerEvent;

public class PlayerChatEventDefinition extends ServerEventDefinition {

	public static final Pattern PATTERN = Pattern.compile(UNIVERSAL_MESSAGE_PREFIX + "<(\\w+)> (.*)$");

	@Override
	public Pattern getPattern() {
		return PATTERN;
	}

	@Override
	public ServerEvent getEvent(Matcher matcher) {
		return new PlayerChatEvent(matcher.group(1), matcher.group(2), matcher.group(3));
	}

}
