package net.calebscode.blockboss.module.event;

import java.util.ArrayList;
import java.util.regex.Matcher;

import net.calebscode.blockboss.module.BlockBossModule;
import net.calebscode.blockboss.module.event.definitions.PlayerJoinedEventDefinition;
import net.calebscode.blockboss.module.event.definitions.ServerEventDefinition;
import net.calebscode.blockboss.server.BlockBossServer;
import net.calebscode.blockboss.server.process.MinecraftServer;

public class ServerEventModule extends BlockBossModule {

	public static final String MODULE_ID = "ServerEventModule";
	
	private ArrayList<ServerEventDefinition> eventDefinitions = new ArrayList<>();
	
	public ServerEventModule(BlockBossServer blockBoss) {
		super(blockBoss);
		eventDefinitions.add(new PlayerJoinedEventDefinition());
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
				System.out.println("Triggered event: " + event.id);
				blockBoss.sendMessage(event);
			}
		}
	}
	
}
