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

package de.iwes.widgets.html.plot.api;

/** 
 * the way a data set is represented differs considerably
 * between the various Javascript libraries, e.g. one library
 * expects two arrays [x1, x2,..., xn], [y1, y2,..., yn], another
 * one expects an array of arrays [[x1,y1],...,[xn,yn]], yet
 * another one expects an array of objects [{x1:y1},...{xn:yn}], etc.<br>
 * 
 * In order to avoid excessive conversions, each implementation defines
 * its own data format and the common interface is quite empty.
 */
public interface Plot2DDataSet {
	
	public String getId();
	
	/**
	 * 
	 * @param xmin
	 * @param xmax
	 * @return
	 * 		must return an object of the respective subtype
	 * @throws UnsupportedOperationException
	 */
	public Plot2DDataSet getValues(float xmin, float xmax) throws UnsupportedOperationException;
	
	public Plot2DDataSet getValues(float xmin, float xmax, int maxNrPoints) throws UnsupportedOperationException;

	
	/**
	 * @param xmin
	 * @param xmax
	 * @param ymin
	 * @param ymax
	 * @return
	 * 		must return an object of the respective subtype
	 * @throws UnsupportedOperationException
	 */
	public Plot2DDataSet getValues(float xmin, float xmax, float ymin, float ymax) throws UnsupportedOperationException;


}
