package de.iwes.timeseries.eval.api.extended.util;

import org.ogema.serialization.jaxb.Resource;

public abstract class HierarchySelectionItem extends HierarchySelectionItemGeneric<Resource> {

	public HierarchySelectionItem(int level, String id) {
		super(level , id);
	}

}
