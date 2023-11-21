package net.calebscode.blockboss.process;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.core.util.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProcessWrapperTest {

	ProcessWrapper process;
	
	@Mock
	Process wrapped;
	InputStream stdout;
	InputStream stderr;
	ByteArrayOutputStream stdin;
	boolean isProcessAlive;
	
	@BeforeEach
	void init() throws IOException {
		process = spy(new ProcessWrapper(new File("/path/to/working/dir"), "command"));
		isProcessAlive = false;
	}
	
	@Test
	void constructor() {
		assertEquals(new File("/path/to/working/dir"), process.getWorkingDir());
		assertEquals("command", process.getCommand());
	}
	
	@Test
	void constructorWithProcessArguments() {
		process = new ProcessWrapper(new File("/path/to/working/dir"), "command", "arg1", "arg2", "arg3");
		
		var args = process.getArguments();
		assertEquals(3, args.size());
		assertEquals("arg1", args.get(0));
		assertEquals("arg2", args.get(1));
		assertEquals("arg3", args.get(2));
	}
	
	@Test
	void addArguments() {
		process.addArguments("arg1");
		process.addArguments("arg2", "arg3");
		
		var args = process.getArguments();
		assertEquals(3, args.size());
		assertEquals("arg1", args.get(0));
		assertEquals("arg2", args.get(1));
		assertEquals("arg3", args.get(2));
	}
	
	@Test
	void start() throws IOException {
		configureWrappedProcess();
		
		process.start();
		
		process.getStdin().write("stdin");
		process.getStdin().flush();
		
		assertTrue(process.isRunning());
		assertEquals("stdout", IOUtils.toString(process.getStdout()));
		assertEquals("stderr", IOUtils.toString(process.getStderr()));
		assertEquals("stdin", new String(stdin.toByteArray(), StandardCharsets.UTF_8));
	}
	
	@Test
	void startThrowsIfAlreadyRunning() {
		when(process.isRunning()).thenReturn(true);
		
		assertThrows(IllegalStateException.class, () -> {
			process.start();
		});
	}
	
	@Test
	void stop() throws IOException, InterruptedException {
		configureWrappedProcess();
		doAnswer(i -> {
			isProcessAlive = false;
			return null;
		}).when(wrapped).destroy();
		when(wrapped.waitFor()).thenReturn(0);
		
		process.start();
		int exitCode = process.stop();
		
		verify(wrapped).destroy();
		assertEquals(0, exitCode);
		assertFalse(process.isRunning());
		assertThrows(IOException.class, () -> { process.getStdout().read(); });
		assertThrows(IOException.class, () -> { process.getStderr().read(); });
		assertThrows(IOException.class, () -> { process.getStdin().write("blah"); });
	}
	
	@Test
	void stopThrowsWhenNotRunning() {
		assertThrows(IllegalStateException.class, () -> {
			process.stop();
		});
	}
	
	private void configureWrappedProcess() throws IOException {
		stdout = new ByteArrayInputStream("stdout".getBytes());
		stderr = new ByteArrayInputStream("stderr".getBytes());
		stdin = new ByteArrayOutputStream();
		
		when(wrapped.isAlive()).thenAnswer(i -> isProcessAlive);
		when(wrapped.getInputStream()).thenReturn(stdout);
		when(wrapped.getErrorStream()).thenReturn(stderr);
		when(wrapped.getOutputStream()).thenReturn(stdin);
		
		doAnswer(i -> {
			isProcessAlive = true;
			return wrapped;
		}).when(process).createProcess();
	}
	
}
