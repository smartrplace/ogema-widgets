/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
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
