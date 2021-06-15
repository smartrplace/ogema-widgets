/**
 * ﻿Copyright 2014-2018 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.util.timer;

import java.util.LinkedHashMap;
import java.util.Map;

/** Providing options for aligned interval types defined in {@link TimeIntervalLength}*/
public class AbsoluteTiming {
	final public static int YEAR = 1;
	final public static int QUARTER_YEAR = 2;
	final public static int MONTH = 3;
	final public static int WEEK = 6;
	final public static int DAY = 10;
	final public static int FOUR_HOUR = 15;
	final public static int HOUR = 100;
	final public static int HALF_HOUR = 200;
	final public static int FIFTEEN_MINUTE = 220;
	final public static int TEN_MINUTE = 240;
	final public static int FIVE_MINUTE = 320;
	final public static int MINUTE = 101;
	final public static int HALF_MINUTE = 1000;
	final public static int TEN_SECOND = 1020;
	final public static int SECOND = 102;
	
	/** ANY_RANGE can be used in some settings to indicate that no special alignment takes
	 * place but that data dependencies exist over all input data. Esepcially for online
	 * evaluations that means that calculations may have to be repeated on all input data
	 * on every request/update.
	 */
	final public static int ANY_RANGE = -100;
	
	final public static int[] INTERVALS = new int[] {SECOND, TEN_SECOND, HALF_MINUTE, MINUTE,
			FIVE_MINUTE, TEN_MINUTE, FIFTEEN_MINUTE, HALF_HOUR, HALF_HOUR, HOUR, FOUR_HOUR,
			DAY, WEEK, MONTH, QUARTER_YEAR, YEAR};
	
	public static final Map<String, String> INTERVAL_NAME_MAP = new LinkedHashMap<>();
	static {
		INTERVAL_NAME_MAP.put("0", "None");
		INTERVAL_NAME_MAP.put(""+HOUR, "Hour");
		INTERVAL_NAME_MAP.put(""+FOUR_HOUR, "Four Hours");
		INTERVAL_NAME_MAP.put(""+DAY, "Day");
		INTERVAL_NAME_MAP.put(""+WEEK, "Week");
		INTERVAL_NAME_MAP.put(""+MONTH, "Month");
		INTERVAL_NAME_MAP.put(""+QUARTER_YEAR, "Quarter Year");
		INTERVAL_NAME_MAP.put(""+YEAR, "Year");
		INTERVAL_NAME_MAP.put(""+HALF_HOUR, "Half Hour");
		INTERVAL_NAME_MAP.put(""+FIFTEEN_MINUTE, "Fifteen Minutes");
		INTERVAL_NAME_MAP.put(""+TEN_MINUTE, "Ten Minutes");
		INTERVAL_NAME_MAP.put(""+FIVE_MINUTE, "Five Minutes");
		INTERVAL_NAME_MAP.put(""+MINUTE, "Minute");
		INTERVAL_NAME_MAP.put(""+HALF_MINUTE, "Half Minute");
		INTERVAL_NAME_MAP.put(""+TEN_SECOND, "Ten Seconds");
		INTERVAL_NAME_MAP.put(""+SECOND, "Second");
	}

}
