package com.technicjelle.bluemapofflineplayermarkers.common;

public interface Logger {
	void error(String message);
	void error(String message, Throwable throwable);
	void warning(String message);
	void info(String message);
	void debug(String message);
	void trace(String message);
}
