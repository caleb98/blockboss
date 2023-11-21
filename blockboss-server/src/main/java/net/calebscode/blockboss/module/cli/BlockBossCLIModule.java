package net.calebscode.blockboss.module.cli;

import java.util.Collection;
import java.util.Collections;

import net.calebscode.blockboss.module.BlockBossModule;
import net.calebscode.blockboss.process.MinecraftServer;
import net.calebscode.blockboss.server.BlockBossServer;

public class BlockBossCLIModule extends BlockBossModule {

	private Thread blockBossCLIThread;

	public BlockBossCLIModule(BlockBossServer blockBoss) {
		super(blockBoss);
	}

	@Override
	public Collection<Class<? extends BlockBossModule>> getDependencies() {
		return Collections.emptyList();
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
