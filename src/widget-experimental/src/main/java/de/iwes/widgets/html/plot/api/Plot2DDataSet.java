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
	public default Plot2DDataSet getValues(float xmin, float xmax) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("not implemented");
	}

	public default Plot2DDataSet getValues(float xmin, float xmax, int maxNrPoints) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("not implemented");
	}


	/**
	 * @param xmin
	 * @param xmax
	 * @param ymin
	 * @param ymax
	 * @return
	 * 		must return an object of the respective subtype
	 * @throws UnsupportedOperationException
	 */
	public default Plot2DDataSet getValues(float xmin, float xmax, float ymin, float ymax) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("not implemented");
	}


}
