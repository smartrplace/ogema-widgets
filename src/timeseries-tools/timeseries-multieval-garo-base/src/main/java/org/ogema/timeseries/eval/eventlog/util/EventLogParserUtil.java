package org.ogema.timeseries.eval.eventlog.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

import org.ogema.timeseries.eval.eventlog.util.EventLogFileParser.EventLogResult;
import org.ogema.tools.resource.util.TimeUtils;
import org.slf4j.Logger;

import de.iwes.util.timer.AbsoluteTimeHelper;
import de.iwes.util.timer.AbsoluteTiming;

public class EventLogParserUtil {
	/** Open zipped log file and apply it to one or several parsers selected
	 * 
	 * @param gzFile
	 * @param parsers
	 * @param eventIds
	 * @param dayStart should be {@link #getDayStartOfLogFile(Path) of gzFile}. If null the value will
	 * 		be calculated here, can be provided for performance optimization
	 * @return
	 */
    public static Map<String, List<EventLogResult>> processNewGzLogFile(Path gzFile,
    		EventLogFileParser parser, List<String> eventIds, Long dayStart) {
    	return processNewGzLogFile(gzFile, Arrays.asList(new EventLogFileParser[] {parser}), eventIds, dayStart);
    }
    public static Map<String, List<EventLogResult>> processNewGzLogFile(Path gzFile,
    		List<EventLogFileParser> parsers, List<String> eventIds, Long dayStart) {
	    if(dayStart == null)
	    	dayStart = getDayStartOfLogFile(gzFile);

		Map<String, List<EventLogResult>> processResult = new HashMap<>();
	    if(dayStart == null) 
	    	return processResult;
		try {
			InputStream fileStream = new FileInputStream(gzFile.toFile());
			InputStream gzipStream = new GZIPInputStream(fileStream);
   			for(EventLogFileParser parser: parsers) {
				List<EventLogResult> parserResult = parser.parseLogFile(gzipStream, eventIds, dayStart);
				processResult.put(parser.id(), parserResult);
			}
   			gzipStream.close();
   			fileStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int totalEvents = 0;
		for(List<EventLogResult> resList: processResult.values()) {
			totalEvents += resList.size();
			for(EventLogResult elr: resList) {
				elr.sourcePath = gzFile;
			}
		}
		//log.info("processNewJarFile result: {}",ip);
		//System.out.println("processNewLogFile result: "+gzFile.toString()+" event#:"+totalEvents); //+ "ip:"+ip);
		return processResult;
    }
    
    public static Long getDayStartOfLogFile(Path gzFile) {
    	String fileName = gzFile.getFileName().toString();
		String logFileName = fileName.substring(0, fileName.length()-3);
		long dayStart = 0;
		if(logFileName.length() > 19) {
			String dateStr = logFileName.substring(6, 19);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH");
			try {
			    Date parsed = format.parse(dateStr);
			    dayStart = AbsoluteTimeHelper.getIntervalStart(parsed.getTime(), AbsoluteTiming.DAY);
			    return dayStart;
			} catch (ParseException pe) {
			    System.out.println("ERROR: Cannot parse \"" + dateStr + "\"");
			    return null;
			}
		}
		else return null;
    }
    
    /** Check a line for events 
     * 
     * @param trim lines trimmed from starting/trailing spaces
     * @param eventText
     * @param eventId
     * @param doLog
     * @param result
     * @param dayStart
     * @param gwId
     * @param log may be null
     */
    public static void checkEvent(String trim, String eventText, String eventId, boolean doLog,
			List<EventLogResult> result, long dayStart, String gwId, Logger log) {
		if(trim.contains(eventText)) {
			// FIXME
			System.out.println("     Event found " + trim);
			EventLogResult elr = new EventLogResult();
			if(trim.length() > 12) {
				SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
				format.setTimeZone(TimeZone.getTimeZone("UTC"));
				String timeString = trim.substring(0, 12);
				try {
				    Date parsed = format.parse(timeString);  
				    elr.eventTime = dayStart + parsed.getTime();
				} catch (ParseException pe) {
				    System.out.println("ERROR: Cannot parse \"" + timeString + "\"");
				    return;
				}
			} else {
				System.out.println(" !!!!!!!! No time string in line:"+trim);
				return;
			}
			elr.eventId = eventId;
			//elr.eventTime = getTimeFromLogLine(line);
			elr.fullEventString = trim;
			result.add(elr);
			if(log != null) log.info(gwId+"#"+TimeUtils.getDateAndTimeString(elr.eventTime)+" : "+eventId);
			if(gwId != null) System.out.println(gwId+"#"+TimeUtils.getDateAndTimeString(elr.eventTime)+" : "+eventId);
		}
		
	}

}
