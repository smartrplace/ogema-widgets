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

package org.ogema.tools.widgets.collection.tests;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.junit.Assert;
import org.junit.Test;
import org.ogema.tools.widgets.test.base.WidgetLoader;
import org.ogema.tools.widgets.test.base.WidgetsTestBaseMin;
import org.ogema.tools.widgets.test.base.widgets.TestButton;
import org.ogema.tools.widgets.test.base.widgets.TestLabel;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.label.Label;

@ExamReactorStrategy(PerClass.class)
public class WidgetCollectionTest extends WidgetsTestBaseMin { 
	
	public WidgetCollectionTest() {
		super(false);
	}

	/**
	 * Creates an empty page and checks that it loads correctly
	 * @throws TimeoutException 
	 * @throws URISyntaxException 
	 * @throws ParseException 
	 */
	@Test
	public void setupWorks() throws ClientProtocolException, IOException, InterruptedException, ExecutionException, TimeoutException, ParseException, URISyntaxException {
		WidgetPage<?> page = widgetApp.createWidgetPage(nextUrl());
		WidgetLoader client = new WidgetLoader(page, HTTP_PORT);
		client.connect(); 
		client.close().get(5, TimeUnit.SECONDS);
	}
	
	/**
	 * Creates a page with two widgets and checks that they are available client-side after page initialization 
	 * @throws TimeoutException 
	 * @throws URISyntaxException 
	 * @throws ParseException 
	 */
	@Test 
	public void widgetsWork() throws ClientProtocolException, IOException, InterruptedException, ExecutionException, TimeoutException, ParseException, URISyntaxException {
		WidgetPage<?> page = widgetApp.createWidgetPage(nextUrl());
		String labelText = "test";
		Label label = new Label(page, "label", labelText);
		page.append(label);
		Button btn = new Button(page, "btn", "button");
		page.append(btn);		
		WidgetLoader client = new WidgetLoader(page, HTTP_PORT);
		client.connect();
		client.assertHasWidget(label);
		client.assertHasWidget(btn);
		client.close().get(5, TimeUnit.SECONDS);
	}

	/**
	 * Verifies that the initial widget information contains the correct data 
	 * @throws TimeoutException 
	 * @throws URISyntaxException 
	 * @throws ParseException 
	 */
	@Test
	public void widgetInitialDataWorks() throws ClientProtocolException, IOException, InterruptedException, ExecutionException, TimeoutException, ParseException, URISyntaxException {
		WidgetPage<?> page = widgetApp.createWidgetPage(nextUrl());
		String labelText = "test";
		Label label = new Label(page, "label", labelText);
		page.append(label);
		WidgetLoader client = new WidgetLoader(page, HTTP_PORT);
		client.connect();
		client.assertHasWidget(label);
		TestLabel clientLabel = client.getWidget(label.getId());
		Assert.assertEquals("Unexpected client side widget data",labelText,clientLabel.getText());
		client.close().get(5, TimeUnit.SECONDS);
	}
	
	/**
	 * Verifies that a Widget GET request works properly 
	 * @throws TimeoutException 
	 * @throws URISyntaxException 
	 * @throws ParseException 
	 */
	@Test
	public void widgetGETWorks() throws ClientProtocolException, IOException, InterruptedException, ExecutionException, TimeoutException, ParseException, URISyntaxException {
		WidgetPage<?> page = widgetApp.createWidgetPage(nextUrl());
		String labelText = "test";
		final String labelText2  = "newText";
		Label label = new Label(page, "label", labelText) {

			private static final long serialVersionUID = 1L;
			private final AtomicInteger counter = new AtomicInteger(0);
			
			// change data on second request
			@Override
			public void onGET(OgemaHttpRequest req) {
				if (counter.getAndIncrement() > 0)
					setText(labelText2, req);
			}
			
		};
		page.append(label);
		WidgetLoader client = new WidgetLoader(page, HTTP_PORT);
		client.connect();
		client.assertHasWidget(label);
		TestLabel clientLabel = client.getWidget(label.getId());
		Assert.assertNotNull("Widget missing",clientLabel);
		Assert.assertEquals("Unexpected client side widget data",labelText,clientLabel.getText());
		clientLabel.sendGET().get();  // send GET and wait for response
		Assert.assertEquals("Unexpected client side widget data",labelText2,clientLabel.getText());
		client.close().get(5, TimeUnit.SECONDS);
	}
	
	@Test
	public void triggerActionWorks() throws ClientProtocolException, IOException, InterruptedException, ExecutionException, TimeoutException, ParseException, URISyntaxException {
		WidgetPage<?> page = widgetApp.createWidgetPage(nextUrl());
		String labelText = "test";
		final String labelText2 = "newText";
		final Label label = new Label(page, "label", labelText);
		page.append(label);
		Button btn = new Button(page, "btn", "button") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				label.setText(labelText2, req);
			}
			
		};
		btn.triggerAction(label, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		page.append(btn);		
		WidgetLoader client = new WidgetLoader(page, HTTP_PORT);
		client.connect();
		client.assertHasWidget(label);
		client.assertHasWidget(btn);
		TestButton clientBtn = client.getWidget(btn.getId());
		TestLabel clientLabel = client.getWidget(label.getId());
		clientBtn.sendPOST(true).get(); // here we need to wait for the triggered  actions
		Assert.assertEquals("Unexpected widget data; triggerAction may have failed",labelText2, clientLabel.getText());
		client.close().get(5, TimeUnit.SECONDS);
	}

	
}
