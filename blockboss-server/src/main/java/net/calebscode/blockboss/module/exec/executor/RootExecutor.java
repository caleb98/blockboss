package net.calebscode.blockboss.module.exec.executor;

import java.util.Collection;
import java.util.Collections;

import net.calebscode.blockboss.module.exec.Group;

public class RootExecutor implements Executor {

	public static final String EXECUTOR_ID = "ROOT";
	public static final Executor ROOT_EXECUTOR = new RootExecutor();

	private RootExecutor() {
		// Singleton
	}

	@Override
	public String getId() {
		return EXECUTOR_ID;
	}

	@Override
	public int getPermissionLevel() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Collection<Group> getGroups() {
		return Collections.emptyList();
	}

	@Override
	public void sendMessage(String message) {
		System.out.println(message);
	}

}
