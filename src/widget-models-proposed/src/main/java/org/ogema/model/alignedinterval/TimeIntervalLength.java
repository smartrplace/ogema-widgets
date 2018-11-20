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
 * Copyright 2009 - 2014
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IIS
 * Fraunhofer ISE
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package org.ogema.model.alignedinterval;

import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.model.prototypes.Data;

/** An interval length that occurs regularly */
public interface TimeIntervalLength extends Data {
	public static enum TypeValues {
		YEAR(1),QUARTER_YEAR(2);
		public final int type;
		private TypeValues(int type) {
			this.type = type;
		}
	}
	final public static int YEAR = 1;
	final public static int QUARTER_YEAR = 2;
	final public static int MONTH = 3;
	final public static int WEEK = 6;
	final public static int DAY = 10;
	final public static int HOUR = 100;
	final public static int HALF_HOUR = 200;
	final public static int FIFTEEN_MINUTE = 220;
	final public static int TEN_MINUTE = 240;
	final public static int FIVE_MINUTE = 320;
	final public static int MINUTE = 101;
	final public static int HALF_MINUTE = 1000;
	final public static int TEN_SECOND = 1020;
	final public static int SECOND = 102;
	/**
	 * The following definitions apply:<br>
	 * year (type=1):  definition of sub element indices
	 * 			full year, e.g. 1970, 2016<br>
	 * quarter of year (type=2): definition of sub element indices:
	 * 			1=January..March, 2=April..June, 3=July..September, 4=October..December
	 * month (type=3): definition of sub element indices:
	 * 			1=January, 12=December<br>
	 * week (type=6): definition of sub element indices:
	 * 			1=Monday, 7=Sunday. Note that 8 can be used to represent holidays relevant to the repeating
	 * 			time resource. The provision of this information must be defined in the resource using the functionality.<br>
	 * day (type=10): definition of sub element indices:
	 * 			1..366<br>
	 * 1/6 day (type=15), usually four intervals within day, may be adjusted due to daylight savings time
	 * hour (type=100): 0..23 (note that this is a fixed interval)<br>
	 * minute (type=101): 0..60 (note that this is a fixed interval)<br>
	 * second (type=102): 0..60 (note that this is a fixed interval)<br>
	 * <br>
	 * Further fixed intervals:<br>
	 * half-hour (type=200): 1..2<br>
	 * 1/3 hour (type=210): 1..3<br>
	 * 1/4 hour (type=220): 1..4<br>
	 * 1/5 hour (type=230): 1..5<br>
	 * 1/6 hour (type=240): 1..6<br>
	 * 1/10 hour (type=300): 1..10<br>
	 * 1/12 hour (type=320): 1..12<br>
	 * 1/15hour (type=350): 1..15<br>
	 * 1/20hour (type=400): 1..20<br>
	 * 1/30hour (type=500): 1..30<br>
	 * 1/2 minute (type=1000): 1..2<br>
	 * 1/6 minute (type 1020): 1..6
	 */
	IntegerResource type();
	
	/**
	 * In some cases the interval shall not be given by a calendar time period type, but just by a fixed duration that
	 * may not be aligned with a calendar. In most cases this element is not used.
	 */
	TimeResource fixedDuration();
}
