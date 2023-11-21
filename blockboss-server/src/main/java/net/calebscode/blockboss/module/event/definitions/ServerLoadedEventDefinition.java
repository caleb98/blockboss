package net.calebscode.blockboss.module.event.definitions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.calebscode.blockboss.module.event.ServerEvent;
import net.calebscode.blockboss.module.event.ServerLoadedEvent;

public class ServerLoadedEventDefinition extends ServerEventDefinition {

	public static final Pattern PATTERN = Pattern
			.compile(UNIVERSAL_MESSAGE_PREFIX + "Done \\((\\d+\\.\\d+)s\\)! For help, type \"help\"$");

	@Override
	public Pattern getPattern() {
		return PATTERN;
	}

	@Override
	public ServerEvent getEvent(Matcher matcher) {
		return new ServerLoadedEvent(matcher.group(1), matcher.group(2));
	}

}
