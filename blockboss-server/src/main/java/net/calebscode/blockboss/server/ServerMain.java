package net.calebscode.blockboss.server;

import net.calebscode.blockboss.logging.BlockBossLoggingConfigurer;
import net.calebscode.blockboss.module.cli.BlockBossCLIModule;
import net.calebscode.blockboss.module.event.ServerEventModule;

public class ServerMain {

	public static void main(String[] args) {		
		BlockBossLoggingConfigurer.configureBlockBossLogs();
		
		BlockBossServer server = new BlockBossServer("C:\\Users\\caleb\\Desktop\\mcserver", "java", "-jar", "server.jar");		
		server.addModule(new BlockBossCLIModule(server));
		server.addModule(new ServerEventModule(server));
		
		server.init();
	}
	
}
