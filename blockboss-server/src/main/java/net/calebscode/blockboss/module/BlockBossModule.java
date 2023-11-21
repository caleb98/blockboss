package net.calebscode.blockboss.module;

import net.calebscode.blockboss.logging.Logging;
import net.calebscode.blockboss.process.MinecraftServer;
import net.calebscode.blockboss.server.BlockBossServer;

public abstract class BlockBossModule implements Logging {

	protected BlockBossServer blockBoss;
	
	public BlockBossModule(BlockBossServer blockBoss) {
		this.blockBoss = blockBoss;
	}
	
	public final String getModuleId() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * Initializes the module. Called once at the start of the application.
	 */
	public abstract void init();
	
	/**
	 * Configures the provided MinecraftServer object.
	 * @param process
	 */
	public abstract void configure(MinecraftServer process);
	
}
