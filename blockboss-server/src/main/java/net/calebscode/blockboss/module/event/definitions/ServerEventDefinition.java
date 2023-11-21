package net.calebscode.blockboss.module.event.definitions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.calebscode.blockboss.module.event.ServerEvent;

public abstract class ServerEventDefinition {

	public static final String UNIVERSAL_MESSAGE_PREFIX = "^\\[(\\d\\d:\\d\\d:\\d\\d)\\] \\[Server thread\\/INFO\\]: ";

	public abstract Pattern getPattern();

	public abstract ServerEvent getEvent(Matcher matcher);

	public final Matcher match(String line) {
		return getPattern().matcher(line);
	}

}
