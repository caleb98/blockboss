package net.calebscode.blockboss.module.exec;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

import net.calebscode.blockboss.module.BlockBossModule;
import net.calebscode.blockboss.module.exec.action.Action;
import net.calebscode.blockboss.module.exec.action.SaveWorldAction;
import net.calebscode.blockboss.module.exec.executor.Executor;
import net.calebscode.blockboss.process.MinecraftServer;
import net.calebscode.blockboss.server.BlockBossServer;

public class ExecModule extends BlockBossModule {

	private HashMap<String, Action> actions = new HashMap<>();

	public ExecModule(BlockBossServer blockBoss) {
		super(blockBoss);
		registerAction(new SaveWorldAction());
	}

	@Override
	public Collection<Class<? extends BlockBossModule>> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public void init(MinecraftServer process) {

	}

	public void attemptExecute(Executor executor, String actionId) {
		if (!actions.containsKey(actionId)) {
			executor.sendMessage("Action '%s' does not exist.", actionId);
			return;
		}

		Action action = actions.get(actionId);
		if (!canExecute(executor, action)) {
			executor.sendMessage("You don't have permission for action '%s'.", actionId);
		}

		action.execute(blockBoss, executor);
	}

	private boolean canExecute(Executor executor, Action action) {
		if (executor.getPermissionLevel() >= action.permissionLevel) {
			return true;
		}

		return executor.getGroups().parallelStream()
				.flatMap(group -> group.getAllowedActions().stream())
				.collect(Collectors.toSet())
				.contains(action.id);
	}

	private void registerAction(Action action) {
		if (actions.containsKey(action.id)) {
			logger().error("An action already exists with id {}", action.id);
			return;
		}

		actions.put(action.id, action);
	}

}
