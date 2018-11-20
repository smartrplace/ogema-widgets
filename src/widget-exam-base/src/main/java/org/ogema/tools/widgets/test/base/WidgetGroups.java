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
package org.ogema.tools.widgets.test.base;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;
import org.ogema.tools.widgets.test.base.util.RequestFuture;

public class WidgetGroups {

	private final WidgetLoader widgetLoader;
	
	public WidgetGroups(WidgetLoader loader) {
		this.widgetLoader = loader;
	}
	
	// TODO
	public Future<JSONObject> sendGET(String groupId) {
//		HttpGet get = new HttpGet(widgetLoader.getGroupUrl(groupId));
//		Future<HttpResponse> future = widgetLoader.asyncClient.execute(get, new GetRequestCallback());
//		RequestFuture result = new RequestFuture(future);
//		return result;
		return null;
	}
	
	public Future<JSONObject> sendPOST(String groupId) {
//		HttpGet get = new HttpGet(widgetLoader.getGroupUrl(groupId));
//		Future<HttpResponse> future = widgetLoader.asyncClient.execute(get, new GetRequestCallback());
//		RequestFuture result = new RequestFuture(future);
//		return result;
		return null;
	}
	
}
