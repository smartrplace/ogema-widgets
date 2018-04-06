package de.iwes.timeseries.eval.viewer.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.api.LabelledItem;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.SelectionTree;
import de.iwes.widgets.html.selectiontree.TerminalOption;
import de.iwes.widgets.listlabel.ListLabel;
import de.iwes.widgets.template.DisplayTemplate;

/**
 * A set of widgets (dropdown, multiselect, label) specific for LabelledItems, such 
 * as DataProvider or EvaluationProvider.
 * Copied from eval viz app
 */
public class LabelledItemUtils {
	
	/**Provides objects of a certain class/interface of LabelledItem that shall be offered to the user
	 * for selection
	 */
	public static interface LabelledItemProvider<T extends LabelledItem> {
		
		int getRevision();
		List<T> getItems();
		
	}
	
	/**Template for Dropdowns or Multiselects offering choices for a certain type of LabelledItem*/
	public static DisplayTemplate<LabelledItem> LABELLED_ITEM_TEMPLATE = new DisplayTemplate<LabelledItem>() {

		@Override
		public String getId(LabelledItem object) {
			return object.id();
		}

		@Override
		public String getLabel(LabelledItem object, OgemaLocale locale) {
			return object.label(locale);
		}
	};

	/**Dropdown allowing to select a LabelledItem from a provider*/
	public static class LabelledItemSelectorSingle<T extends LabelledItem> extends TemplateDropdown<LabelledItem> {

		private static final long serialVersionUID = 1L;
		private final LabelledItemProvider<T> provider;

		public LabelledItemSelectorSingle(WidgetPage<?> page, String id, LabelledItemProvider<T> provider) {
			super(page, id);
			this.provider = provider;
			setTemplate(LABELLED_ITEM_TEMPLATE);
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			update(provider.getItems(), req);
		}
		
	}
	
	/**Multiselect allowing to select several LabelledItems from a provider*/
	public static class LabelledItemSelectorMulti<T extends LabelledItem> extends TemplateMultiselect<LabelledItem> {

		private static final long serialVersionUID = 1L;
		private final LabelledItemProvider<T> provider;

		public LabelledItemSelectorMulti(WidgetPage<?> page, String id, LabelledItemProvider<T> provider) {
			super(page, id);
			this.provider = provider;
			setTemplate(LABELLED_ITEM_TEMPLATE);
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void onGET(OgemaHttpRequest req) {
			update((Collection) provider.getItems(), req);
		}
		
	}
	
	/** TODO: This is a label for a LabelledItem, not for a LabelledItemProvider*/
	public static class ProviderLabel extends Label {
		
		private static final long serialVersionUID = 1L;
		private final LabelledItem provider;
		private final boolean labelOrDescription;

		public ProviderLabel(WidgetPage<?> page, String id, LabelledItem provider, boolean labelOrDescription) {
			super(page, id);
			this.provider = provider;
			this.labelOrDescription = labelOrDescription;
		}
		
		public ProviderLabel(OgemaWidget parent, String id, OgemaHttpRequest req, LabelledItem provider, boolean labelOrDescription) {
			super(parent, id, req);
			this.provider = provider;
			this.labelOrDescription = labelOrDescription;
		}

		@Override
		public void onGET(OgemaHttpRequest req) {
			setText(labelOrDescription ? provider.label(req.getLocale()) : provider.description(req.getLocale()),req);
		}
		
	}
	
	/** TODO: This is a label for a LabelledItem, not for a LabelledItemProvider*/
	public static class MutableProviderLabel<T extends LabelledItem> extends Label {
		
		private static final long serialVersionUID = 1L;
		private final LabelledItemSelectorSingle<T> selector;
		private final boolean labelOrDescription;

		public MutableProviderLabel(WidgetPage<?> page, String id, LabelledItemSelectorSingle<T> selector, boolean labelOrDescription) {
			super(page, id);
			this.selector = selector;
			this.labelOrDescription = labelOrDescription;
		}

		@Override
		public void onGET(OgemaHttpRequest req) {
			@SuppressWarnings("unchecked")
			final T item = (T) selector.getSelectedItem(req);
			if (item == null) {
				setText("", req);
				return;
			}
			setText(labelOrDescription ? item.label(req.getLocale()) : item.description(req.getLocale()),req);
		}
		
	}
	
	public static class ItemsListLabel extends ListLabel {

		private static final long serialVersionUID = 1L;
//		private final List<? extends LabelledItem> items;
		
