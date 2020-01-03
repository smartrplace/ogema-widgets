package de.iwes.util.develconfig.plus;

import org.ogema.core.logging.LogLevel;

public class DevelopmentLoggerConfiguration {
	public String id = "default";
	/**If true the short id will be added to the logger string even though the logger
	 * will add its own longer id information also
	 */
	public boolean useShortIdAlsoForLogger = false;
	/**null means no output*/
	public LogLevel minimumDirectConsoleLevel = LogLevel.WARNING;
	public LogLevel minimumLoggerLevel = LogLevel.TRACE;
	public enum TimePrinting {
		NONE,
		HOUR_TO_SECOND,
		HOUR_TO_MILLISECOND,
		YEAR_TO_SECOND,
		FULL_DATE
	}
	public TimePrinting directToConsoleTimePrinting = TimePrinting.NONE;
	/**If not null will be used instead of directToConsoleTimePrinting*/
	public String directToConsoleTimeTimeFormat = null;
	public boolean printMessageCounter = false;
	/**If true a message may be printed to the console directly, but also via the logger if the logger is
	 * configured like this
	 */
	public boolean sendToLoggerIfPrintedToConsole = true;
}
