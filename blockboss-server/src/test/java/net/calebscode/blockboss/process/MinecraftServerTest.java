package net.calebscode.blockboss.process;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.logging.log4j.core.util.StringBuilderWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MinecraftServerTest {
	
	MinecraftServer server;
	boolean isRunning;
	
	@Test
	void constructor() {
		server = new MinecraftServer(new File("/path/to/server.jar"), "-Xmx6G", "-Xms1G");
		
		var process = server.getProcess();
		var args = process.getArguments();
		
		assertEquals(new File("/path/to/"), process.getWorkingDir());
		assertEquals("java", process.getCommand());
		assertEquals(List.of("-jar", "server.jar", "-Xmx6G", "-Xms1G"), args);
	}
	
	@Test
	void start() throws IllegalArgumentException, IllegalAccessException, IOException {
		server = new MinecraftServer(new File("/path/to/server.jar"));
		var process = mockProcess(server);
		
		server.start();
		
		verify(process).start();
	}
	
	@Test
	void sendInputs() throws IllegalArgumentException, IllegalAccessException, IOException, InterruptedException {
		isRunning = true;
		server = new MinecraftServer(new File("/path/to/server.jar"));
		
		var stringBuilder = new StringBuilder();
		var process = mockProcess(server);
		when(process.getStdin()).thenReturn(new BufferedWriter(new StringBuilderWriter(stringBuilder)));
		when(process.getStdout()).thenReturn(new BufferedReader(InputStreamReader.nullReader()));
		when(process.getStderr()).thenReturn(new BufferedReader(InputStreamReader.nullReader()));
		when(process.isRunning()).then(i -> isRunning);
		
		server.start();
		server.sendInput("hello");
		
		Thread.sleep(250);
		assertEquals("hello\n", stringBuilder.toString());
		isRunning = false;
	}
	
	@Test
	void addStdoutListener() throws IllegalArgumentException, IllegalAccessException, IOException, InterruptedException {
		isRunning = true;
		server = new MinecraftServer(new File("/path/to/server.jar"));
		
		var stringBuilder = new StringBuilder();
		var process = mockProcess(server);
		when(process.getStdin()).thenReturn(new BufferedWriter(OutputStreamWriter.nullWriter()));
		when(process.getStdout()).thenReturn(new BufferedReader(new StringReader("stdout")));
		when(process.getStderr()).thenReturn(new BufferedReader(InputStreamReader.nullReader()));
		when(process.isRunning()).then(i -> isRunning);
		
		server.addStdoutListener(stdoutData -> stringBuilder.append(stdoutData));
		server.start();
		
		Thread.sleep(250);
		assertEquals("stdout", stringBuilder.toString());
		isRunning = false;
	}
	
	@Test
	void addStderrListener() throws IllegalArgumentException, IllegalAccessException, IOException, InterruptedException {
		isRunning = true;
		server = new MinecraftServer(new File("/path/to/server.jar"));
		
		var stringBuilder = new StringBuilder();
		var process = mockProcess(server);
		when(process.getStdin()).thenReturn(new BufferedWriter(OutputStreamWriter.nullWriter()));
		when(process.getStdout()).thenReturn(new BufferedReader(InputStreamReader.nullReader()));
		when(process.getStderr()).thenReturn(new BufferedReader(new StringReader("stderr")));
		when(process.isRunning()).then(i -> isRunning);
		
		server.addStderrListener(stdoutData -> stringBuilder.append(stdoutData));
		server.start();
		
		Thread.sleep(250);
		assertEquals("stderr", stringBuilder.toString());
		isRunning = false;
	}
	
	private ProcessWrapper mockProcess(MinecraftServer server) throws IllegalArgumentException, IllegalAccessException {
		var process = mock(ProcessWrapper.class);
		when(process.getWorkingDir()).thenReturn(server.getProcess().getWorkingDir());
		when(process.getCommand()).thenReturn(server.getProcess().getCommand());
		when(process.getArguments()).thenReturn(server.getProcess().getArguments());
		
		Field processField = ReflectionUtils.findFields(
				MinecraftServer.class, 
				f -> f.getName().equals("process"), 
				ReflectionUtils.HierarchyTraversalMode.TOP_DOWN
			).get(0);
		
		processField.setAccessible(true);
		processField.set(server, process);
		
		return process;
	}
	
}
