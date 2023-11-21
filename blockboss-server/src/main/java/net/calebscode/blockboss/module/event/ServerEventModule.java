package net.calebscode.blockboss.module.event;

import java.util.ArrayList;
import java.util.regex.Matcher;

import net.calebscode.blockboss.module.BlockBossModule;
import net.calebscode.blockboss.module.event.definitions.AchievementGetEventDefinition;
import net.calebscode.blockboss.module.event.definitions.PlayerAuthEventDefinition;
import net.calebscode.blockboss.module.event.definitions.PlayerChatEventDefinition;
import net.calebscode.blockboss.module.event.definitions.PlayerJoinedEventDefinition;
import net.calebscode.blockboss.module.event.definitions.PlayerLeftEventDefinition;
import net.calebscode.blockboss.module.event.definitions.ServerEventDefinition;
import net.calebscode.blockboss.module.event.definitions.ServerLoadedEventDefinition;
import net.calebscode.blockboss.module.event.definitions.WorldSavedEventDefinition;
import net.calebscode.blockboss.process.MinecraftServer;
import net.calebscode.blockboss.server.BlockBossServer;

public class ServerEventModule extends BlockBossModule {

	public static final String MODULE_ID = "ServerEventModule";

	private ArrayList<ServerEventDefinition> eventDefinitions = new ArrayList<>();

	public ServerEventModule(BlockBossServer blockBoss) {
		super(blockBoss);
		eventDefinitions.add(new AchievementGetEventDefinition());
		eventDefinitions.add(new PlayerAuthEventDefinition());
		eventDefinitions.add(new PlayerChatEventDefinition());
		eventDefinitions.add(new PlayerJoinedEventDefinition());
		eventDefinitions.add(new PlayerLeftEventDefinition());
		eventDefinitions.add(new ServerLoadedEventDefinition());
		eventDefinitions.add(new WorldSavedEventDefinition());
	}

	@Override
	public void init() {

	}

	@Override
	public void configure(MinecraftServer process) {
		process.addStdoutListener(this::processEvents);
	}

	private void processEvents(String line) {
		for (var eventDef : eventDefinitions) {
			Matcher match = eventDef.match(line);
			if (match.matches()) {
				ServerEvent event = eventDef.getEvent(match);
				blockBoss.sendEvent(event);
				logger().info("Triggered event: {}", event.id);
			}
		}
	}

}
