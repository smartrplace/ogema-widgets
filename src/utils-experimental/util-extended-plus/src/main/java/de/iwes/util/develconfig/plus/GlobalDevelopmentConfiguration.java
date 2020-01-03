package de.iwes.util.develconfig.plus;

import org.ogema.core.logging.OgemaLogger;

/**Overwrite this to provide a custom logger configuration. Note that standard SLF4J and {@link OgemaLogger}s
 * cannot be configured here, but only {@link DevelopmentLogger}s*/
public class GlobalDevelopmentConfiguration {
	public static String shortId = "DefaultGC";
	public DevelopmentLoggerConfiguration getLoggerConfiguration(String loggerId) {
		return new DevelopmentLoggerConfiguration();
	}
}
