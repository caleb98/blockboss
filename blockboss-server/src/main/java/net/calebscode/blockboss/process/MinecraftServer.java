package net.calebscode.blockboss.process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import net.calebscode.blockboss.logging.Logging;

public class MinecraftServer implements Logging {

	private ProcessWrapper process;

	private ArrayList<Consumer<String>> stdoutListeners = new ArrayList<>();
	private ArrayList<Consumer<String>> stderrListeners = new ArrayList<>();
	private ConcurrentLinkedQueue<String> inputs = new ConcurrentLinkedQueue<>();

	public MinecraftServer(File serverJar, String... arguments) {
		File jarDirectory = serverJar.getParentFile();
		String jarName = serverJar.getName();
		process = new ProcessWrapper(jarDirectory, "java", "-jar", jarName);
		process.addArguments(arguments);
	}

	public void addStdoutListener(Consumer<String> listener) {
		stdoutListeners.add(listener);
	}

	public void addStderrListener(Consumer<String> listener) {
		stderrListeners.add(listener);
	}

	public void sendInput(String input) {
		inputs.add(input);
	}

	public void start() throws IOException {
		inputs.clear();

		logger().info("Starting minecraft server process in '{}' with command: {}",
				process.getWorkingDir().getParentFile().getAbsolutePath(),
				process.getCommand() + " " + String.join(" ", process.getArguments()));

		process.start();

		logger().info("Creating Minecraft-Process-Stream-Monitor thread.");
		new Thread(this::monitorProcessStreams, "Server").start();
	}

	public boolean isRunning() {
		return process.isRunning();
	}

	public ProcessWrapper getProcess() {
		return process;
	}

	private void monitorProcessStreams() {
		try {
			while (isRunning()) {
				callStdoutListeners();
				callStderrListeners();
				sendInputs();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void sendInputs() throws IOException {
		while (!inputs.isEmpty()) {
			String input = inputs.poll();
			process.getStdin().write(input + "\n");
		}
		process.getStdin().flush();
	}

	private void callStderrListeners() throws IOException {
		String line;
		while (process.getStderr().ready() && (line = process.getStderr().readLine()) != null) {
			for (var listener : stderrListeners) {
				listener.accept(line);
			}
		}
	}

	private void callStdoutListeners() throws IOException {
		String line;
		while (process.getStdout().ready() && (line = process.getStdout().readLine()) != null) {
			for (var listener : stdoutListeners) {
				listener.accept(line);
			}
		}
	}

}
