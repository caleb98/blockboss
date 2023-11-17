package net.calebscode.blockboss.server.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import net.calebscode.blockboss.logging.Logging;

public class MinecraftServer implements Logging {
	
	private File serverDirectory;
	private String[] arguments;
	
	private Process serverProcess;
	private BufferedReader stdout;
	private BufferedReader stderr;
	private BufferedWriter stdin;
	
	private ArrayList<Consumer<String>> stdoutListeners = new ArrayList<>();
	private ArrayList<Consumer<String>> stderrListeners = new ArrayList<>();
	
	private ConcurrentLinkedQueue<String> inputs = new ConcurrentLinkedQueue<>();
	
	public MinecraftServer(File serverDirectory, String... arguments) {
		this.serverDirectory = serverDirectory;
		this.arguments = arguments;
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
		if (isRunning()) {
			throw new IllegalStateException("Cannot start MinecraftServerProcess that is already running.");
		}
		
		inputs.clear();
		
		logger().info("Starting minecraft server process in '{}' with command: {}", serverDirectory.getAbsolutePath(), String.join(" ", arguments));
		
		ProcessBuilder builder = new ProcessBuilder(arguments);
		builder.directory(serverDirectory);
		serverProcess = builder.start();
		
		InputStream stdoutStream = serverProcess.getInputStream();
		InputStream stderrStream = serverProcess.getErrorStream();
		OutputStream stdinStream = serverProcess.getOutputStream();
		
		stdout = new BufferedReader(new InputStreamReader(stdoutStream));
		stderr = new BufferedReader(new InputStreamReader(stderrStream));
		stdin = new BufferedWriter(new OutputStreamWriter(stdinStream));

		logger().info("Creating Minecraft-Process-Stream-Monitor thread.");
		new Thread(this::monitorProcessStreams, "Server").start();
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
			stdin.write(input + "\n");
		}
		stdin.flush();
	}

	private void callStderrListeners() throws IOException {
		String line;
		while (stderr.ready() && (line = stderr.readLine()) != null) {
			for (var listener : stderrListeners) {
				listener.accept(line);
			}
		}
	}

	private void callStdoutListeners() throws IOException {
		String line;
		while (stdout.ready() && (line = stdout.readLine()) != null) {
			for (var listener : stdoutListeners) {
				listener.accept(line);
			}
		}
	}
	
	public boolean isRunning() {
		return serverProcess != null && serverProcess.isAlive();
	}

}
