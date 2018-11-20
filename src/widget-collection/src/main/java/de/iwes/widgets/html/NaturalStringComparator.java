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
package de.iwes.widgets.html;

import java.util.Comparator;

/**
 * Compare Strings including numbers.
 * 
 * See http://stackoverflow.com/questions/1262239/natural-sort-order-string-comparison-in-java-is-one-built-in 
 */
public class NaturalStringComparator implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		return compareNatural(o1, o2);
	}
	
	public static final int compareNatural (String s1, String s2)
	{
	   // Skip all identical characters
	   int len1 = s1.length();
	   int len2 = s2.length();
	   int i;
	   char c1, c2;
	   for (i = 0, c1 = 0, c2 = 0; (i < len1) && (i < len2) && (c1 = s1.charAt(i)) == (c2 = s2.charAt(i)); i++);

	   // Check end of string
	   if (c1 == c2)
	      return(len1 - len2);

	   // Check digit in first string
	   if (Character.isDigit(c1))
	   {
	      // Check digit only in first string
	      if (!Character.isDigit(c2))
	         return((i > 0) && Character.isDigit(s1.charAt(i - 1)) ? 1 : c1 - c2);

	      // Scan all integer digits
	      int x1, x2;
	      for (x1 = i + 1; (x1 < len1) && Character.isDigit(s1.charAt(x1)); x1++);
	      for (x2 = i + 1; (x2 < len2) && Character.isDigit(s2.charAt(x2)); x2++);

	      // Longer integer wins, first digit otherwise
	      return(x2 == x1 ? c1 - c2 : x1 - x2);
	   }

	   // Check digit only in second string
	   if (Character.isDigit(c2))
	      return((i > 0) && Character.isDigit(s2.charAt(i - 1)) ? -1 : c1 - c2);

	   // No digits
	   return(c1 - c2);
	}
	
	
	
}
