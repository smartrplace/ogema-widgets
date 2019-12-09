package org.ogema.timeseries.eval.eventlog.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import de.iwes.widgets.template.LabelledItem;

public interface EventLogFileParser extends LabelledItem {
	public static class EventLogResult {
		public String eventId;
		public long eventTime;
		public String fullEventString;
		public String gatewayId;
		
		/** Message for event, usually including gatewayId*/
		public String eventMessage;
		
		/** The meaning of the value is event-specific and may not be set*/
		public Float eventValue = null;
		/** File where event was found*/
		public Path sourcePath;
	}
	
	/** Parse file event log file
	 * 
	 * @param logFileStream input stream reading (unzipped) log file
	 * @param eventIds eventIds that shall be searched. If null all supported event IDs shall be
	 * 		returned. The argument may not be supported by all implementations, so you have to expect
	 * 		to get back any event known to the provider.
	 * @param OGEMA log files only contain time relative to the start of the day. So the start time of the
	 * 		day has to be provided.
	 * @return Events found. The events shall be
	 * 		sorted according to the eventTime.
	 * @throws IOException 
	 */
	List<EventLogResult> parseLogFile(InputStream logFileStream, List<String> eventIds, long dayStart) throws IOException;
	
	/** List of eventIds known to the parsing provider*/
	List<String> supportedEventIds();
}
