package net.calebscode.blockboss.module.event.definitions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.calebscode.blockboss.module.event.AchievementGetEvent;
import net.calebscode.blockboss.module.event.ServerEvent;

public class AchievementGetEventDefinition extends ServerEventDefinition {

	public static final Pattern PATTERN = Pattern
			.compile(UNIVERSAL_MESSAGE_PREFIX + "(\\w+) has made the advancement \\[(.*)\\]$");

	@Override
	public Pattern getPattern() {
		return PATTERN;
	}

	@Override
	public ServerEvent getEvent(Matcher matcher) {
		return new AchievementGetEvent(matcher.group(1), matcher.group(2), matcher.group(3));
	}

}
