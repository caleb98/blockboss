package net.calebscode.blockboss.module.exec.action;

import net.calebscode.blockboss.module.exec.executor.Executor;
import net.calebscode.blockboss.server.BlockBossServer;

public abstract class Action {

	public final String id;
	public final int permissionLevel;

	public Action(String id, int permissionLevel) {
		this.id = id;
		this.permissionLevel = permissionLevel;
	}

	public abstract int execute(BlockBossServer server, Executor executor);

}
