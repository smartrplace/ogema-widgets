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

package de.iwes.widgets.html.durationselector;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DurationFieldType;
import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class DurationSelectorData extends WidgetData { 
	
	private long duration = Long.MIN_VALUE; // MIN_VALUE is treated as "not set"
	private boolean allowZero = true;
	private DurationFieldType[] admissibleTypes = new DurationFieldType[]
			{	
				DurationFieldType.years(),
				DurationFieldType.months(),
				DurationFieldType.weeks(),
				DurationFieldType.days(), 
				DurationFieldType.hours(),
				DurationFieldType.minutes(),
				DurationFieldType.seconds(),
				DurationFieldType.millis()
			};
	private DurationFieldType selectedType = null;
	private static final Map<DurationFieldType, String> jsTypeShorthands;
	
	static {
		jsTypeShorthands = new HashMap<DurationFieldType, String>();
		jsTypeShorthands.put(DurationFieldType.years(), "y");
		jsTypeShorthands.put(DurationFieldType.months(), "M");
		jsTypeShorthands.put(DurationFieldType.weeks(), "w");
		jsTypeShorthands.put(DurationFieldType.days(), "d");
		jsTypeShorthands.put(DurationFieldType.hours(), "h");
		jsTypeShorthands.put(DurationFieldType.minutes(), "m");
		jsTypeShorthands.put(DurationFieldType.seconds(), "s");
		jsTypeShorthands.put(DurationFieldType.millis(), "ms");
	}
	
	/*********** Constructor **********/
	
	public DurationSelectorData(DurationSelector selector) {
		super(selector);
	}
	
	/******* Inherited methods ******/
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		JSONObject result = new JSONObject();
		if (duration != Long.MIN_VALUE)
			result.put("duration", duration); // FIXME not used; data is only transferred from client to server
		checkSelectedTypeOk();
		if (selectedType == null && admissibleTypes != null && admissibleTypes.length != 0) 
			selectedType = admissibleTypes[0];
		result.put("types", getAdmissibleTypesJS());
		if (selectedType != null)
			result.put("selectedType", jsTypeShorthands.get(selectedType));
		result.put("allowZero", allowZero);
        return result;
    }
	
	@Override
    public JSONObject onPOST(String data, OgemaHttpRequest req) {
		JSONObject request = new JSONObject(data);
		Long result = null;
		try {
			result = request.getLong("data");
		} catch (Exception e) {
		}
		if (result != null)
			duration = result;
		else
			duration = Long.MIN_VALUE;
		return request;
    }	
	
	/********** Public methods **********/

	/**
	 * Returns Long.MIN_VALUE if no duration has been selected
	 * @return
	 */
	public long getValue() {
		return duration;
	}
	
	/**
	 * In order to deselect the value, pass Long.MIN_VALUE
	 * @param value
	 */
	public void setValue(long value) {
		this.duration = value;
	}
	
	public boolean isAllowZero() {
		return allowZero;
	}

	public void setAllowZero(boolean allowZero) {
		this.allowZero = allowZero;
	}

	public DurationFieldType[] getAdmissibleTypes() {
		return admissibleTypes;
	}

	public void setAdmissibleTypes(DurationFieldType[] admissibleTypes) {
		this.admissibleTypes = admissibleTypes;
	}
	
	public void setSelectedType(DurationFieldType selectedType) {
		this.selectedType = selectedType;
	}
	
	/*
	 ******** Internal methods *****
	 */
	
	private String[] getAdmissibleTypesJS() {
		String[] result = new String[admissibleTypes.length];
		for (int i=0;i<admissibleTypes.length;i++) {
			result[i] = jsTypeShorthands.get(admissibleTypes[i]);
		}
		return result;
	}
	
	private void checkSelectedTypeOk() {
		if (selectedType == null) return;
		for (DurationFieldType tp: admissibleTypes) {
			if (tp.equals(selectedType)) 
				return;
		}
		selectedType = null;
	}
	
	
	
}
