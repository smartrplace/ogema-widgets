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
package de.iwes.widgets.html.durationselector;

import org.joda.time.DurationFieldType;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * A widget that allows the user to select a time duration. The admissible units of
 * time (like year, month, day, hour, ...) can be configured.
 * TODO a duration selected server-side is not transferred to the client, currently,
 * it only works the other way round.
 * @deprecated Joda dependency to be removed
 */
@Deprecated
public class DurationSelector extends OgemaWidgetBase<DurationSelectorData>  {

    private static final long serialVersionUID = 550713654103033621L;
    private long defaultValue = Long.MIN_VALUE;
	private boolean allowZero = true;
	private DurationFieldType[] admissibleTypes = null;
	private DurationFieldType selectedType = null;

    /*********** Constructor **********/

    public DurationSelector(WidgetPage<?> page, String id) {
    	this(page, id, false);
    }

    public DurationSelector(WidgetPage<?> page, String id, long defaultValue) {
    	this(page, id, false);
    	this.defaultValue = defaultValue;
    }

    public DurationSelector(WidgetPage<?> page, String id, boolean globalWidget) {
     	super(page, id, globalWidget);
    }

    public DurationSelector(OgemaWidget parent, String id, OgemaHttpRequest req) {
     	super(parent, id, req);
    }

    /******* Inherited methods *****/

    @Override
	public DurationSelectorData createNewSession() {
    	return new DurationSelectorData(this);
    }

    @Override
    protected void setDefaultValues(DurationSelectorData opt) {
    	super.setDefaultValues(opt);
    	opt.setValue(defaultValue);
    	opt.setAllowZero(allowZero);
    	if (admissibleTypes != null)
    		opt.setAdmissibleTypes(admissibleTypes);
    	if (selectedType != null)
    		opt.setSelectedType(selectedType);
    }

    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
    	return DurationSelector.class;
    }

    @Override
    protected void registerJsDependencies() {
    	registerLibrary(true, "moment", "/ogema/widget/durationselector/lib/moment-with-locales_2.10.0.min.js"); // FIXME global moment variable will be removed in some future version
    	super.registerJsDependencies();
    }

	 /*********** Public methods **********/

	public long getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(long defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setValue(long value,OgemaHttpRequest req) {
		getData(req).setValue(value);
	}

	public long getValue(OgemaHttpRequest req) {
		return getData(req).getValue();
	}


	/**
	 * Enable or disable the selection of a zero-duration interval. Default is true.
	 * @param allowZero
	 */
	public void setDefaultAllowZero(boolean allowZero) {
		this.allowZero = allowZero;
	}

	/**
	 * @see #setAllowZero(boolean, OgemaHttpRequest)
	 * @param req
	 * @return
	 */
	public boolean isAllowZero(OgemaHttpRequest req) {
		return getData(req).isAllowZero();
	}

	/**
	 * Enable or disable the selection of a zero-duration interval. Default is true.
	 * @param allowZero
	 * @param req
	 */
	public void setAllowZero(boolean allowZero, OgemaHttpRequest req) {
		getData(req).setAllowZero(allowZero);
	}

	/**
	 * Set the admissible interval types, such as years, months, days, etc.
	 * In particular, this restricts the minimum time period that can be
	 * selected.
	 * By default, all types are allowed (from milliseconds up to years).
	 * @param admissibleTypes
	 */
	public void setDefaultAdmissibleTypes(DurationFieldType[] admissibleTypes) {
		this.admissibleTypes = admissibleTypes;
	}

	/**
	 * @see #setAdmissibleTypes(DurationFieldType[], OgemaHttpRequest)
	 * @param req
	 * @return
	 */
	public DurationFieldType[] getAdmissibleTypes(OgemaHttpRequest req) {
		return getData(req).getAdmissibleTypes();
	}

	/**
	 * Set the admissible interval types, such as years, months, days, etc.
	 * In particular, this restricts the minimum time period that can be
	 * selected.
	 * By default, all types are allowed (from milliseconds up to years).
	 * @param admissibleTypes
	 * @param req
	 */
	public void setAdmissibleTypes(DurationFieldType[] admissibleTypes, OgemaHttpRequest req) {
		getData(req).setAdmissibleTypes(admissibleTypes);
	}

	/**
	 * Define the preselected time unit
	 * @param selectedType
	 */
	public void setDefaultSelectedType(DurationFieldType selectedType) {
		this.selectedType = selectedType;
	}

	/**
	 * Define the preselected time unit
	 * @param selectedType
	 * @param req
	 */
	public void setSelectedType(DurationFieldType selectedType, OgemaHttpRequest req) {
		getData(req).setSelectedType(selectedType);
	}

}
