package com.technicjelle.bluemapofflineplayermarkers.common;

import java.util.logging.Level;

public class JavaUtilLogger implements Logger {
	private final java.util.logging.Logger logger;

	public JavaUtilLogger(java.util.logging.Logger logger) {
		this.logger = logger;
	}

	@Override
	public void error(String message) {
		logger.severe(message);
	}

	@Override
	public void error(String message, Throwable throwable) {
		logger.log(Level.SEVERE, message, throwable);
	}

	@Override
	public void warning(String message) {
		logger.warning(message);
	}

	@Override
	public void info(String message) {
		logger.info(message);
	}

	@Override
	public void debug(String message) {
		logger.finer(message);
	}

	@Override
	public void trace(String message) {
		logger.finest(message);
	}
}
