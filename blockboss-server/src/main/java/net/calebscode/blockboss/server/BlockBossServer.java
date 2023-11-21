package net.calebscode.blockboss.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.calebscode.blockboss.logging.Logging;
import net.calebscode.blockboss.module.BlockBossModule;
import net.calebscode.blockboss.process.MinecraftServer;
import net.calebscode.blockboss.server.event.EventBus;
import net.calebscode.blockboss.server.event.MinecraftServerProcessStartedEvent;

public class BlockBossServer implements Logging {

	private MinecraftServer minecraftServer;
	private ArrayList<BlockBossModule> modules = new ArrayList<>();
	private boolean isShutdownRequested = false;
	
	private Thread eventProcessingThread;
	private EventBus eventBus = new EventBus();
	private Queue<Object> pendingEvents = new ConcurrentLinkedQueue<>();

	public BlockBossServer(File serverJar) {
		minecraftServer = new MinecraftServer(serverJar);
	}

	public boolean addModule(BlockBossModule module) {
		eventBus.register(module);
		return modules.add(module);
	}
	
	public void init() {
		for (var module : modules) {
			module.init();
		}
		
		eventProcessingThread = new Thread(new EventProcessingThread(), "Events");
		eventProcessingThread.start();
	}
	
	public void start() throws IOException {
		isShutdownRequested = false;
		
		for (var module : modules) {
			module.configure(minecraftServer);
		}
		
		minecraftServer.start();
		sendEvent(new MinecraftServerProcessStartedEvent());
	}
	
	public void shutdown() {
		isShutdownRequested = true;
	}
	
	public boolean isShutdownRequested() {
		return isShutdownRequested;
	}
	
	public MinecraftServer getMinecraftServer() {
		return minecraftServer;
	}
	
	public void sendEvent(Object message) {
		pendingEvents.add(message);
	}
	
	private class EventProcessingThread implements Runnable {
		@Override
		public void run() {
			logger().info("Starting {}", Thread.currentThread().getName());
			while (!isShutdownRequested && !Thread.interrupted()) {
				Object message;
				while ((message = pendingEvents.poll()) != null) {
					eventBus.send(message);
				}
			}
			logger().info("Exiting {}", Thread.currentThread().getName());
		}
	}
	
}
