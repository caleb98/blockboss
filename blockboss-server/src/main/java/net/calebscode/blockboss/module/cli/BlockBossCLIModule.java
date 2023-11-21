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
	public void init(MinecraftServer server) {
		blockBossCLIThread = new Thread(new BlockBossCLIThread(blockBoss, this), "CLI");
		blockBossCLIThread.start();

		server.addStdoutListener(System.out::println);
		server.addStderrListener(System.err::println);
	}

}
