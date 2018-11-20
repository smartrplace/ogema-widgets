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
package de.iwes.timeseries.eval.api.helper;

import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.EvaluationProvider;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.RedirectButton;

public abstract class EvalOpenButton extends RedirectButton {
	private static final long serialVersionUID = 1L;
	protected abstract EvaluationInstance startEvaluation();
	protected abstract EvaluationProvider getProvider();
	
	public EvalOpenButton(WidgetPage<?> page, String id, String text) {
		super(page, id, text);
	}
	
	@Override
	public void onGET(OgemaHttpRequest req) {
		if(getProvider() == null) {
			disable(req);
		} else {
			enable(req);
		}
	}
	@Override
	public void onPrePOST(String data, OgemaHttpRequest req) {
		EvaluationInstance eval = startEvaluation();
		int counter = 0;
		while(!eval.isDone() || counter > 10) try {
			Thread.sleep(1000);
			counter++;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String url = "https://localhost:8443/de/iwes/tools/timeseries/analysis/results.html?provider=";
		url += getProvider().id() + "&instance=" + eval.id();//"EnergyEvaluation_0";
		setUrl(url, req);
		
	}
}
