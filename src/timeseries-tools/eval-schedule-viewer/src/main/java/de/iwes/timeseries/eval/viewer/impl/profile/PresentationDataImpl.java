package de.iwes.timeseries.eval.viewer.impl.profile;

import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.viewer.api.Profile;
import de.iwes.timeseries.eval.viewer.api.ProfileSchedulePresentationData;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeries;
import de.iwes.widgets.reswidget.scheduleviewer.DefaultSchedulePresentationData;

public class PresentationDataImpl extends DefaultSchedulePresentationData implements ProfileSchedulePresentationData {

	private final Profile profile;
	private final boolean online;
	
	public PresentationDataImpl(ReadOnlyTimeSeries schedule, Class<?> type, String label, InterpolationMode mode, Profile profile) {
		super(schedule, type, label, mode);
		this.profile = profile;
		this.online = rots instanceof OnlineTimeSeries;
	}
	
	public PresentationDataImpl(ReadOnlyTimeSeries schedule, Class<?> type, String label, InterpolationMode mode, Profile profile, boolean online) {
		super(schedule, type, label, mode);
		this.profile = profile;
		this.online = online;
	}
	
	/**
	 * May be null
	 */
	@Override
	public Profile getProfile() {
		return profile;
	}
	
	@Override
	public boolean isOnlineTimeSeries() {
		return online;
	}

}
