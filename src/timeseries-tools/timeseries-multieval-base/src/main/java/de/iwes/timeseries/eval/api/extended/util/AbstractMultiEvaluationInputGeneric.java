package de.iwes.timeseries.eval.api.extended.util;

import de.iwes.timeseries.eval.api.DataProviderType;
import de.iwes.timeseries.eval.api.extended.DataProviderResInfoGeneric;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationItemSelector;
import de.iwes.widgets.html.selectiontree.LinkingOptionType;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public class AbstractMultiEvaluationInputGeneric<R> implements MultiEvaluationInputGeneric<R> {
	protected final DataProviderType type;
	protected final DataProviderResInfoGeneric<R, ?> dataProvider;
	
	@Override
	public DataProviderType type() {
		return type;
	}

	@Override
	public DataProviderResInfoGeneric<R, ?> dataProvider() {
		return dataProvider;
	}

	public AbstractMultiEvaluationInputGeneric(DataProviderType type, DataProviderResInfoGeneric<R, ?> dataProvider) {
		this.type = type;
		this.dataProvider = dataProvider;
	}

	/** !! This constructor is only to be used by the JSON Deserialization, not for other purposes !!*/
	public AbstractMultiEvaluationInputGeneric() {
		this.type = null;
		this.dataProvider = null;
	}

	@Override
	public MultiEvaluationItemSelector itemSelector() {
		return new MultiEvaluationItemSelector() {
			
			@Override
			public boolean useDataProviderItem(LinkingOptionType linkingOptionType, SelectionItem item) {
				return true;
			}
		};
	}

}
