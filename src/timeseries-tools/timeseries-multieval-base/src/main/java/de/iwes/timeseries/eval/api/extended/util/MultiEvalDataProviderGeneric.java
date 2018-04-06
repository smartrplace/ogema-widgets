package de.iwes.timeseries.eval.api.extended.util;

import java.util.List;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.extended.DataProviderResInfoGeneric;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.TerminalOption;

public abstract class MultiEvalDataProviderGeneric<R> implements DataProviderResInfoGeneric<R, TimeSeriesData> {
	public MultiEvalDataProviderGeneric(LinkingOption[] selectionOptions) {
		this.selectionOptions = selectionOptions;
	}

	protected LinkingOption[] selectionOptions;
	
	@Override
	public LinkingOption[] selectionOptions() {
		return selectionOptions;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TerminalOption<? extends ReadOnlyTimeSeries> getTerminalOption() {
		return (TerminalOption<? extends ReadOnlyTimeSeries>) selectionOptions[selectionOptions.length-1];
	}

	@Override
	public abstract EvaluationInput getData(List<SelectionItem> items);

	@Override
	public String id() {
		return "defaultInputForMultiEvaluations";
	}

	@Override
	public String label(OgemaLocale locale) {
		return "Default Input for MultiEvaluations";
	}

	@Override
	public String description(OgemaLocale locale) {
		return "This is default input for MultiEvaluations";
	}

	@Override
	public abstract R getResource(SelectionItem item);

}
