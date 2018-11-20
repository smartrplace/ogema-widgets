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
package de.iwes.timeseries.eval.viz.gui;

import de.iwes.timeseries.eval.viz.gui.SourceSelectorPopup.InputDataSnippet;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.extended.html.bricks.PageSnippetData;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class ShadowContainerSnippet extends PageSnippet {

	private static final long serialVersionUID = 1L;

	public ShadowContainerSnippet(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
	}
	
	@Override
	public PageSnippetData createNewSession() {
		return new ShadowContainerSnippetData(this);
	}
	
	@Override
	public ShadowContainerSnippetData getData(OgemaHttpRequest req) {
		return (ShadowContainerSnippetData) super.getData(req);
	}
	
//	public TemplateMultiselect<SelectionItem> getSelector(OgemaHttpRequest req) {
//		return getData(req).multiselect;
//	}
//	
//	public void setSelector(TemplateMultiselect<SelectionItem> selector, OgemaHttpRequest req) {
//		getData(req).multiselect = selector;
//	}

	public void setShadowSnippet(InputDataSnippet inputSnippet, OgemaHttpRequest req) {
		getData(req).shadowSnippet = inputSnippet;
	}
	
	public InputDataSnippet getShadowSnippet(OgemaHttpRequest req) {
		return getData(req).shadowSnippet;
	}
	
	private static class ShadowContainerSnippetData extends PageSnippetData {
		
//		private TemplateMultiselect<SelectionItem> multiselect;
		private InputDataSnippet shadowSnippet;

		public ShadowContainerSnippetData(ShadowContainerSnippet snippet) {
			super(snippet);
		}
		
	}
	
	
}
