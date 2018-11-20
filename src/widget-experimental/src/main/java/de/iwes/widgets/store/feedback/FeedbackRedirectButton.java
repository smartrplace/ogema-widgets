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
package de.iwes.widgets.store.feedback;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.RedirectButton;

/**
 * @deprecated does not belong here
 */
@Deprecated
public class FeedbackRedirectButton extends RedirectButton {
	private static final long serialVersionUID = 1L;
	private final String appId;
	private final boolean useGlyphicons;

	public FeedbackRedirectButton(WidgetPage<?> page, String id, String appId) {
		this(page, id, "Feedback", appId, true);
	}
	public FeedbackRedirectButton(WidgetPage<?> page, String id, String text, String appId) {
		this(page, id, text, appId, true);
	}
	public FeedbackRedirectButton(WidgetPage<?> page, String id, String text, String appId,
			boolean useGlyphicons) {
		super(page, id, text);
		this.appId = appId;
		this.useGlyphicons = useGlyphicons;
	}
	
	public FeedbackRedirectButton(OgemaWidget parent, String id, String appId,
			OgemaHttpRequest req) {
		this(parent, id,  "Feedback", appId, true, req);
	}
	public FeedbackRedirectButton(OgemaWidget parent, String id, String text, String appId,
			OgemaHttpRequest req) {
		this(parent, id,  text, appId, true, req);
	}
	public FeedbackRedirectButton(OgemaWidget parent, String id, String text, String appId,
			boolean useGlyphicons, OgemaHttpRequest req) {
		super(parent, id, text, "", req);
		this.appId = appId;
		this.useGlyphicons = useGlyphicons;
	}
	
	public void setInitialCommentText(String text, OgemaHttpRequest req) {
		addParameter("initialText", text, req);
	}
	public void setDefaultInitialCommentText(String text) {
		Map<String, String[]> parameters = new HashMap<>();
		parameters.put("initialText", new String[] {text});
		setDefaultParameters(parameters);
	}
	
	@Override
	public void onGET(OgemaHttpRequest req) {
		if(useGlyphicons)  setGlyphicon("glyphicon glyphicon-comment", req);
//		setCss(buttonStyle, req);
	}

	@Override
	public void onPrePOST(String data, OgemaHttpRequest req) {
		String dest = AccessController.doPrivileged(new PrivilegedAction<String>() {
			public String run() {
				return "https://" + System.getProperty("org.smartrplace.store.viewer.host", "localhost") 
				+ ":" + System.getProperty("org.smartrplace.store.viewer.port", "8510") + "/smartrplace/appstore/details.html";
			}
		});
		setUrl(dest, req);
		setParameters(Collections.singletonMap("configId", new String[]{appId}),req);
	}


}
