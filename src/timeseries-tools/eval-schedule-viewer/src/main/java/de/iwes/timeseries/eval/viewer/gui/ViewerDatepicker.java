package de.iwes.timeseries.eval.viewer.gui;

import java.util.List;

import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.viewer.gui.LabelledItemUtils.DataTree;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.calendar.datepicker.Datepicker;
import de.iwes.widgets.html.calendar.datepicker.DatepickerData;

class ViewerDatepicker extends Datepicker {

	private static final long serialVersionUID = 1L;
	private final boolean isStart;
	private final DataTree dataTree;

	public ViewerDatepicker(WidgetPage<?> page, String id, boolean isStart, DataTree dataTree) {
		super(page, id);
		this.isStart = isStart;
		this.dataTree = dataTree;
	}
	
	@Override
	public void onGET(OgemaHttpRequest req) {
		final List<? extends ReadOnlyTimeSeries> schedules = dataTree.getSelectedSchedules(req);
		long startTime = (isStart ? Long.MAX_VALUE: Long.MIN_VALUE);
		for (ReadOnlyTimeSeries sched: schedules) {
			SampledValue sv = (isStart ? sched.getNextValue(Long.MIN_VALUE): sched.getPreviousValue(Long.MAX_VALUE));
			if (sv != null) {
				long start0 = sv.getTimestamp();
				if ((isStart && start0 < startTime) || (!isStart && start0 > startTime)) 
					startTime = start0;
			}
		}
		if (isStart) {
			if (startTime == Long.MAX_VALUE)
				startTime = System.currentTimeMillis();
		} else {
			if (startTime == Long.MIN_VALUE)
				startTime = System.currentTimeMillis();
			if (startTime < Long.MAX_VALUE - 10000)
				startTime += 1001; // ensure all data points are really shown
		}
		setDate(startTime, req);
	}
	
	
	// TODO
	private static class ViewerDatepickerData extends DatepickerData {

		private boolean fixedInterval = false;
		
		public ViewerDatepickerData(ViewerDatepicker datepicker) {
			super(datepicker);
		}
		
	}
	

}
