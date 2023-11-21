package net.calebscode.blockboss.server;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.calebscode.blockboss.logging.BlockBossLoggingConfigurer;
import net.calebscode.blockboss.module.cli.BlockBossCLIModule;
import net.calebscode.blockboss.module.event.ServerEventModule;
import net.calebscode.blockboss.module.exec.ExecModule;

public class ServerMain {

	private static final Options COMMAND_LINE_OPTIONS = createCommandLineOptions();

	public static void main(String[] args) {
		CommandLine commands = parseCommandLine(args);

		if (commands.hasOption('h') || commands.getArgs().length == 0) {
			printHelpAndExit();
		}

		String serverJar = commands.getArgs()[0];

		if (!serverJar.endsWith(".jar")) {
			serverJar += ".jar";
		}

		validateInputs(serverJar);

		BlockBossLoggingConfigurer.configureBlockBossLogs();

		BlockBossServer server = new BlockBossServer(new File(serverJar));
		server.addModule(new BlockBossCLIModule(server));
		server.addModule(new ServerEventModule(server));
		server.addModule(new ExecModule(server));

		server.init();
	}

	private static void validateInputs(String serverJar) {
		File jar = new File(serverJar);
		if (!jar.exists()) {
			System.out.println("Specified server jar does not exist.");
			printHelpAndExit();
		} else if (!jar.isFile()) {
			System.out.println("Specified server jar is not a file.");
			printHelpAndExit();
		}
	}

	private static void printHelpAndExit() {
		HelpFormatter help = new HelpFormatter();
		help.printHelp("blockboss-server [options...] <server-jar>", COMMAND_LINE_OPTIONS);
		System.exit(0);
	}

	private static CommandLine parseCommandLine(String[] args) {
		try {
			CommandLineParser parser = new DefaultParser();
			return parser.parse(createCommandLineOptions(), args);
		} catch (ParseException ex) {
			System.err.println(ex.getMessage());
			return new CommandLine.Builder().build();
		}
	}

	private static Options createCommandLineOptions() {
		Options options = new Options();

		options.addOption(new Option("h", "help", false, "Prints this help message."));

		return options;
	}

}
