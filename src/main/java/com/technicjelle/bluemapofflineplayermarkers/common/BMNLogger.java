package com.technicjelle.bluemapofflineplayermarkers.common;

public class BMNLogger implements Logger {
	private final com.technicjelle.BMUtils.BMNative.BMNLogger logger;

	public BMNLogger(String addonID) {
		logger = new com.technicjelle.BMUtils.BMNative.BMNLogger(addonID);
	}

	@Override
	public void error(String message) {
		logger.logError(message);
	}

	@Override
	public void error(String message, Throwable throwable) {
		logger.logError(message, throwable);
	}

	@Override
	public void warning(String message) {
		logger.logWarning(message);
	}

	@Override
	public void info(String message) {
		logger.logInfo(message);
	}

	@Override
	public void debug(String message) {
		logger.logDebug(message);
	}

	@Override
	public void trace(String message) {
		logger.logDebug(message);
	}
}
