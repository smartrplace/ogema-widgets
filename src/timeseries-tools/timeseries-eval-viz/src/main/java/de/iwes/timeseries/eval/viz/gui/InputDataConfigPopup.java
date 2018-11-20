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
package de.iwes.timeseries.eval.viz.gui;

import java.util.List;

import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.viz.gui.SourceSelectorPopup.InputDataSnippet;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.dropdown.DropdownData;
import de.iwes.widgets.html.form.dropdown.EnumDropdown;
import de.iwes.widgets.html.form.dropdown.TemplateDropdownData;
import de.iwes.widgets.html.popup.Popup;

// TODO add
//	* offset, factor, time offset per schedule group
class InputDataConfigPopup extends Popup {

	private static final long serialVersionUID = 1L;
	private final InputDataSnippet inputSnippet;
	final ModeDropdown modesDropdown;
	private final PageSnippet bodySnippet;
	private final Button closeButton;

	public InputDataConfigPopup(WidgetPage<?> page, String id, String title, InputDataSnippet inputSnippet) {
		super(page, id, title, true);
		this.inputSnippet = inputSnippet;
		this.modesDropdown = new ModeDropdown(page, id + "_modes", inputSnippet);
		modesDropdown.setDefaultAddEmptyOption(true, "As in schedules"); // FIXME ?
		this.bodySnippet = new PageSnippet(page, id + "_body", true);
		this.closeButton = new Button(page, id+ "_close", "Close");
		buildWidget();
		setDependencies();
	}
	
	private final void setDependencies() {
		this.triggerAction(modesDropdown, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		closeButton.triggerAction(this, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
	}
	
	private final void buildWidget() {
		final StaticTable tab= new StaticTable(2, 2)
			.setContent(0, 0, "Select interpolation mode").setContent(0,1, modesDropdown);
		bodySnippet.append(tab, null);
		this.setBody(bodySnippet, null);
		this.setTitle("Input data configuration: " + inputSnippet.getInputData().label(OgemaLocale.ENGLISH), null);
		this.setFooter(closeButton, null);
	}
	
	public InputDataSnippet getInputSnippet() {
		return inputSnippet;
	}
	
	static class ModeDropdown extends EnumDropdown<InterpolationMode> {
		
		private static final long serialVersionUID = 1L;
		private final InputDataSnippet inputSnippet;

		public ModeDropdown(WidgetPage<?> page, String id, InputDataSnippet inputSnippet) {
			super(page, id, InterpolationMode.class);
			this.inputSnippet = inputSnippet;
		}
		
		@Override
		public TemplateDropdownData<InterpolationMode> createNewSession() {
			return new ModeDropdownData(this);
		}
	
		public boolean hasNoneMode(OgemaHttpRequest req) {
			return ((ModeDropdownData) getData(req)).hasNoneMode;
		}
		
		public boolean hasDifferentModes(OgemaHttpRequest req) {
			return ((ModeDropdownData) getData(req)).hasDifferentModes;
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			final List<ReadOnlyTimeSeries> schedules = inputSnippet.getSelectedSchedules(req);
			InterpolationMode mode = null;
			boolean hasDifferentModes = false;
			boolean hasNoneMode = false;
			for (ReadOnlyTimeSeries schedule :schedules) {
				InterpolationMode modeL = schedule.getInterpolationMode();
				if (modeL == InterpolationMode.NONE)
					hasNoneMode = true;
				if (hasDifferentModes) {
					if (hasNoneMode)
						break;
					continue;
				}
				if (mode == null)
					mode = modeL;
				else if (modeL != mode) {
					hasDifferentModes = true;
					setAddEmptyOption(true, "As in schedules (different modes)", req);
					selectSingleOption(DropdownData.EMPTY_OPT_ID, req);
					mode = null;
				}
			}
			if (mode != null) {
				setAddEmptyOption(false, req);
				selectItem(mode, req);
			}
			((ModeDropdownData) getData(req)).hasNoneMode = hasNoneMode;
			((ModeDropdownData) getData(req)).hasDifferentModes = hasDifferentModes;
		}
		
		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			final InterpolationMode mode = getSelectedItem(req);
			if (mode == InterpolationMode.NONE) 
				((ModeDropdownData) getData(req)).hasNoneMode = true;
			else if (mode != null)
				((ModeDropdownData) getData(req)).hasNoneMode = false;
		}
		
	}
	
	private static class ModeDropdownData extends TemplateDropdownData<InterpolationMode> {
		
		private boolean hasNoneMode = false;
		private boolean hasDifferentModes = false;

		public ModeDropdownData(ModeDropdown dropdown) {
			super(dropdown);
		}
		
	}
	
}
