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
package de.iwes.widgets.html.form.label;

import org.joda.time.PeriodType;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * Display a time interval
 * @deprecated Joda dependency to be removed
 */
@Deprecated
public class TimeIntervalLabel extends Label  {

    private static final long serialVersionUID = 7367326133405921539L;
    private long defaultInterval = Long.MIN_VALUE;
    private PeriodType defaultType = null;
    private boolean displayZeros = false;

    /*********** Constructors **********/

    public TimeIntervalLabel(WidgetPage<?> page, String id) {
    	super(page,id);
    }

    public TimeIntervalLabel(OgemaWidget parent, String id, OgemaHttpRequest req) {
    	super(parent,id,req);
    }

    /******* Inherited methods ******/

	@Override
	public TimeIntervalLabelData createNewSession() {
		return new TimeIntervalLabelData(this);
	}

	@Override
	public TimeIntervalLabelData getData(OgemaHttpRequest req) {
		return (TimeIntervalLabelData) super.getData(req);
	}

	@Override
	protected void setDefaultValues(LabelData opt) {
		super.setDefaultValues(opt);
		TimeIntervalLabelData opt2 = (TimeIntervalLabelData) opt;
		opt2.setInterval(defaultInterval);
		opt2.setDisplayZeros(displayZeros);
		if (defaultType != null)
			opt2.setPeriodType(defaultType);
	}

    /******* Public methods ******/

	/**
	 * in ms
	 * @param interval
	 */
	public void setDefaultInterval(long interval) {
		this.defaultInterval = interval;
	}

	/**
	 * @param interval
	 * 		in ms
	 * @param req
	 */
	public void setInterval(long interval, OgemaHttpRequest req) {
		getData(req).setInterval(interval);
	}

	/**
	 * @param req
	 * @return
	 * 		interval in ms
	 */
	public long getInterval(OgemaHttpRequest req) {
		return getData(req).getInterval();
	}

 	/**
 	 * Defines which types of date items are shown (days, years, months, ....)
 	 * @param type
 	 */
 	public void setDefaultPeriodType(PeriodType type) {
 		this.defaultType = type;
 	}

 	/**
 	 * Defines which types of date items are shown (days, years, months, ....)
 	 * @param type
 	 * @param req
 	 */
 	public void setPeriodType(PeriodType type, OgemaHttpRequest req) {
 		getData(req).setPeriodType(type);
 	}

 	public PeriodType getPeriodType(OgemaHttpRequest req) {
 		return getData(req).getPeriodType();
 	}

	public void setDefaultDisplayZeros(boolean displayZeros) {
		this.displayZeros = displayZeros;
	}

	public boolean isDisplayZeros(OgemaHttpRequest req) {
		return getData(req).isDisplayZeros();
	}

	public void setDisplayZeros(boolean displayZeros, OgemaHttpRequest req) {
		getData(req).setDisplayZeros(displayZeros);
	}

}
