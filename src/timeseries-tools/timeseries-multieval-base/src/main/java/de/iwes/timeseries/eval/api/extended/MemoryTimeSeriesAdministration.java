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
package de.iwes.timeseries.eval.api.extended;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.api.DataProvider;

/**intended as OSGi service*/
public interface MemoryTimeSeriesAdministration extends DataProvider<ReadOnlyTimeSeries> {
	/**Add time series to storage
	 * 
	 * @param key identifier of the time series. If the key already exists in the
	 * 		storage the entry is overridden
	 * @param timeSeries
	 * @return the previous value associated with key, or null if there was no mapping for key.
	 */
	ReadOnlyTimeSeries addTimeSeries(String sourceId, String key, ReadOnlyTimeSeries timeSeries);
	
	/** Remove entry from storage
	 * 
	 * @param key identifier to be removed.
	 * @return the previous value associated with key, or null if there was no mapping for key. 
	 */
	ReadOnlyTimeSeries removeTimeSeries(String sourceId, String key);
	
	/** Check if a time series for a key exists
	 * 
	 * @param key
	 * @return true if a time series exists in the storage for the key
	 */
	boolean hasKey(String sourceId, String key);
	
	/**Get time series from storage
	 * 
	 * @param key
	 * @return null if the key does not exist in the storage
	 */
	ReadOnlyTimeSeries getTimeSeries(String sourceId, String key);
	
	/**Get all sources in the storage*/
	List<String> getAllSources();
	List<String> getAllKeys(String source);
	
	/**Export the time series into a CSV file. The resulting file shall have the format
	 * of a single file in the sema-Zip-Export structure with the keys as heading Strings even
	 * if this export does not make sure all data is provided from the same room.<br>
	 * This method shall be made available as console command in the form
	 * exportCSV <fileName> key1 key2 ...
	 * @return true if all keys could be written out. Missing keys shall be omitted*/
	boolean exportToCSV(OutputStream file, Map<String, List<String>> keys);
	/**Import a CSV file in the format written by exportToCSV into the storage. If a key already
	 * exists it shall be overwritten<br>
	 * This method shall be made available as console command
	 * @param file
	 * @return number of time series imported 
	 */
	 int importFromCSV(InputStream file);
	 
	 
	 boolean exportToJSONFile(MultiResult multiResult, OutputStream file);
}
