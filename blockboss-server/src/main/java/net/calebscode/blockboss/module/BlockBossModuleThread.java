package net.calebscode.blockboss.module;

import net.calebscode.blockboss.logging.Logging;
import net.calebscode.blockboss.server.BlockBossServer;

public class BlockBossModuleThread implements Runnable, Logging {

	protected BlockBossServer blockBoss;
	protected BlockBossModule owner;
	
	public BlockBossModuleThread(BlockBossServer blockBoss, BlockBossModule owner) {
		this.blockBoss = blockBoss;
		this.owner = owner;
	}
	
	@Override
	public void run() {
		logger().info("Starting {}", Thread.currentThread().getName());
		init();
		while (!blockBoss.isShutdownRequested()) {
			doUpdate();
		}
		cleanup();
		logger().info("Exiting {}", Thread.currentThread().getName());
	}
	
	public void init() {}
	public void doUpdate() {}
	public void cleanup() {}

}
