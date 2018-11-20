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
