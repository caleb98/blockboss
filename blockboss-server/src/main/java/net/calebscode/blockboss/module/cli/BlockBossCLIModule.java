package net.calebscode.blockboss.module.cli;

import net.calebscode.blockboss.module.BlockBossModule;
import net.calebscode.blockboss.module.event.PlayerJoinedEvent;
import net.calebscode.blockboss.process.MinecraftServer;
import net.calebscode.blockboss.server.BlockBossServer;
import net.calebscode.blockboss.server.event.Subscribe;

public class BlockBossCLIModule extends BlockBossModule {

	private Thread blockBossCLIThread;
	
	public BlockBossCLIModule(BlockBossServer blockBoss) {
		super(blockBoss);
	}
	
	@Override
	public void init() {
		blockBossCLIThread = new Thread(new BlockBossCLIThread(blockBoss, this), "CLI");
		blockBossCLIThread.start();
	}
	
	@Override
	public void configure(MinecraftServer process) {
		process.addStdoutListener(System.out::println);
		process.addStderrListener(System.err::println);
	}
	
}
