package net.calebscode.blockboss.module.exec;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Group {

	private String name;
	private Set<String> allowedActions = new HashSet<>();
	private Set<String> allowedTasks = new HashSet<>();
	
	public Group(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean removeAllowedAction(String actionId) {
		return allowedActions.remove(actionId);
	}

	public boolean removeAllowedActions(String... actionIds) {
		return allowedActions.removeAll(Arrays.asList(actionIds));
	}
	
	public boolean addAllowedAction(String actionId) {
		return allowedActions.add(actionId);
	}
	
	public boolean addAllowedActions(String... actionIds) {
		return allowedActions.addAll(Arrays.asList(actionIds));
	}
	
	public boolean isActionAllowed(String actionId) {
		return allowedActions.contains(actionId);
	}
	
	public Set<String> getAllowedActions() {
		return Collections.unmodifiableSet(allowedActions);
	}
	
	public boolean removeAllowedTask(String taskId) {
		return allowedTasks.remove(taskId);
	}

	public boolean removeAllowedTasks(String... taskIds) {
		return allowedTasks.removeAll(Arrays.asList(taskIds));
	}
	
	public boolean addAllowedTask(String taskId) {
		return allowedTasks.add(taskId);
	}
	
	public boolean addAllowedTasks(String... taskIds) {
		return allowedTasks.addAll(Arrays.asList(taskIds));
	}
	
	public boolean isTaskAllowed(String taskId) {
		return allowedTasks.contains(taskId);
	}
	
	public Set<String> getAllowedTasks() {
		return Collections.unmodifiableSet(allowedTasks);
	}
	
}
