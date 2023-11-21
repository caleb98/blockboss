package net.calebscode.blockboss.module.cli;

import java.io.IOException;
import java.util.Scanner;

import net.calebscode.blockboss.module.BlockBossModule;
import net.calebscode.blockboss.module.BlockBossModuleThread;
import net.calebscode.blockboss.module.exec.ExecModule;
import net.calebscode.blockboss.module.exec.executor.RootExecutor;
import net.calebscode.blockboss.server.BlockBossServer;

public class BlockBossCLIThread extends BlockBossModuleThread {

	private Scanner input;

	public BlockBossCLIThread(BlockBossServer blockBoss, BlockBossModule owner) {
		super(blockBoss, owner);
		input = new Scanner(System.in);
	}

	@Override
	public void doUpdate() {
		if (input.hasNextLine()) {
			String line = input.nextLine();

			if (line.startsWith("bb ")) {
				handleBlockBossCommand(line.substring(3));
			} else if (blockBoss.getMinecraftServer().isRunning()) {
				blockBoss.getMinecraftServer().sendInput(line);
			}
		}
	}

	private void handleBlockBossCommand(String blockBossCommand) {
		var fullCommand = blockBossCommand.split(" ");

		if (fullCommand.length == 0) {
			System.out.println("Please provide a command to run.");
			return;
		}

		switch (fullCommand[0]) {

		case "serve":
			if (!blockBoss.getMinecraftServer().isRunning()) {
				try {
					blockBoss.start();
				} catch (IOException ex) {
					System.err.printf("Unable to start Minecraft server: %s\n", ex.getMessage());
				}
			} else {
				System.out.println("Minecraft server is already running.");
			}
			break;

		case "exit":
			if (blockBoss.getMinecraftServer().isRunning()) {
				blockBoss.getMinecraftServer().sendInput("stop");
			}
			blockBoss.shutdown();
			break;

		case "action":
			if (fullCommand.length != 2) {
				System.out.println("Usage: bb action [actionId]");
				return;
			}

			if (!blockBoss.isModulePresent(ExecModule.class)) {
				System.out.println("That command requires the ExecModule to be present.");
				return;
			}

			var execModule = blockBoss.getModule(ExecModule.class);
			execModule.attemptExecute(RootExecutor.ROOT_EXECUTOR, fullCommand[1]);
			break;

		default:
			System.out.printf("Unrecognized BlockBoss command: %s\n", blockBossCommand);
			break;

		}
	}

}
