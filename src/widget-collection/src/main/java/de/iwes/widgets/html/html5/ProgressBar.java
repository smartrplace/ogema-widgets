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
package de.iwes.widgets.html.html5;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * Wraps an HTML5 <code>&lt;progress&gt;</code> tag, which is particularly suited to 
 * represent the status of progress for some activity.<br>
 * To enable auto-updating of the client (HTML) side, call
 * {link {@link OgemaWidget#setDefaultPollingInterval(long)},
 * and pass a positive number (update interval in milliseconds). 
 */
public class ProgressBar extends OgemaWidgetBase<ProgressBarData> {
		
	private static final long serialVersionUID = 1L;
	
	private float defaultValue = 0;
	private float defaultMax = 1;

	
	/*
	 ********** Constructors ***********
	 */

	public ProgressBar(WidgetPage<?> page, String id) {
		super(page, id);
	}
	
	public ProgressBar(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
	}

	public ProgressBar(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
	}
	
	/*
	 ********** Inherited methods ***********
	 */
	

	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return ProgressBar.class;
	}

	@Override
	public ProgressBarData createNewSession() {
		return new ProgressBarData(this);
	}
	
	@Override
	protected void setDefaultValues(ProgressBarData opt) {
		super.setDefaultValues(opt);
		opt.setValue(defaultValue);
		opt.setMax(defaultMax);
	}
	
	@Override
	protected void registerJsDependencies() {
		this.registerLibrary(true, "ProgressBar", "/ogema/widget/html5/ProgressBar.js");
	}
	
	/*
	 ************** Public methods *************
	 */

	/** Set default initial value of the progress bar. If not specified the progress bar starts
	 * at the position zero
	 */
	public void setDefaultValue(float value) {
		this.defaultValue = value;
	}
	
	/**Set default value at which the progress bar shows operation completed. If not specified
	 * the operation completed state is reached at a value of 1.0
	 */
	public void setDefaultMax(float max) {
		this.defaultMax = max;
	}
	
	/**Get current progress value*/
	public float getValue(OgemaHttpRequest req) {
		return getData(req).getValue();
	}

	/**Set current progress value*/
	public void setValue(float value,OgemaHttpRequest req) {
		getData(req).setValue(value);
	}

	/**Get value for the currrent session at which the progress bar shows operation completed*/
	public float getMax(OgemaHttpRequest req) {
		return getData(req).getMax();
	}

	/**Set session-specific value at which the progress bar shows operation completed. If not specified
	 * the default maximum value will be used or 1.0
	 */
	public void setMax(float max,OgemaHttpRequest req) {
		getData(req).setMax(max);
	}
	
}
