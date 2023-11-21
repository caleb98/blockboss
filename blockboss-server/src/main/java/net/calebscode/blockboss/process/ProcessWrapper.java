package net.calebscode.blockboss.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.calebscode.blockboss.test.VisibleForTesting;

public class ProcessWrapper {

	private File workingDir;
	private String command;
	private List<String> arguments;

	private Process process;
	private BufferedReader stdout;
	private BufferedReader stderr;
	private BufferedWriter stdin;

	public ProcessWrapper(File workingDir, String command) {
		this.workingDir = workingDir;
		this.command = command;
		this.arguments = new ArrayList<>();
	}

	public ProcessWrapper(File workingDir, String command, String... arguments) {
		this.workingDir = workingDir;
		this.command = command;
		this.arguments = new ArrayList<>(Arrays.asList(arguments));
	}

	public String getCommand() {
		return command;
	}

	public File getWorkingDir() {
		return workingDir;
	}

	public void addArguments(String... arguments) {
		this.arguments.addAll(Arrays.asList(arguments));
	}

	public List<String> getArguments() {
		return Collections.unmodifiableList(arguments);
	}

	public void start() throws IOException {
		if (isRunning()) {
			throw new IllegalStateException("Cannot start process that is already running.");
		}

		closeStreams();

		process = createProcess();

		stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
		stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		stdin = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
	}

	/**
	 * Stops the wrapped process.
	 *
	 * @return exit code of the terminated process
	 *
	 * @throws InterruptedException
	 */
	public int stop() throws InterruptedException {
		if (!isRunning()) {
			throw new IllegalStateException("Cannot stop a process that is not running.");
		}

		process.destroy();
		int exitCode = process.waitFor();

		closeStreams();

		return exitCode;
	}

	public BufferedReader getStdout() {
		return stdout;
	}

	public BufferedReader getStderr() {
		return stderr;
	}

	public BufferedWriter getStdin() {
		return stdin;
	}

	public boolean isRunning() {
		return process != null && process.isAlive();
	}

	private void closeStreams() {
		try {
			if (stdout != null) {
				stdout.close();
			}
			if (stderr != null) {
				stderr.close();
			}
			if (stdin != null) {
				stdin.close();
			}
		} catch (IOException ex) {
			// Ignore
		}
	}

	@VisibleForTesting
	protected Process createProcess() throws IOException {
		var commandWithArgs = new ArrayList<String>(arguments.size() + 1);
		commandWithArgs.add(command);
		commandWithArgs.addAll(arguments);

		ProcessBuilder builder = new ProcessBuilder(commandWithArgs);
		builder.directory(workingDir);
		return builder.start();
	}

}
