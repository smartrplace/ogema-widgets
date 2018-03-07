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

package de.iwes.widgets.html.schedulemanipulator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.ogema.core.channelmanager.measurements.BooleanValue;
import org.ogema.core.channelmanager.measurements.DoubleValue;
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.IntegerValue;
import org.ogema.core.channelmanager.measurements.LongValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.channelmanager.measurements.StringValue;
import org.ogema.core.channelmanager.measurements.Value;
import org.ogema.core.model.Resource;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.core.timeseries.TimeSeries;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetGroup;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.calendar.datepicker.Datepicker;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.ButtonData;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.dropdown.DropdownOption;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;

// FIXME changing the time leads to display error and BAD quality; date changing works -> ?
// FIXME adding session dependent widgets to widget group?
public class ScheduleRowTemplate extends RowTemplate<Long> {
	
//	private final static String NEW_LINE_ID = "___NEW_LINE___";
	private final static String DATE_FORMAT_JS = "YYYY-MM-DD HH:mm:ss";
	private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	// this is the DynamicTable widget
	private final OgemaWidget parent;
	// need this to trigger updates; will also trigger table update
	private final ScheduleManipulator scheduleManipulator;
	// note: the alert may be null
	private final Alert alert;
	private final boolean showQuality;
	private final WidgetGroup valueWidgets;
	private final static TriggeredAction SCHEDULE_CHANGED = new TriggeredAction("scheduleChanged");
	
	public ScheduleRowTemplate(OgemaWidget parent, ScheduleManipulator scheduleManipulator, WidgetGroup valueWidgets) {
		this(parent, scheduleManipulator, valueWidgets, null, false);
	}
	
	public ScheduleRowTemplate(OgemaWidget parent, ScheduleManipulator scheduleManipulator, WidgetGroup valueWidgets, Alert alert, boolean showQuality) {
		this.parent = parent;
		this.scheduleManipulator = scheduleManipulator;
		this.alert = alert;
		this.showQuality = showQuality;
		this.valueWidgets = valueWidgets;
	}
	
	@Override
	public Row addRow(final Long timestamp, OgemaHttpRequest req) {
		Row row = new Row();
		final String lineId = getLineId(timestamp);
		final boolean isNewLine = (timestamp.longValue() == ScheduleManipulatorData.NEW_LINE_ID);
		Label dpLabel = new Label(parent, lineId+ "_dpLabel","Timestamp: ",req);
		row.addCell("dpLabel",dpLabel,1);
		final Datepicker dp = new Datepicker(parent, lineId + "_datepicker",req) {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				if (isNewLine) return;
				long date = getDateLong(req);
				setNewTimestamp(timestamp, date, req);
			}
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				long time;
				if (!isNewLine) 
					time  = timestamp;  
				else {
					String date = getDate(req);
					if (date != null && !date.isEmpty()) // would be annoying to override the new value upon every GET
						return;
					time = System.currentTimeMillis();
				}
	 			Date date = new Date(time);
	 			SimpleDateFormat FORMAT = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
				setDate(FORMAT.format(date),req);  
			}
			
			
		};
		dp.setDefaultFormat(DATE_FORMAT_JS);
		valueWidgets.addWidget(dp);
		if (!isNewLine) 
			dp.triggerAction(scheduleManipulator, TriggeringAction.POST_REQUEST, SCHEDULE_CHANGED, req);  // note: also GET should be triggered -> SCHEDULE_CHANGED must trigger GET
