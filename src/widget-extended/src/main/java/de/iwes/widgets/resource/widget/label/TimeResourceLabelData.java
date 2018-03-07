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

package de.iwes.widgets.resource.widget.label;

import org.json.JSONObject;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.tools.resource.util.TimeUtils;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.resource.widget.textfield.TimeResourceTextFieldData;
import de.iwes.widgets.resource.widget.textfield.TimeResourceTextField.Interval;

public class TimeResourceLabelData extends ResourceLabelData<TimeResource> {
	
	// FIXME move interval out of the text field
	private Interval interval = null;
	
	public TimeResourceLabelData(TimeResourceLabel textField) {
		super(textField);
	}
	
	@Override
	public JSONObject retrieveGETData(OgemaHttpRequest req) {
		JSONObject result = super.retrieveGETData(req);
		TimeResource tr = getSelectedResource();
		if (tr != null) {
			String str;
			if (interval != null)
				str = TimeResourceTextFieldData.getIntervalString(tr.getValue(), interval);
			else
				str = TimeUtils.getDateAndTimeString(tr.getValue());
			result.put("value", str);
		}
		return result;
	}

	public Interval getInterval() {
		return interval;
	}

	/**
	 * Set to null in order to represent absoulte time (millis since 1 Jan 1970)
	 * @param interval
	 */
	public void setInterval(Interval interval) {
		this.interval = interval;
	}

	
	
}
