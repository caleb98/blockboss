package net.calebscode.blockboss.module.cli;

import java.io.IOException;
import java.util.Scanner;

import net.calebscode.blockboss.module.BlockBossModule;
import net.calebscode.blockboss.module.BlockBossModuleThread;
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
				line = handleBlockBossCommand(line);
			}
			else if (blockBoss.getMinecraftServer().isRunning()) {
				blockBoss.getMinecraftServer().sendInput(line);
			}
		}
	}
	
	private String handleBlockBossCommand(String blockBossCommand) {
		blockBossCommand = blockBossCommand.substring(3);
		switch (blockBossCommand) {
		
		case "serve":
			if (!blockBoss.getMinecraftServer().isRunning()) {
				try {
					blockBoss.start();
				} catch (IOException ex) {
					System.err.printf("Unable to start Minecraft server: %s\n",  ex.getMessage());
				}
			}
			else {
				System.out.println("Minecraft server is already running.");
			}
			break;
			
		case "exit":
			blockBoss.shutdown();
			break;
		
		default:
			System.out.printf("Unrecognized BlockBoss command: %s\n", blockBossCommand);
			break;
			
		}
		return blockBossCommand;
	}

}
