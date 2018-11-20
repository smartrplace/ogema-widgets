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

import java.util.Collections;

import org.ogema.core.application.ApplicationManager;
import org.osgi.service.component.annotations.Component;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.filedownload.Download;
import de.iwes.widgets.html.filedownload.DownloadData;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.checkbox.Checkbox;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.textfield.TextField;

@Component(
		service=LazyWidgetPage.class,
		property= {
				LazyWidgetPage.BASE_URL + "=" + Constants.URL_BASE, 
				LazyWidgetPage.RELATIVE_URL + "=download.html",
				LazyWidgetPage.START_PAGE + "=false",
				LazyWidgetPage.MENU_ENTRY + "=Download page"
		}
)
public class DownloadPage implements LazyWidgetPage {
	
	@Override
	public void init(ApplicationManager appMan, WidgetPage<?> page) {
		new DownloadPageInit(page, appMan);
	}

	private static class DownloadPageInit {

		private final WidgetPage<?> page;
		private final Header header;
		private final TextField input;
		private final TextField filename;
		private final TextField contentType;
		private final Checkbox singleDownload;
		private final Checkbox forceDownload;
		private final Download download;
		private final Button button;

		DownloadPageInit(final WidgetPage<?> page, final ApplicationManager appMan) {
			this.page = page;
			this.header = new Header(page, "header", true);
			header.setDefaultText("Download page");
			header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
			header.setDefaultColor("blue");
			this.input = new TextField(page, "input");
			this.filename = new TextField(page, "filename");
			this.contentType = new TextField(page, "contentType") {

				private static final long serialVersionUID = 1L;

				@Override
				public void onGET(OgemaHttpRequest req) {
					final boolean forced = forceDownload.getCheckboxList(req).get("");
					if (forced) {
						setValue("application/octet-stream", req);
						disable(req);
					} else {
						enable(req);
					}
				}

			};
			contentType.setDefaultValue("text/plain");
			filename.setDefaultValue("example.txt");
			this.singleDownload = new Checkbox(page, "singleDownload");
			singleDownload.setDefaultList(Collections.singletonMap("", false));
			this.forceDownload = new Checkbox(page, "forceDownload");
			forceDownload.setDefaultList(Collections.singletonMap("", false));
			this.button = new Button(page, "downloadButton", "Download");
			this.download = new Download(page, "download", appMan) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onGET(OgemaHttpRequest req) {
					final String text = input.getValue(req);
					final String file = filename.getValue(req).trim();
					if (!file.isEmpty())
						setCustomFilename(file, req);
					final boolean reusable = !singleDownload.getCheckboxList(req).get("");
					final boolean forceDownloadVal = forceDownload.getCheckboxList(req).get("");
					setSource(text, reusable, contentType.getValue(req), req);
				}
			};
			buildPage();
			setDependencies();
		}

		private final void buildPage() {
			int row = 0;
			final StaticTable table = new StaticTable(6, 2, new int[] { 3, 3 }).setContent(row, 0, "Enter content")
					.setContent(row++, 1, input).setContent(row, 0, "File name").setContent(row++, 1, filename)
					.setContent(row, 0, "Content type").setContent(row++, 1, contentType)
					.setContent(row, 0, "Single download").setContent(row++, 1, singleDownload)
					.setContent(row, 0, "Force download").setContent(row++, 1, forceDownload)
					.setContent(row, 0, "Download file").setContent(row++, 1, button);
			page.append(header).linebreak().append(table).linebreak().append(download);
		}

		private final void setDependencies() {
			forceDownload.triggerAction(contentType, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			button.triggerAction(download, TriggeringAction.POST_REQUEST, DownloadData.GET_AND_STARTDOWNLOAD);
		}

	}
}
