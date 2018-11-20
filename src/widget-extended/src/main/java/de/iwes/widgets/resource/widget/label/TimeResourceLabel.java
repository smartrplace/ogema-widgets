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

import org.ogema.core.model.simple.TimeResource;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.label.LabelData;
import de.iwes.widgets.resource.widget.textfield.TimeResourceTextField.Interval;

/**
 * A label that prints the value of a {@link TimeResource}. It supports both absolute time
 * and time intervals. This type has to be set explicitly, though, via {@link #setInterval(Interval, OgemaHttpRequest)}
 * or {@link #setDefaultInterval(Interval)}. 
 * 
 * @see ResourceLabel
 */
public class TimeResourceLabel extends ResourceLabel<TimeResource> {

	private static final long serialVersionUID = 1L;
	private Interval defaultInterval = null;

	public TimeResourceLabel(WidgetPage<?> page, String id) {
		super(page, id);
	}
	
	public TimeResourceLabel(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
	}

	@Override
	public TimeResourceLabelData createNewSession() {
		return new TimeResourceLabelData(this);
	}
	
	@Override
	public TimeResourceLabelData getData(OgemaHttpRequest req) {
		return (TimeResourceLabelData) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(LabelData opt) {
		super.setDefaultValues(opt);
		TimeResourceLabelData opt2 = (TimeResourceLabelData) opt;
		opt2.setInterval(defaultInterval);
	}

	/**
	 * @param req
	 * @return
	 * 		null, if the resource represents absolute time, the interval unit otherwise
	 */
	public Interval getInterval(OgemaHttpRequest req) {
		return getData(req).getInterval();
	}

	/**
	 * Set to null in order to represent absoulte time (millis since 1 Jan 1970)
	 * @param interval
	 */
	public void setInterval(Interval interval, OgemaHttpRequest req) {
		getData(req).setInterval(interval);
	}
	
	/**
	 * Set to null in order to represent absoulte time (millis since 1 Jan 1970)
	 * @param interval
	 */
	public void setDefaultInterval(Interval interval) {
		this.defaultInterval = interval;
	}
	
	
}