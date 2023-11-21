package net.calebscode.blockboss.module;

import java.util.Collection;

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
	 * @return a collection of module types which this module depends on
	 */
	public abstract Collection<Class<? extends BlockBossModule>> getDependencies();

	/**
	 * Initializes the module. Called once at the start of the application.
	 */
	public abstract void init(MinecraftServer server);

}