		public ItemsListLabel(WidgetPage<?> page, String id, List<? extends LabelledItem> items) {
			super(page, id, true);
//			this.items = items
			final List<String> list = new ArrayList<>();
			for (LabelledItem l : items) {
				list.add(l.label(OgemaLocale.ENGLISH));
			}
			setDefaultValues(list);
		}
		
		public ItemsListLabel(OgemaWidget parent, String id, OgemaHttpRequest req, List<? extends LabelledItem> items) {
			super(parent, id, req);
			final List<String> list = new ArrayList<>();
			for (LabelledItem l : items) {
				list.add(l.label(req.getLocale()));
			}
			setValues(list,req);
		}
		
//		@Override
//		public void onGET(OgemaHttpRequest req) {
//			setText(getListHtml(items, req.getLocale()), req);
//		}
		
	}
	
	
//	public static String getListHtml(final List<? extends LabelledItem> items, final OgemaLocale locale) {
//		final StringBuilder sb = new StringBuilder("<ul style=\"list-style-position: inside;\">");
//		for (LabelledItem item : items) {
//			sb.append("<li>").append(item.label(locale));
//		}
//		sb.append("</ul>");
//		return sb.toString();
//	}
	
	public static class DataTree extends SelectionTree {
		
		private static final long serialVersionUID = 1L;
		private final LabelledItemSelectorSingle<DataProvider<?>> selector;

		public DataTree(WidgetPage<?> page, String id, final LabelledItemSelectorSingle<DataProvider<?>> selector) {
			super(page, id, false);
			this.selector = selector;
		}
		
		@Override
		public void onGET(OgemaHttpRequest req) {
			final DataProvider<?> provider = (DataProvider<?>) selector.getSelectedItem(req);
			final List<LinkingOption> options = (provider == null) ? Collections.<LinkingOption> emptyList() :
					Arrays.asList(provider.selectionOptions());
			setSelectionOptions(options, req);
		}
		
		public List<ReadOnlyTimeSeries> getSchedules(OgemaHttpRequest req) {
			@SuppressWarnings("unchecked")
			final TemplateMultiselect<SelectionItem> selector = (TemplateMultiselect<SelectionItem>) getTerminalSelectWidget(req);
			if (selector == null)
				return Collections.emptyList();
			@SuppressWarnings("unchecked")
			final TerminalOption<ReadOnlyTimeSeries> terminalOpt = (TerminalOption<ReadOnlyTimeSeries>) getTerminalOption(req);
			if (terminalOpt == null)
				return Collections.emptyList();
			final List<ReadOnlyTimeSeries> list = new ArrayList<>();
			for (SelectionItem item : selector.getItems(req)) {
				list.add(terminalOpt.getElement(item));
			}
			return list;
		}
		
		public List<ReadOnlyTimeSeries> getSelectedSchedules(OgemaHttpRequest req) {
			@SuppressWarnings("unchecked")
			final TemplateMultiselect<SelectionItem> selector = (TemplateMultiselect<SelectionItem>) getTerminalSelectWidget(req);
			if (selector == null)
				return Collections.emptyList();
			@SuppressWarnings("unchecked")
			final TerminalOption<ReadOnlyTimeSeries> terminalOpt = (TerminalOption<ReadOnlyTimeSeries>) getTerminalOption(req);
			if (terminalOpt == null)
				return Collections.emptyList();
			final List<ReadOnlyTimeSeries> list = new ArrayList<>();
			for (SelectionItem item : selector.getSelectedItems(req)) {
				list.add(terminalOpt.getElement(item));
			}
			return list;
		}
		
		public void selectSchedules(final Collection<ReadOnlyTimeSeries> schedules, final OgemaHttpRequest req) {
			@SuppressWarnings("unchecked")
			final TemplateMultiselect<SelectionItem> selector = (TemplateMultiselect<SelectionItem>) getTerminalSelectWidget(req);
			if (selector == null)
				return;
			@SuppressWarnings("unchecked")
			final TerminalOption<ReadOnlyTimeSeries> terminalOpt = (TerminalOption<ReadOnlyTimeSeries>) getTerminalOption(req);
			final List<SelectionItem> allItems = selector.getItems(req);
			final List<SelectionItem> toBeSelected = new ArrayList<>();
			for (SelectionItem item : allItems) {
				final ReadOnlyTimeSeries ts = terminalOpt.getElement(item);
				for (ReadOnlyTimeSeries s : schedules) {
					if (s.equals(ts)) {
						toBeSelected.add(item);
						break;
					}
				}
			}
			selector.selectItems(toBeSelected, req);
		}
		
	}
	
}
