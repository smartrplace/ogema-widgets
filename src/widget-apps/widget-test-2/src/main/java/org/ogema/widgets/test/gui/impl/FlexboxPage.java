/**
 * ﻿Copyright 2014-2018 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ogema.widgets.test.gui.impl;

import org.ogema.core.application.ApplicationManager;
import org.osgi.service.component.annotations.Component;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.form.dropdown.EnumDropdown;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.html5.Flexbox;
import de.iwes.widgets.html.html5.flexbox.AlignContent;
import de.iwes.widgets.html.html5.flexbox.AlignItems;
import de.iwes.widgets.html.html5.flexbox.FlexDirection;
import de.iwes.widgets.html.html5.flexbox.FlexWrap;
import de.iwes.widgets.html.html5.flexbox.JustifyContent;

@Component(
		service=LazyWidgetPage.class,
		property= {
				LazyWidgetPage.BASE_URL + "=" + Constants.URL_BASE, 
				LazyWidgetPage.RELATIVE_URL + "=flexbox.html",
				LazyWidgetPage.START_PAGE + "=false",
				LazyWidgetPage.MENU_ENTRY + "=Flexbox page"
		}
)
public class FlexboxPage implements LazyWidgetPage {
	
	@Override
	public void init(ApplicationManager appMan, WidgetPage<?> page) {
		new FlexboxPageInit(page);
	}
	
	private static class FlexboxPageInit {
	
		private final WidgetPage<?> page;
		private final Header header;
		private final Alert info;
		
		private final Flexbox settingsFlexbox;
		private final EnumDropdown<JustifyContent> selectJustifyContent;
		private final EnumDropdown<AlignItems> selectAlignItems;
		private final EnumDropdown<FlexDirection> selectFlexDirection;
		private final EnumDropdown<FlexWrap> selectFlexWrap;
		private final EnumDropdown<AlignContent> selectAlignContent;
		private final TextField marginField;
		private final Flexbox flexibleFlexbox;
		
		private final Header subHeader;
		// we create several Flexboxes with different settings
		// justify content settings
		private final Flexbox box0;
		private final Flexbox box1;
		private final Flexbox box2;
		private final Flexbox box3;
		private final Flexbox box4;
		// flex-direction settings
		private final Flexbox box5;
		private final Flexbox box6;
		private final Flexbox box7;
		
		@SuppressWarnings("serial")
		FlexboxPageInit(final WidgetPage<?> page) {
			this.page = page;
			this.header = new Header(page, "header", "Flexbox page");
			this.header.setDefaultColor("blue");
			final String description = "This page illustrates the use of the <b>Flexbox</b> widget, with its different settings.<br>"
					+ "Change the settings in the dropdowns below to see how the arrangement of the boxes changes.<br>"
					+ "Change the browser size to understand the behaviour of the boxes at different scales.<br>"
					+ "For more information on the Html-Flexbox element see "
					+ "<a href=\"https://css-tricks.com/snippets/css/a-guide-to-flexbox/\">https://css-tricks.com/snippets/css/a-guide-to-flexbox/</a>";
			this.info = new Alert(page, "info", description);
			info.setDefaultTextAsHtml(true);
			info.addDefaultStyle(AlertData.BOOTSTRAP_INFO);
			
			this.selectJustifyContent = new EnumDropdown<>(page, "selectJustifyContent", JustifyContent.class);
			this.selectAlignItems = new EnumDropdown<>(page, "selectAlignItems", AlignItems.class);
			this.selectFlexDirection = new EnumDropdown<>(page, "selectFlexDirection", FlexDirection.class);
			this.selectFlexWrap = new EnumDropdown<>(page, "selectFlexWrap", FlexWrap.class);
			this.selectAlignContent = new EnumDropdown<>(page, "selectAlignContent", AlignContent.class);
			this.marginField = new TextField(page, "marginField", "0.5em");			
			
			this.flexibleFlexbox = new Flexbox(page, "flexibleFlexbox", false) {
				
				private final Label createLabel(final int idx, final OgemaHttpRequest req) {
					final Label l = new Label(this, "flexLab_" + getId() + "_" + idx, req);
					l.setDefaultText("This is box " + + idx);
					l.setDefaultMargin("0.5em");
					l.setDefaultPadding("0.5em");
					l.setDefaultColor("red");
					l.setDefaultBackgroundColor("orange");
					return l;
				}
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					if (getItems(req).isEmpty()) {
						for (int i=1; i < 5; i++)
							addItem(createLabel(i, req), req);
					}
					final String margin = marginField.getValue(req);
					for (OgemaWidget w : getItems(req)) {
						((Label) w).setMargin(margin, req);
					}
					setJustifyContent(selectJustifyContent.getSelectedItem(req), req);
					setAlignItems(selectAlignItems.getSelectedItem(req), req);
					setFlexDirection(selectFlexDirection.getSelectedItem(req), req);
					setFlexWrap(selectFlexWrap.getSelectedItem(req), req);
					setAlignContent(selectAlignContent.getSelectedItem(req), req);
				}
				
			};
			this.settingsFlexbox = new Flexbox(page, "settingsBox", true);
			settingsFlexbox.setDefaultJustifyContent(JustifyContent.SPACE_AROUND);
			settingsFlexbox.setFlexWrap(FlexWrap.WRAP, null);
			
			final Flexbox jcWrapper = getWrapper("Justify content", selectJustifyContent);
			final Flexbox fdWrapper = getWrapper("Flex direction", selectFlexDirection);
			final Flexbox aiWrapper = getWrapper("Align items", selectAlignItems);
			final Flexbox fwWrapper = getWrapper("Flex wrap", selectFlexWrap);
			final Flexbox acWrapper = getWrapper("Align content", selectAlignContent);
			final Flexbox marginWrapper = getWrapper("Boxes margin", marginField);
			
			settingsFlexbox.addItem(jcWrapper, null)
				.addItem(fdWrapper, null)
				.addItem(aiWrapper, null)
				.addItem(fwWrapper, null)
				.addItem(acWrapper, null)
				.addItem(marginWrapper, null);
			
			this.subHeader = new Header(page, "examplesHeader", "Examples");
			subHeader.setDefaultColor("blue");
			// smaller header size
			subHeader.setDefaultHeaderType(2);
			this.box0 = createBox(page, "box0");
			box0.setJustifyContent(JustifyContent.FLEX_LEFT, null);
			this.box1 = createBox(page, "box1");
			box1.setJustifyContent(JustifyContent.FLEX_RIGHT, null);
			this.box2 = createBox(page, "box2");
			box2.setJustifyContent(JustifyContent.CENTER, null);
			this.box3 = createBox(page, "box3");
			box3.setJustifyContent(JustifyContent.SPACE_AROUND, null);
			this.box4 = createBox(page, "box4");
			box4.setJustifyContent(JustifyContent.SPACE_BETWEEN, null);
			this.box5 = createBox(page, "box5");
			box5.setFlexDirection(FlexDirection.ROW_REVERSE, null);
			this.box6 = createBox(page, "box6");
			box6.setFlexDirection(FlexDirection.COLUMN, null);
			box6.setAlignItems(AlignItems.CENTER, null);
			this.box7 = createBox(page, "box7");
			box7.setFlexDirection(FlexDirection.COLUMN_REVERSE, null);
			box7.setAlignItems(AlignItems.FLEX_LEFT, null);
			
			buildPage();
			setDependencies();
		}
	
		private final void buildPage() {
			int row = 0;
			page.append(header).append(info).linebreak()
				.append(new StaticTable(2, 2, new int[] {2,10})
						.setContent(row, 0, "Select settings").setContent(row++, 1, settingsFlexbox)
						.setContent(row, 0, "Result").setContent(row++, 1, flexibleFlexbox)
				).linebreak()
				.append(subHeader).append(new StaticTable(8, 2, new int[] {2,10})
					.setContent(row=0, 0, "Justify content: FLEX_LEFT").setContent(row++, 1, box0)
					.setContent(row, 0, "Justify content: FLEX_RIGHT").setContent(row++, 1, box1)
					.setContent(row, 0, "Justify content: CENTER").setContent(row++, 1, box2)
					.setContent(row, 0, "Justify content: SPACE_AROUND").setContent(row++, 1, box3)
					.setContent(row, 0, "Justify content: SPACE_BETWEEN").setContent(row++, 1, box4)
					.setContent(row, 0, "Flex direction: ROW_REVERSE").setContent(row++, 1, box5)
					.setContent(row, 0, "Flex direction: COLUMN<br>Align items: CENTER").setContent(row++, 1, box6)
					.setContent(row, 0, "Flex direction: COLUMN_REVERSE<br>Align items: FLEX_LEFT").setContent(row++, 1, box7)
			);
		}
		
		private final void setDependencies() {
			selectAlignContent.triggerAction(flexibleFlexbox, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			selectAlignItems.triggerAction(flexibleFlexbox, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			selectJustifyContent.triggerAction(flexibleFlexbox, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			selectFlexDirection.triggerAction(flexibleFlexbox, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			selectFlexWrap.triggerAction(flexibleFlexbox, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			marginField.triggerAction(flexibleFlexbox, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		}
		
		private final Flexbox getWrapper(final String label, final OgemaWidgetBase<?> selector) {
			final String id = label.toLowerCase().replace(" ", "");
			final Flexbox jcWrapper = new Flexbox(page, "wrapper_" + id, true);
			jcWrapper.setFlexDirection(FlexDirection.COLUMN, null);
			jcWrapper.setAlignItems(AlignItems.FLEX_LEFT, null);
			final Label l = new Label(page, "label_" + id, label + ":");
			l.addDefaultCssStyle("font-weight", "bold");
			l.setDefaultColor("darkblue");
			jcWrapper.addItem(l, null);
			jcWrapper.addItem(selector, null);
			selector.setDefaultMargin("0.5em",true, false, true, true);
			return jcWrapper;
		}
		
		private final static Flexbox createBox(final WidgetPage<?> page, final String id) {
			final Flexbox flex = new Flexbox(page, id, true);
			for (int i=1; i < 5; i++) {
				final Label l = new Label(page, id + "_" + i, "This is box " + i);
				l.setDefaultBackgroundColor("lightgreen");
				l.setDefaultColor("darkgreen");
				l.addDefaultCssStyle("font-weight", "bold");
				// difference between padding and margin: the background color (lightgreen) will fill the padding, but not the margin
				l.setDefaultPadding("0.5em", false, true, false, true);
				l.setDefaultMargin("0.5em", false, true, false, true);
				l.setDefaultPadding("0.2em", true, false, true, false);
				l.setDefaultMargin("0.2em", true, false, true, false);
	//			l.setMargin("1em");
				// null as session argument is fine for global widgets (arg true in Flexbox constructor)
				flex.addItem(l, null);
			}
			return flex;
		}
	
	}
}