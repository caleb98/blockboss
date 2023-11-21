package net.calebscode.blockboss.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import net.calebscode.blockboss.module.BlockBossModule;
import net.calebscode.blockboss.process.MinecraftServer;
import net.calebscode.blockboss.server.event.EventBus;
import net.calebscode.blockboss.server.event.MinecraftServerProcessStartedEvent;
import net.calebscode.blockboss.server.event.Subscribe;

@ExtendWith(MockitoExtension.class)
public class BlockBossServerTest {

	@Spy
	EventBus eventBus;

	@Mock
	MinecraftServer server;

	@Mock
	BlockBossModule module;

	@InjectMocks
	BlockBossServer blockBoss = new BlockBossServer(new File("/path/to/server.jar"));

	@BeforeAll
	static void loadLogger() {
		// Prevents some logging errors from being output to the console when the tests
		// are run by initializing the logging subsystem.
		LogManager.getRootLogger();
	}

	@Test
	void addModule() {
		blockBoss.addModule(module);

		verify(eventBus).register(module);
		assertTrue(blockBoss.isModulePresent(module.getClass()));
		assertEquals(module, blockBoss.getModule(module.getClass()));
	}

	@Test
	void addModuleThrowsWhenModuleClassPresent() {
		blockBoss.addModule(module);

		assertThrows(IllegalArgumentException.class, () -> {
			blockBoss.addModule(module);
		});
	}

	@Test
	void init() {
		blockBoss.addModule(module);

		blockBoss.init();

		verify(module).init(blockBoss.getMinecraftServer());
		assertTrue(blockBoss.getEventProcessingThread().isAlive());

		blockBoss.shutdown();
	}

	@Test
	void initThrowsIfAlreadyInitialized() {
		blockBoss.init();

		assertThrows(IllegalStateException.class, blockBoss::init);

		blockBoss.shutdown();
	}

	@Test
	void start() throws IOException, InterruptedException {
		var module = mock(TestModule.class);

		blockBoss.addModule(module);
		blockBoss.init();
		blockBoss.start();

		assertFalse(blockBoss.isShutdownRequested());

		blockBoss.shutdown();
		blockBoss.getEventProcessingThread().join();

		verify(server).start();
		verify(module).serverStarted(any(MinecraftServerProcessStartedEvent.class));

		blockBoss.shutdown();
	}

	@Test
	void startThrowsWhenMinecraftServerAlreadyRunning() {
		when(server.isRunning()).thenReturn(true);
		
		assertThrows(IllegalStateException.class, blockBoss::start);
	}

	@Test
	void shutdown() throws InterruptedException {
		blockBoss.init();
		blockBoss.shutdown();

		blockBoss.getEventProcessingThread().join();

		assertTrue(!blockBoss.getEventProcessingThread().isAlive());
	}

	@Test
	void sendEvent() throws InterruptedException {
		blockBoss.init();
		Function<Object, Boolean> listenerRemoved = spy(new Function<Object, Boolean>() {
			@Override
			public Boolean apply(Object t) {
				return true;
			}
		});
		Function<Object, Boolean> listenerRemained = spy(new Function<Object, Boolean>() {
			@Override
			public Boolean apply(Object t) {
				return false;
			}
		});
		var message = new Object();

		blockBoss.addEventListener(Object.class, listenerRemoved);
		blockBoss.addEventListener(Object.class, listenerRemained);

		blockBoss.sendEvent(message);
		blockBoss.sendEvent(message);
		blockBoss.shutdown();

		blockBoss.getEventProcessingThread().join();

		verify(listenerRemoved, times(1)).apply(message);
		verify(listenerRemained, times(2)).apply(message);

		blockBoss.shutdown();
	}

	@Test
	void isModulePresent() {
		blockBoss.addModule(module);

		assertTrue(blockBoss.isModulePresent(module.getClass()));
		assertFalse(blockBoss.isModulePresent(TestModule.class));
	}

	@Test
	void getModule() {
		blockBoss.addModule(module);

		assertEquals(module, blockBoss.getModule(module.getClass()));
		assertNull(blockBoss.getModule(TestModule.class));
	}

	public class TestModule extends BlockBossModule {

		public TestModule(BlockBossServer blockBoss) {
			super(blockBoss);
		}

		@Override
		public Collection<Class<? extends BlockBossModule>> getDependencies() {
			return Collections.emptyList();
		}

		@Override
		public void init(MinecraftServer server) {

		}

		@Subscribe
		public void serverStarted(MinecraftServerProcessStartedEvent event) {

		}

	}

}
