package de.iwes.timeseries.eval.api.extended.util;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOptionType;

public class GenericLinkingOptionType  extends LinkingOptionType {
	private final String id;
	private final String label;
	private final LinkingOptionType[] dependencies;
	
	@Override
	public String id() {
		return id;
	}

	@Override
	public String label(OgemaLocale locale) {
		return label;
	}

	@Override
	public LinkingOptionType[] dependencies() {
		return dependencies;
	}

	public GenericLinkingOptionType(String id, String label, LinkingOptionType[] dependencies) {
		super();
		this.id = id;
		this.label = label;
		this.dependencies = dependencies;
	}
}
