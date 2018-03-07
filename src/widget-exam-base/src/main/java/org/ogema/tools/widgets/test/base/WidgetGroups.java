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
