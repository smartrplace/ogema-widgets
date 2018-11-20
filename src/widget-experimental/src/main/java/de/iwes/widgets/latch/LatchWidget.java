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
package de.iwes.widgets.latch;

import java.util.concurrent.TimeUnit;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.emptywidget.EmptyData;
import de.iwes.widgets.html.emptywidget.EmptyWidget;

public class LatchWidget extends EmptyWidget {

	private static final long serialVersionUID = 1L;
	int defaultCount = -1;

	public LatchWidget(WidgetPage<?> page, String id) {
		super(page, id);
	}
	
	public LatchWidget(WidgetPage<?> page, String id, boolean globalWidget) {
		super(page, id, globalWidget);
	}
	
	public LatchWidget(OgemaWidget parent, String id, OgemaHttpRequest req) {
		super(parent, id, req);
	}
	
	@Override
	public LatchWidgetData createNewSession() {
		return new LatchWidgetData(this);
	}
	
	@Override
	public LatchWidgetData getData(OgemaHttpRequest req) {
		return (LatchWidgetData) super.getData(req);
	}
	
	@Override
	protected void setDefaultValues(EmptyData opt) {
		super.setDefaultValues(opt);
		LatchWidgetData opt2 = (LatchWidgetData) opt;
		if (defaultCount > 0) 
			opt2.reset(defaultCount);
		else
			opt2.reset(0);
	}
	
	public void setDefaultCount(int count) {
		this.defaultCount = count;
	}
	
	public void reset(int expectedCounts, OgemaHttpRequest req) {
		getData(req).reset(expectedCounts);
	}
	
	public void reset(int expectedCounts, final long timeout, final TimeUnit unit, OgemaHttpRequest req) {
		getData(req).reset(expectedCounts, timeout, unit);
	}

	
	public boolean await(long timeout, TimeUnit unit, OgemaHttpRequest req) throws InterruptedException {
		return getData(req).await(timeout, unit);
	}
	
	public void countDown(OgemaHttpRequest req) {
		getData(req).countDown();
	}
	
	public long getCount(OgemaHttpRequest req) {
		return getData(req).getCount();
	}
	
}