/*		boolean timeStampFound = false;
		if (!lineId.equals(ScheduleManipulatorOptions.NEW_LINE_ID)) {
			long time  = Long.parseLong(lineId);  // make sure the value is of long type
 			Date date = new Date(time);
 			SimpleDateFormat FORMAT = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
			dp.setDefaultDate(FORMAT.format(date));  
			timeStampFound = true;
		}
*/
		row.addCell("dp",dp,2);
		Label tfLabel = new Label(parent, lineId+ "_tfLabel","Value: ",req);
		row.addCell("tfLabel",tfLabel,1);
		final TextField tf = new TextField(parent, lineId + "_tf",req) {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				if (isNewLine) return;
				String value = getValue(req);
				setNewValue(timestamp,value,req);
			}
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if (isNewLine) return;
				SampledValue sv = getSampledValue(timestamp, req);
				if (sv == null)
					return;
				Object value;
				if (isTemperatureSchedule(req)) {
					value = String.format(Locale.ENGLISH,"%.2f", sv.getValue().getFloatValue() - 273.15);
				} 
				else
					value =ScheduleRowTemplate.getValue(sv.getValue());
				setValue(value.toString(), req);
			}
			
			
		};
		valueWidgets.addWidget(tf);
		row.addCell("tf",tf,1);
		if (!isNewLine)
			tf.triggerAction(scheduleManipulator, TriggeringAction.POST_REQUEST, SCHEDULE_CHANGED, req);
		final Dropdown qualDropdown;
		if (showQuality) {
			Label qualLabel = new Label(parent, lineId+ "_qualLabel","Quality: ",req);
			row.addCell("qualLabel",qualLabel,1);
			
			Set<DropdownOption> options = new LinkedHashSet<DropdownOption>();
			options.add(new DropdownOption("GOOD","GOOD",true));
			options.add(new DropdownOption("BAD","BAD",false));
			qualDropdown = new Dropdown(parent, lineId + "_qualDropdown",options,req) {

				private static final long serialVersionUID = 1L;
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					if (isNewLine) return;
					SampledValue sv = getSampledValue(timestamp, req);
					if (sv == null)
						return;
					String quality = sv.getQuality().name();
					selectSingleOption(quality, req);
				}
				
				@Override
				public void onPOSTComplete(String data, OgemaHttpRequest req) {
					if (isNewLine) return;
					DropdownOption selected = getSelected(req);
					if (selected == null) return; // should not normally happen
					String value = selected.id();
					TimeSeries schedule = scheduleManipulator.getSchedule(req);
					if (schedule == null) 
						return;
//					long time = Long.parseLong(lineId);
					SampledValue sv = schedule.getValue(timestamp);
					if (sv == null) {
						String msg = "Trying to set quality of a non-existent schedule value. This should not happen";
						LoggerFactory.getLogger(ScheduleRowTemplate.class).warn(msg);
						showAlert(msg, false, req);
						return;
					}
					if (sv.getQuality().name().equals(value)) return; // nothing changed
					SampledValue svNew = new SampledValue(sv.getValue(),sv.getTimestamp(),Quality.valueOf(value));
					Collection<SampledValue> coll = new ArrayList<SampledValue>();
					coll.add(svNew);
					schedule.addValues(coll);
				}
				
				
			};
			if (!isNewLine)
				qualDropdown.triggerAction(scheduleManipulator, TriggeringAction.POST_REQUEST, SCHEDULE_CHANGED, req);
			valueWidgets.addWidget(qualDropdown);
			row.addCell("qualDropdown",qualDropdown,1);
		}
		else
			qualDropdown = null;
		
		if (isNewLine) {
			Button saveButton = new Button(parent, lineId + "_saveButton", req) {

				private static final long serialVersionUID = 1L;
				
				@Override
				public void onPrePOST(String data, OgemaHttpRequest req) {
					TimeSeries schedule = scheduleManipulator.getSchedule(req);
					if (schedule == null) {
						LoggerFactory.getLogger(ScheduleRowTemplate.class).warn("Trying to add a point to a null schedule");
						return;
					}
					String date = dp.getDate(req);
					String value = tf.getValue(req);
					if (date == null || value == null || date.isEmpty() || value.isEmpty()) {
						String msg = "Timestamp or value absent: time: " + date + "; value: " + value;
						LoggerFactory.getLogger(ScheduleRowTemplate.class).warn(msg);
						showAlert(msg, false, req);
						return;
					}
					long time;
					try {
						SimpleDateFormat FORMAT = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
						Date dt = FORMAT.parse(date);
						time = dt.getTime();
					} catch (Exception e) {
						LoggerFactory.getLogger(ScheduleRowTemplate.class).error("Date in wrong format",e);
						showAlert("Date in wrong format: " + e,false, req);
						return;
					}
					boolean added;
					try {
						added = schedule.addValue(time, getValue(schedule, value)); // TODO add quality -> needs API extension
					} catch (Exception e) {
						showAlert("Value format seems to be invalid: " + e, false, req);
						return;
					}
					String msg;
					if (added)
						msg  = "TimeSeries value for " +  new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(new Date(time)) + " has been added.";
					else
						msg = "TimeSeries value could not be added.";
					showAlert(msg, added, req);
				}
				
			};
			saveButton.triggerAction(scheduleManipulator, TriggeringAction.POST_REQUEST, SCHEDULE_CHANGED, req);
			
			row.addCell("deleteSaveBtn", saveButton, 1);
			saveButton.setText(" Add ",req);
			saveButton.setStyle(ButtonData.BOOTSTRAP_GREEN,req);
//			saveButton.triggerAction(scheduleManipulator.getId(), TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
			if (alert != null)
				saveButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
		}
		else {
			Button deleteButton = new Button(parent, lineId + "_deleteBtn", req) {

				private static final long serialVersionUID = 1L;
				
				@Override
				public void onPrePOST(String data, OgemaHttpRequest req) {
					TimeSeries schedule = scheduleManipulator.getSchedule(req);
					if (schedule == null) {
						LoggerFactory.getLogger(ScheduleRowTemplate.class).warn("Trying to delete a point from a null schedule");
						return;
					}
					boolean deleted = schedule.deleteValues(timestamp, timestamp+1);
					String msg;
					if (deleted)
						msg = "TimeSeries value for " +  new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(new Date(timestamp)) + " has been deleted.";
					else
						msg = "TimeSeries value could not be deleted.";
					showAlert(msg, deleted, req);
				}
				
			};
			
			deleteButton.triggerAction(scheduleManipulator, TriggeringAction.POST_REQUEST, SCHEDULE_CHANGED, req);
			
			deleteButton.setText("Delete",req);
			deleteButton.setStyle(ButtonData.BOOTSTRAP_RED,req);
//			deleteButton.triggerAction(scheduleManipulator.getId(), TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
			if (alert != null)
				deleteButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
			row.addCell("deleteSaveBtn", deleteButton, 1);
		}
		return row;
	}
	
	@Override
	public String getLineId(Long timestamp) {
		if (timestamp == null)
			return "_" + ScheduleManipulatorData.NEW_LINE_ID; 
		return "_" + timestamp;
	}
	
	private static Value getValue(TimeSeries schedule, String value) {
		if (!(schedule instanceof Schedule))
			return new FloatValue(Float.parseFloat(value));
		Resource res = ((Schedule) schedule).getParent();
		if (res == null) 
			return null;
		else if (res instanceof TemperatureResource) 
			return new FloatValue(Float.parseFloat(value) + 273.15F);
		else if (res instanceof FloatResource)
			return new FloatValue(Float.parseFloat(value));
		else if (res instanceof IntegerResource)
			return new IntegerValue(Integer.parseInt(value));
		else if (res instanceof TimeResource) 
			return new LongValue(Long.parseLong(value));
		else if (res instanceof BooleanResource)
			return new BooleanValue(Boolean.parseBoolean(value));
		else 
			throw new IllegalArgumentException("Resource type " + res.getResourceType().getSimpleName());
	}
	
	private void showAlert(String msg, boolean success, OgemaHttpRequest req) {
		if (alert == null) return;
		alert.setText(msg, req);
		alert.setWidgetVisibility(true, req);
		alert.allowDismiss(true, req);
		alert.autoDismiss(6000, req);
		alert.setStyle((success ? AlertData.BOOTSTRAP_SUCCESS : AlertData.BOOTSTRAP_DANGER), req);
	}
	
	// FIXME
	@Override
	public Map<String, Object> getHeader() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 ************************** internal methods ***********************/
	
	private void setNewValue(long time, String value, OgemaHttpRequest req) {
		TimeSeries schedule = scheduleManipulator.getSchedule(req);
		if (schedule == null) 
			return;
		SampledValue sv = schedule.getValue(time);
		if (sv == null) 
			return;
		Value valueObj = null;
		boolean isTemp = false;
		if (schedule instanceof Schedule) {
			isTemp = ((Schedule) schedule).getParent() instanceof TemperatureResource;
		}
		try {			
			valueObj = getValue(sv.getValue(), value, isTemp);
		} catch (Exception e) {}
		if (valueObj == null)
			return;
 		SampledValue newSv = new SampledValue(valueObj ,sv.getTimestamp(),sv.getQuality());
		schedule.deleteValues(time, time+1);
		Collection<SampledValue> coll = new ArrayList<SampledValue>();
		coll.add(newSv);
		schedule.addValues(coll);
	}
	
	private static Value getValue(Value old, String newValue, boolean isTemp) {
		Value value;
		if (isTemp) {
			float fl = Float.parseFloat(newValue) + 273.15F;
			value = new FloatValue(fl);
		}
		else if (old instanceof FloatValue) {
			float fl = Float.parseFloat(newValue);
			value = new FloatValue(fl);
		}
		else if (old instanceof IntegerValue) {
			int it = Integer.parseInt(newValue);
			value = new IntegerValue(it);
		}
		else if (old instanceof BooleanValue) {
			boolean bl = Boolean.parseBoolean(newValue);
			value = new BooleanValue(bl);
		}
		else if (old instanceof LongValue) {
			long lg = Long.parseLong(newValue);
			value = new LongValue(lg);
		}
		else 
			throw new IllegalArgumentException("Cannot handle value type " + old.getClass().getSimpleName());
		return value;
	}
	
	private void setNewTimestamp(long time, long timeNew, OgemaHttpRequest req) {
		TimeSeries schedule = scheduleManipulator.getSchedule(req);
		if (schedule == null) 
			return;
		SampledValue sv = schedule.getValue(time);
		if (sv == null) 
			return;
		SampledValue newSv = new SampledValue(sv.getValue(),timeNew,sv.getQuality());
		schedule.deleteValues(time, time+1);
		Collection<SampledValue> coll = new ArrayList<SampledValue>();
		coll.add(newSv);
		schedule.addValues(coll);
	}
	
	private SampledValue getSampledValue(long time, OgemaHttpRequest req) {
		TimeSeries schedule = scheduleManipulator.getSchedule(req);
		if (schedule == null) return null;
		return schedule.getValue(time);
	}
	
	private boolean isTemperatureSchedule(OgemaHttpRequest req) {
		TimeSeries schedule = scheduleManipulator.getSchedule(req);
		if (schedule == null || !(schedule instanceof Schedule)) return false;
		Resource res = ((Schedule) schedule).getParent();
		if (res == null) 
			return false;
		else if (res instanceof TemperatureResource) 
			return true;
		else 
			return false;
	}

	private static Object getValue(Value value) {
		if (value instanceof FloatValue || value instanceof DoubleValue)
			return value.getFloatValue();
		else if (value instanceof IntegerValue || value instanceof LongValue)
			return value.getLongValue();
		else if (value instanceof BooleanValue)
			return value.getBooleanValue();
		else if (value instanceof StringValue)
			return value.getStringValue();
		else 
			throw new IllegalArgumentException("Cannot handle value of type " + value.getClass());
	}
	
}
