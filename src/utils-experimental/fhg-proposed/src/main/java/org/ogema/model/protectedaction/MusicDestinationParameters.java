package org.ogema.model.protectedaction;

import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;

public interface MusicDestinationParameters {
	/**Standard values:
	 * classic, pop, jazz, audio-books, ...
	 */
	StringResource genre();
	
	TimeResource maxSize();
	FloatResource maxSampleRate();
	FloatResource minSampleRate();
	FloatResource resampleToRate();
	/** 1: mp3
	 * 2: wav
	 * 3: ...
	 */
	IntegerResource convertToFormat();
}
