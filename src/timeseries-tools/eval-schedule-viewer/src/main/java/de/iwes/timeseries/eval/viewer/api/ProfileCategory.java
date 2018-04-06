package de.iwes.timeseries.eval.viewer.api;

import java.util.List;

import de.iwes.timeseries.eval.api.LabelledItem;

public interface ProfileCategory extends LabelledItem {

	List<Profile> getProfiles();
	
}
