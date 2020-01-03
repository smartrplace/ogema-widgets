package de.iwes.util.develconfig.plus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.logging.LogLevel;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.slf4j.Logger;

import de.iwes.util.develconfig.plus.DevelopmentLoggerConfiguration.TimePrinting;
import de.iwes.util.format.StringFormatHelper;

/** Logger that is specifically designed for very specific configuration programmtically, most
 * relevant for algorithm development. In contrast to the standard {@link OgemaLogger} this
 * logger can be configured via a {@link GlobalDevelopmentConfiguration} and the app can 
 * set the log levels itself. Normal productive apps should not do that, but for algorithm
 * development this can be very helpful to enable stronger logging for a single code partition and
 * go back to normal logging later on.
 *
 */
public class DevelopmentLogger {
	protected final OgemaLogger ogemaLogger;
	protected final Logger slf4jLogger;
	
	protected DevelopmentLoggerConfiguration config;
	protected String globalConfigId = null;
	protected final String shortId;
	protected boolean minimumConsoleLevelSetInApp = false;
	protected boolean minimumLoggerLevelSetInApp = false;
	protected boolean timeFormatSetInApp = false;
	
	protected boolean logTofile = true;
	protected boolean sytemOutToConsole = false;
	
	public int counter = 0;
	
	public DevelopmentLogger(String shortId, OgemaLogger ogemaLogger) {
		this.ogemaLogger = ogemaLogger;
		this.slf4jLogger = null;
		if(shortId == null)
			this.config = getStandardLogConfig();
		else
			this.config = GlobalDevelopmentProvider.getConfig().getLoggerConfiguration(shortId);
		this.shortId = shortId;
		setTimeFormat();
	}
	public DevelopmentLogger(String shortId, Logger slf4jLogger) {
		this.ogemaLogger = null;
		this.slf4jLogger = slf4jLogger;
		if(shortId == null)
			this.config = getStandardLogConfig();
		else
			this.config = GlobalDevelopmentProvider.getConfig().getLoggerConfiguration(shortId);
		this.shortId = shortId;
		setTimeFormat();
	}
	/*public DevelopmentLogger(String loggerName) {
		this.ogemaLogger = null;
		this.slf4jLogger = LoggerFactory.getLogger(loggerName);
		this.config = getStandardLogConfig();
		setTimeFormat();
	}*/
	public DevelopmentLogger(String shortId) {
		this.ogemaLogger = null;
		this.slf4jLogger = null;
		this.shortId = shortId;
		this.config = GlobalDevelopmentProvider.getConfig().getLoggerConfiguration(shortId);
		setTimeFormat();
	}
	
