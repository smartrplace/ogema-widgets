package org.ogema.model.daytype;

import org.ogema.core.model.array.IntegerArrayResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.model.prototypes.Data;

public interface DayType extends Data {
	/**Note: If an application finds conflicting DayType resources for a certain setting the most
	 * specific shall be applied<br> 
	 * 1: Monday<br>
	 * 2: Tuesday<br>
	 * 3: Wednesday<br>
	 * 4: Thursday<br>
	 * 5: Friday<br>
	 * 6: Saturday<br>
	 * 7: Sunday<br>
	 * 8: Monday to Friday<br>
	 * 9: Saturday/Sunday<br>
	 * 10: (reserved)<br>
	 * 11: Holidays<br>
	 * 12: All Days<br>
	 * 100: List of simple types<br>
	 * 1000: Day defined by external calendar<br>
	 * 1001: External event<br>
	 * ...
	 */
	IntegerResource type();
	
	/**If true the day type shall be applied to holidays as to the usual setting*/
	BooleanResource includeHolidays();
	
	/**Only relevant for type == 100*/
	IntegerArrayResource simpleTypes();
}
