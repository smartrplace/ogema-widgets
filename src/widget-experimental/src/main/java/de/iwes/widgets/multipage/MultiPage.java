/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */
package de.iwes.widgets.multipage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.PageSnippetI;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.template.PageSnippetTemplate;

public class MultiPage<T> extends PageSnippet {

	private static final long serialVersionUID = 1L;
	private final List<PageSnippetI> subPages;
	private final List<NavigationButton> buttons;

	public MultiPage(WidgetPage<?> page, String id, PageSnippetTemplate<T> template, List<T> objects) {
		super(page, id, true);
		Objects.requireNonNull(objects);
		if (objects.isEmpty())
			throw new IllegalArgumentException("Items list must not be empty");
		final List<PageSnippetI> subPages = new ArrayList<>();
		for (T o: objects) {
			final PageSnippetI snippet = template.getSnippet(o, null);
			subPages.add(snippet);
			snippet.setDefaultVisibility(false);
			append(snippet, null);
		}
		this.subPages = Collections.unmodifiableList(subPages);
		this.buttons = Collections.unmodifiableList(addNavigationButtons());
	}
	
	private final List<NavigationButton> addNavigationButtons() {
		final List<NavigationButton> buttons = new ArrayList<>();
		final Iterator<PageSnippetI> it = subPages.iterator();
		PageSnippetI last = null;
		PageSnippetI current = it.next();
		do {
			if (last != null) {
				final NavigationButton backButton = new NavigationButton(getPage(), current.getId() + "_backBtn", true, last, current);
				current.append(backButton, null); // TODO layout
				buttons.add(backButton);
			}
			final PageSnippetI next = it.hasNext() ? it.next() : null;
			if (next != null) {
				final NavigationButton forthButton = new NavigationButton(getPage(), current.getId() + "_forthBtn", false, next, current);
				current.append(forthButton, null); // TODO layout
				buttons.add(forthButton);
			}
			last = current;
			current = next;
		} while (current != null);
		return buttons;
	}
	
	public void triggerInitialState(OgemaWidget governor) {
		final Iterator<PageSnippetI> it = subPages.iterator();
		governor.triggerAction(it.next(), TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		while (it.hasNext()) {
			governor.triggerAction(it.next(), TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		}
	}
	
	public List<PageSnippetI> getSubPages() {
		return subPages;
	}
	
	public List<NavigationButton> getNavigationButtons() {
		return buttons;
	}
	
	public PageSnippetI getTerminalSnippet() {
		return subPages.get(subPages.size()-1);
	}
	
	public PageSnippetI getStartSnippet() {
		return subPages.get(0);
	}
	
	/**
	 * Like {@link OgemaWidget#addDefaultStyle(WidgetStyle)}, but applied to
	 * all navigation buttons.
	 * @param style
	 */
	public void addDefaultButtonStyle(WidgetStyle<?> style) {
		for (NavigationButton b: buttons) {
			b.addDefaultStyle(style);
		}
	}

	public static class NavigationButton extends Button {

		private static final long serialVersionUID = 1L;
		private final PageSnippetI target;
		private final PageSnippetI current;
		
		public NavigationButton(WidgetPage<?> page, String id, boolean backOrForth, PageSnippetI target, PageSnippetI current) {
			super(page, id, backOrForth ? "Back" : "Next", true);
			this.target = target;
			this.current = current;
			triggerAction(current, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
			triggerAction(target, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		}
		
		@Override
		public void onPOSTComplete(String data, OgemaHttpRequest req) {
			target.setWidgetVisibility(true, req);
			current.setWidgetVisibility(false, req);
		}
		
	}
	
}