	public void error(String message) {
		boolean sendToLogger = directPrintToConsole(message, LogLevel.ERROR, -1)||config.sendToLoggerIfPrintedToConsole;
		if(sendToLogger&&isLogLevelToPrint(LogLevel.ERROR, config.minimumLoggerLevel) && (getLogger()!=null)) getLogger().error(message);
	}
	public void error(String message, Object... o) {
		boolean sendToLogger = directPrintToConsole(message, LogLevel.ERROR, -1, o)||config.sendToLoggerIfPrintedToConsole;
		if(sendToLogger&&isLogLevelToPrint(LogLevel.ERROR, config.minimumLoggerLevel) && (getLogger()!=null)) getLogger().error(message, o);
	}
	public void warn(String message) {
		boolean sendToLogger = directPrintToConsole(message, LogLevel.WARNING, -1)||config.sendToLoggerIfPrintedToConsole;
		if(sendToLogger&&isLogLevelToPrint(LogLevel.WARNING, config.minimumLoggerLevel) && (getLogger()!=null)) getLogger().warn(message);
	}
	public void warn(String message, Object... o) {
		boolean sendToLogger = directPrintToConsole(message, LogLevel.WARNING, -1, o)||config.sendToLoggerIfPrintedToConsole;
		if(sendToLogger&&isLogLevelToPrint(LogLevel.WARNING, config.minimumLoggerLevel) && (getLogger()!=null)) getLogger().warn(message, o);
	}
	public void info(String message) {
		boolean sendToLogger = directPrintToConsole(message, LogLevel.INFO, -1)||config.sendToLoggerIfPrintedToConsole;
		if(sendToLogger&&isLogLevelToPrint(LogLevel.INFO, config.minimumLoggerLevel) && (getLogger()!=null)) getLogger().info(message);
	}
	public void info(String message, Object... o) {
		boolean sendToLogger = directPrintToConsole(message, LogLevel.INFO, -1, o)||config.sendToLoggerIfPrintedToConsole;
		if(sendToLogger&&isLogLevelToPrint(LogLevel.INFO, config.minimumLoggerLevel) && (getLogger()!=null)) getLogger().info(message, o);
	}
	public void debug(String message) {
		boolean sendToLogger = directPrintToConsole(message, LogLevel.DEBUG, -1)||config.sendToLoggerIfPrintedToConsole;
		if(sendToLogger&&isLogLevelToPrint(LogLevel.DEBUG, config.minimumLoggerLevel) && (getLogger()!=null)) getLogger().debug(message);
	}
	public void debug(String message, Object... o) {
		boolean sendToLogger = directPrintToConsole(message, LogLevel.DEBUG, -1, o)||config.sendToLoggerIfPrintedToConsole;
		if(sendToLogger&&isLogLevelToPrint(LogLevel.DEBUG, config.minimumLoggerLevel) && (getLogger()!=null)) getLogger().debug(message, o);
	}
	public void trace(String message) {
		boolean sendToLogger = directPrintToConsole(message, LogLevel.TRACE, -1)||config.sendToLoggerIfPrintedToConsole;
		if(sendToLogger&&isLogLevelToPrint(LogLevel.TRACE, config.minimumLoggerLevel) && (getLogger()!=null)) getLogger().trace(message);
	}
	public void trace(String message, Object... o) {
		boolean sendToLogger = directPrintToConsole(message, LogLevel.TRACE, -1, o)||config.sendToLoggerIfPrintedToConsole;
		if(sendToLogger&&isLogLevelToPrint(LogLevel.TRACE, config.minimumLoggerLevel) && (getLogger()!=null)) getLogger().trace(message, o);
	}
	public boolean isTraceEnabled() {
		if((config.minimumDirectConsoleLevel != null)&&isLogLevelToPrint(LogLevel.TRACE, config.minimumDirectConsoleLevel)) {
			return true;
		}
		if((config.minimumLoggerLevel!= null)&&isLogLevelToPrint(LogLevel.TRACE, config.minimumLoggerLevel)) {
			return true;
		}
		if((getLogger() != null) && getLogger().isTraceEnabled()) {
			return true;
		}
		return false;
	}
	
	public int logSchedule(String initialMessage, ReadOnlyTimeSeries sched, long startTime, int maxValue, String scheduleName,
			LogLevel level) {
		String output = initialMessage+"\r\n";
		int i;
		long curTime = startTime;
		if(scheduleName == null) scheduleName = "sched";
		for(i=0; i<maxValue; i++) {
			if(sched.size() < i) break;
			SampledValue val = sched.getNextValue(curTime);
			if(val == null) break;
			output += "    "+(scheduleName+" ["+StringFormatHelper.getFullTimeDateInLocalTimeZone(val.getTimestamp())+
					"]:"+val.getValue().getFloatValue())+"\r\n";
			curTime = val.getTimestamp()+1;
		}
		log(output, level);
		return i;
	}

	public void log(String message, LogLevel level) {
		switch(level) {
		case ERROR:
			error(message);
			break;
		case WARNING:
			warn(message);
			break;
		case INFO:
			info(message);
			break;
		case DEBUG:
			debug(message);
			break;
		case TRACE:
			trace(message);
			break;
		default:
			throw new IllegalStateException("unknown loglevel:"+level);
		}
	}
	
	protected Logger getLogger() {
		if(ogemaLogger != null) return ogemaLogger;
		return slf4jLogger;
	}
	
