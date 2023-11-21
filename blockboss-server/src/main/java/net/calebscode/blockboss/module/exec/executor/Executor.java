package net.calebscode.blockboss.module.exec.executor;

import java.util.Collection;

import net.calebscode.blockboss.module.exec.Group;

public interface Executor {

	public String getId();

	public int getPermissionLevel();

	public Collection<Group> getGroups();

	public void sendMessage(String message);

	public default void sendMessage(String format, Object... args) {
		sendMessage(String.format(format, args));
	}

}
