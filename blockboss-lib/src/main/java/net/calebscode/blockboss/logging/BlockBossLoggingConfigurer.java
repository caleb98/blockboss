package net.calebscode.blockboss.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.json.JsonConfigurationFactory;

public class BlockBossLoggingConfigurer {

	public static boolean configureBlockBossLogs() {
		try {
			ConfigurationFactory.setConfigurationFactory(new JsonConfigurationFactory());
			File logConfigFile = new File("blockboss-config/log4j2.json");
			ConfigurationSource source = new ConfigurationSource(new FileInputStream(logConfigFile));
			Configurator.initialize(null, source);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