	protected DevelopmentLoggerConfiguration getStandardLogConfig() {
		try {
			return DevelopmentLoggerConfiguration.class.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}
	
	protected boolean directPrintToConsole(String message, LogLevel level, long currentTime, Object... o) {
		return directPrintToConsole(String.format(message.replace("{}",  "%s"), o), level, currentTime);
	}
	protected boolean directPrintToConsole(String content, LogLevel level, long currentTime) {
		updateConfig();
		if(!isLogLevelToPrint(level, config.minimumDirectConsoleLevel)) return true;
		String out = content;
		if(shortId != null) {
			out = shortId+": "+out;
		}
		if((currentTime >= 0)&&(config.directToConsoleTimeTimeFormat != null)) {
			//DateFormat formatter = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss:SSS");
			DateFormat formatter = new SimpleDateFormat(config.directToConsoleTimeTimeFormat);
			out = formatter.format(currentTime)+": "+out;
		}
		if(config.printMessageCounter) {
			out = String.format("%3d: ", counter) + out;
		}
		System.out.println(out);
		counter ++;
		return false;
	}
	
	protected void setTimeFormat() {
		if(config.directToConsoleTimeTimeFormat != null) return;
		config.directToConsoleTimeTimeFormat = getTimeFormat(config.directToConsoleTimePrinting);
	}
	protected String getTimeFormat(TimePrinting timePrintingMode) {
		switch(timePrintingMode) {
		case FULL_DATE:
			return "dd.MM.YYYY HH:mm:ss:SSS";
		case YEAR_TO_SECOND:
			return "dd.MM.YYYY HH:mm:ss";
		case HOUR_TO_MILLISECOND:
			return "HH:mm:ss:SSS";
		case HOUR_TO_SECOND:
			return "HH:mm:ss";
		case NONE:
			return null;
		default:
			throw new IllegalStateException("unknown TimePrintingMode:"+timePrintingMode);
		}
	}
	
	protected void updateConfig() {
		if(shortId == null) return;
		boolean update = false;
		if(globalConfigId == null) {
			globalConfigId = GlobalDevelopmentProvider.shortId;
			update = true;
		} else if(!globalConfigId.equals(GlobalDevelopmentProvider.shortId)) {
			globalConfigId = GlobalDevelopmentProvider.shortId;
			update = true;
		}
		if(update) {
			DevelopmentLoggerConfiguration newConfig = GlobalDevelopmentProvider.getConfig().getLoggerConfiguration(shortId);
			if(minimumConsoleLevelSetInApp) {
				newConfig.minimumDirectConsoleLevel = config.minimumDirectConsoleLevel;
			}
			if(minimumLoggerLevelSetInApp) {
				newConfig.minimumLoggerLevel = config.minimumLoggerLevel;
			}
			if(timeFormatSetInApp) {
				newConfig.directToConsoleTimeTimeFormat = config.directToConsoleTimeTimeFormat;
			}
			config = newConfig;
			info("Update Log Config for "+shortId+" for GlobalConfig "+globalConfigId);
		}
	}
	
	public static boolean isLogLevelToPrint(LogLevel messageLevel, LogLevel minimumLevel) {
		if(minimumLevel == null) return false;
		switch(minimumLevel) {
		case TRACE:
			return true;
		case DEBUG:
			switch(messageLevel) {
			case TRACE:
				return false;
			default:
				return true;
			}
		case INFO:
			switch(messageLevel) {
			case TRACE:
			case DEBUG:
				return false;
			default:
				return true;
			}
		case WARNING:
			switch(messageLevel) {
			case TRACE:
			case DEBUG:
			case INFO:
				return false;
			default:
				return true;
			}
		case ERROR:
			switch(messageLevel) {
			case TRACE:
			case DEBUG:
			case INFO:
			case WARNING:
				return false;
			default:
				return true;
			}
		default:
			throw new IllegalStateException("unknown loglevel:"+minimumLevel);
		}
	}
	
	public void setTimeFormat(TimePrinting timePrintingMode) {
		config.directToConsoleTimeTimeFormat = getTimeFormat(timePrintingMode);
		timeFormatSetInApp = true;
	}
	public void setTimeFormat(String format) {
		config.directToConsoleTimeTimeFormat = format;
		timeFormatSetInApp = true;
	}
	public void setMinimumDirectConsoleLogLevel(LogLevel minimumLevel) {
		config.minimumDirectConsoleLevel = minimumLevel;
		minimumConsoleLevelSetInApp = true;
	}
	public void setMinimumLoggerLevel(LogLevel minimumLevel) {
		config.minimumLoggerLevel = minimumLevel;
		minimumLoggerLevelSetInApp = true;
	}
}
