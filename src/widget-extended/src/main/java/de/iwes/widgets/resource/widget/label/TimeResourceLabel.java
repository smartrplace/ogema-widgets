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