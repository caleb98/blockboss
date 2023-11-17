package net.calebscode.blockboss.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.calebscode.blockboss.logging.Logging;
import net.calebscode.blockboss.module.BlockBossModule;
import net.calebscode.blockboss.server.process.MinecraftServer;

public class BlockBossServer implements Logging {

	private MinecraftServer minecraftServer;
	private ArrayList<BlockBossModule> modules = new ArrayList<>();
	private boolean isShutdownRequested = false;
	
	private Thread messageProcessingThread;
	private MessageBus messageBus = new MessageBus();
	private Queue<Object> pendingMessages = new ConcurrentLinkedQueue<>();

	public BlockBossServer(String serverDirectory, String... serverCommand) {
		minecraftServer = new MinecraftServer(new File(serverDirectory), serverCommand);
	}

	public boolean addModule(BlockBossModule module) {
		messageBus.register(module);
		return modules.add(module);
	}
	
	public void init() {
		for (var module : modules) {
			module.init();
		}
		
		messageProcessingThread = new Thread(new MessageProcessingThread(), "BlockBoss-MPT");
		messageProcessingThread.start();
	}
	
	public void start() throws IOException {
		isShutdownRequested = false;
		
		for (var module : modules) {
			module.configure(minecraftServer);
		}
		
		minecraftServer.start();
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
	
	public void sendMessage(Object message) {
		pendingMessages.add(message);
	}
	
	private class MessageProcessingThread implements Runnable {
		@Override
		public void run() {
			logger().info("Starting {}", Thread.currentThread().getName());
			while (!isShutdownRequested && !Thread.interrupted()) {
				Object message;
				while ((message = pendingMessages.poll()) != null) {
					messageBus.send(message);
				}
			}
			logger().info("Exiting {}", Thread.currentThread().getName());
		}
	}
	
}
