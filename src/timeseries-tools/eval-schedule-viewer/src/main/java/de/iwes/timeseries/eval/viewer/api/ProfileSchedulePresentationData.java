package de.iwes.timeseries.eval.viewer.api;

import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;

public interface ProfileSchedulePresentationData extends SchedulePresentationData {

	Profile getProfile();
	boolean isOnlineTimeSeries();
	
}
