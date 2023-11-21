package net.calebscode.blockboss.server;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

import net.calebscode.blockboss.logging.Logging;
import net.calebscode.blockboss.module.BlockBossModule;
import net.calebscode.blockboss.process.MinecraftServer;
import net.calebscode.blockboss.server.event.EventBus;
import net.calebscode.blockboss.server.event.MinecraftServerProcessStartedEvent;

public class BlockBossServer implements Logging {

	private MinecraftServer minecraftServer;
	private HashMap<Class<?>, BlockBossModule> modules = new HashMap<>();
	private boolean isInitialized = false;
	private boolean isShutdownRequested = false;

	private Thread eventProcessingThread;
	private EventBus eventBus = new EventBus();
	private Queue<Object> pendingEvents = new ConcurrentLinkedQueue<>();

	public BlockBossServer(File serverJar) {
		minecraftServer = new MinecraftServer(serverJar);
	}

	public void addModule(BlockBossModule module) {
		var moduleClass = module.getClass();
		if (modules.containsKey(moduleClass)) {
			throw new IllegalArgumentException(String.format("Module of type %s has already been added.", moduleClass));
		}

		eventBus.register(module);
		modules.put(moduleClass, module);
	}

	public void init() {
		if (isInitialized) {
			throw new IllegalStateException("BlockBossServer is already initialized.");
		}

		eventProcessingThread = new Thread(new EventProcessingThread(), "Events");
		eventProcessingThread.start();

		for (var module : modules.values()) {
			module.init(minecraftServer);
		}

		isInitialized = true;
	}

	public void start() throws IOException {
		if (minecraftServer.isRunning()) {
			throw new IllegalStateException("Minecraft server is already running.");
		}

		isShutdownRequested = false;
		minecraftServer.start();
		sendEvent(new MinecraftServerProcessStartedEvent());
	}

	public void shutdown() {
		isShutdownRequested = true;
		eventProcessingThread.interrupt();
	}

	public boolean isShutdownRequested() {
		return isShutdownRequested;
	}

	public MinecraftServer getMinecraftServer() {
		return minecraftServer;
	}

	public <T> void addEventListener(Class<T> clazz, Function<T, Boolean> listener) {
		eventBus.addListener(clazz, listener);
	}

	public void sendEvent(Object message) {
		synchronized (pendingEvents) {
			pendingEvents.add(message);
			pendingEvents.notifyAll();
		}
	}

	public boolean isModulePresent(Class<?> moduleType) {
		return modules.containsKey(moduleType);
	}

	@SuppressWarnings("unchecked")
	public <T extends BlockBossModule> T getModule(Class<T> moduleType) {
		if (!modules.containsKey(moduleType)) {
			return null;
		}

		return (T) modules.get(moduleType);
	}

	private class EventProcessingThread implements Runnable {
		@Override
		public void run() {
			logger().info("Starting {}", Thread.currentThread().getName());
			while (!isShutdownRequested && !Thread.interrupted()) {
				synchronized (pendingEvents) {
					try {
						pendingEvents.wait();
						Object message;
						while ((message = pendingEvents.poll()) != null) {
							eventBus.send(message);
						}
					} catch (InterruptedException e) {
						break;
					}
				}
			}
			logger().info("Exiting {}", Thread.currentThread().getName());
		}
	}

}
