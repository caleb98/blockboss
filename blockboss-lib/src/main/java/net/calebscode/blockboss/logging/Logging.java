package net.calebscode.blockboss.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Logging {

	default Logger logger() {
		return LogManager.getLogger(this.getClass());
	}
	
}
