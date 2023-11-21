package net.calebscode.blockboss.module;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import net.calebscode.blockboss.process.MinecraftServer;
import net.calebscode.blockboss.server.BlockBossServer;

@ExtendWith(MockitoExtension.class)
public class BlockBossModuleTest {

	@Test
	void getModuleId() {
		var module = new TestModule(null);

		assertEquals("TestModule", module.getModuleId());
	}

	class TestModule extends BlockBossModule {

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

	}

}
