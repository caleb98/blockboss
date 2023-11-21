package net.calebscode.blockboss.module.exec.action;

import net.calebscode.blockboss.module.event.WorldSavedEvent;
import net.calebscode.blockboss.module.exec.executor.Executor;
import net.calebscode.blockboss.server.BlockBossServer;

public class SaveWorldAction extends Action {

	public static final String ID = "SaveWorld";
	public static final int PERMISSION_LEVEL = 400;

	public SaveWorldAction() {
		super(ID, PERMISSION_LEVEL);
	}

	@Override
	public int execute(BlockBossServer server, Executor executor) {
		if (!server.getMinecraftServer().isRunning()) {
			executor.sendMessage("Unable to save world: Minecraft server is not running.");
			return -1;
		}

		executor.sendMessage("Saving world...");
		server.addEventListener(WorldSavedEvent.class, (event) -> {
			executor.sendMessage("World saved!");
			return true;
		});

		server.getMinecraftServer().sendInput("save-all");

		return 0;
	}

}
