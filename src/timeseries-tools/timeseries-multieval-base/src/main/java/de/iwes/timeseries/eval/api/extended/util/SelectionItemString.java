package de.iwes.timeseries.eval.api.extended.util;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public class SelectionItemString implements SelectionItem {
	public String value;
	
	@Override
	public String id() {
		return value;
	}

	@Override
	public String label(OgemaLocale locale) {
		return value;
	}

	public SelectionItemString(String value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SelectionItemString)
			return value.equals(((SelectionItemString)obj).value);
		return value.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return value.hashCode();
	}
}
