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
package org.ogema.tools.widgets.collection.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.ogema.tools.widgets.test.base.WidgetLoader;
import org.ogema.tools.widgets.test.base.WidgetsTestBaseMin;
import org.ogema.tools.widgets.test.base.widgets.TestButton;
import org.ogema.tools.widgets.test.base.widgets.TestLabel;
import org.ogema.tools.widgets.test.base.widgets.TestTable;
import org.ogema.tools.widgets.test.base.widgets.TestTextField;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;

@ExamReactorStrategy(PerClass.class) 
public class MultiClientTest extends WidgetsTestBaseMin {
	
	public MultiClientTest() {
		super(false);
	}
	
//	private static final int MAX_NR_CLIENTS = 20;
	
	/*
	 * FIXME obscure sporadic ClassNotFoundExceptions and NoClassDefFoundErrors if nrClients ~ 100 
	 * Other sporadic errors occur as well (404 response), in particular if one increases the number of clients (50 - 100)
	 * TODO print some status info in case of failure
	 */
//	@Ignore
	@Test 
	public void parallelRequestsWorkForSessionSpecificWidgets() throws Throwable {
		int nrClients = 10; // on a PC 50 is working fine, but 100 mostly fails for obscure reasons
		final WidgetPage<?> page = widgetApp.createWidgetPage(nextUrl());
		final Label label = new Label(page, "label","Default text");
		page.append(label);
		final TextField tf = new TextField(page, "textField", "Default text") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				label.setText(getValue(req), req); // write text from the text field to the label
			}
			
		}; 
		tf.triggerAction(label, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		page.append(tf);
		
		List<Future<?>> futures = new ArrayList<>(nrClients);
		ExecutorService exec = Executors.newFixedThreadPool(nrClients);
		final CountDownLatch latch1 = new CountDownLatch(1);
		final CountDownLatch latch2 = new CountDownLatch(1);
		final CountDownLatch clientsLatch1 = new CountDownLatch(nrClients);
		final CountDownLatch clientsLatch2 = new CountDownLatch(nrClients);
		for (int i=0;i<nrClients;i++) {
			final int j = i;
			Callable<Void> task = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					WidgetLoader client = new WidgetLoader(page, HTTP_PORT);
					clientsLatch1.countDown();
					latch1.await();
					// System.out.println("   client " + j + " starting");
					client.connect();
					// System.out.println("    client " + j + " started");
					client.assertHasWidget(tf);
					client.assertHasWidget(label);
					TestTextField textField = client.getWidget(tf.getId());
					TestLabel testLabel = client.getWidget(label.getId());
					String clientSpecificText = "client " + j;
					textField.setValue(clientSpecificText);
					textField.sendPOST(true).get(10,TimeUnit.SECONDS);
					// System.out.println("     client " + j + " done");
					Assert.assertEquals(clientSpecificText, testLabel.getText());

					clientsLatch2.countDown();
					latch2.await();
					
					clientSpecificText = "updated client " + j;
					textField.setValue(clientSpecificText);
					textField.sendPOST(true).get(10,TimeUnit.SECONDS);
					//	System.out.println("     client " + j + " done again");
					Assert.assertEquals(clientSpecificText, testLabel.getText());
					client.close().get(2, TimeUnit.SECONDS); // should finish immediately
					return null;
				}
				
			}; 
			futures.add(exec.submit(task));
		}
		Assert.assertTrue(clientsLatch1.await(60, TimeUnit.SECONDS));; // assure all tasks are ready to query the page
		latch1.countDown();
		
		// we wait for all clients to reach a specific point in their execution schedule, where all the relevant AsynClients have been initialized,
		// so that for the following tests we can be sure that there is no artifical synchronization from creation of the clients.
		boolean done = clientsLatch2.await(30,TimeUnit.SECONDS);
		Assert.assertTrue("Client seems to be hanging... no response after 30s.", done);
		latch2.countDown();
		
		// wait for all clients to finish their tasks
		for (Future<?> f: futures) {
			try {
				f.get(30,TimeUnit.SECONDS);
			} catch(ExecutionException e) {
				System.out.println("There was an exception in some client task...");
				e.printStackTrace();
				throw e.getCause();
			}
		}
		// FIXME
		System.out.println("MultiClientTest successful...");
	}
	
	/**
	 * Here we create a page with one global table; it contains non-global subwidgets (2 labels and 1 button per row),
	 * and a new row is created on each GET-request from one of the clients. 
	 * XXX A bunch of strange exceptions occur sporadically; furthermore, the number of rows in the table found by the clients is
	 * often inconsistent, but occasionally makes sense; sometimes they don't find any rows at all, in which case the test fails
	 */
	@Ignore
	@Test 
	public void parallelRequestsWorkForGlobalWidgets() throws Throwable {
		int nrClients = 10;
		final WidgetPage<?> page = widgetApp.createWidgetPage(nextUrl());
		

		RowTemplate<String> template = new RowTemplate<String>() {

			@Override
			public Row addRow(String object,OgemaHttpRequest req) {
				Row row = new Row();
				Label idLabel = new Label(page, object, object);
				row.addCell(idLabel);
				final Label counterLabel = new Label(page, "counter_" + object,"0");
				row.addCell(counterLabel);
				Button button = new Button(page, "btn_" + object) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onPOSTComplete(String data, OgemaHttpRequest req) {
						int counter = Integer.parseInt(counterLabel.getText(req))+1;
						counterLabel.setText(counter + "", req);
					}
					
				};
				button.triggerAction(counterLabel, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
				row.addCell(button);
				return row;
			}

			@Override
			public String getLineId(String object) {
				return object;
			}

			@Override
			public Map<String, Object> getHeader() {
				return null;
			}
		};
		final DynamicTable<String> testtable = new DynamicTable<String>(page, "testtable", true) {

			private static final long serialVersionUID = 1L;
			private final AtomicInteger counter= new AtomicInteger(0);

			// every GET creates a new row!
			@Override
			public void onGET(OgemaHttpRequest req) {
				addItem("row" + counter.getAndIncrement(), req);
			}
			
		};
		testtable.setRowTemplate(template);
		page.append(testtable);
		
		List<Future<?>> futures = new ArrayList<>(nrClients);
		ExecutorService exec = Executors.newFixedThreadPool(nrClients);
		final CountDownLatch latch1 = new CountDownLatch(1);
		final CountDownLatch latch2 = new CountDownLatch(1);
		final CountDownLatch clientLatch1 = new CountDownLatch(nrClients);
		final CountDownLatch clientLatch2 = new CountDownLatch(nrClients);
		for (int i=0;i<nrClients;i++) {
			Callable<Void> task = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					WidgetLoader client = new WidgetLoader(page, HTTP_PORT);
					clientLatch1.countDown();
					latch1.await();
					// System.out.println("   client " + j + " starting");
					client.connect();
					// System.out.println("    client " + j + " started");
					client.assertHasWidget(testtable);
					TestTable tt = client.getWidget(testtable.getId());
//						tt.initGETClient();
					Set<String> rows = tt.getRows();
					// FIXME 
					System.out.println("  Nr rows found: " + rows.size());
					Assert.assertFalse("Table rows found empty, although each GET creates a new row!",rows.isEmpty());
					String id = rows.iterator().next();
					// FIXME
					System.out.println("    selected row " + id);
//						client.reloadWidgets();
					TestLabel label = client.getWidget("counter_"+id);
					TestButton btn = client.getWidget("btn_" +id);
					Assert.assertNotNull("Widget missing", label);
					Assert.assertNotNull("Widget missing", btn);
					
					label.initGETClient();
					btn.initPOSTClient();
					tt.initGETClient();
					clientLatch2.countDown();
					latch2.await();
					
					label.sendGET().get(5,TimeUnit.SECONDS);
					int cnt = Integer.parseInt(label.getText());
					btn.sendPOST(true).get(5,TimeUnit.SECONDS);
					// FIXME
					System.out.println("   Old counter: " + cnt  + ", new: " + Integer.parseInt(label.getText()));
					Assert.assertEquals("Widget trigger in table failed",cnt+1, Integer.parseInt(label.getText()));
					
					tt.sendGET().get(5,TimeUnit.SECONDS); // make sure no problems occur here 
					
					client.close().get(2, TimeUnit.SECONDS); // should finish immediately
					return null;
				}
				
			}; 
			futures.add(exec.submit(task));
		}
		Assert.assertTrue("Clients not ready",clientLatch1.await(60, TimeUnit.SECONDS));
		latch1.countDown();
		
		// we wait for all clients to reach a specific point in their execution schedule, where all the relevant AsynClients have been initialized,
		// so that for the following tests we can be sure that there is no artifical synchronization from creation of the clients.
		Assert.assertTrue("Clients not ready",clientLatch2.await(30, TimeUnit.SECONDS));
		latch2.countDown();
		
		// wait for all clients to finish their tasks
		for (Future<?> f: futures) {
			try {
				f.get(30,TimeUnit.SECONDS);
			} catch(ExecutionException e) {
				System.out.println("There was an exception in some client task...");
				e.printStackTrace();
				throw e.getCause();
			}
		}
		// FIXME
		System.out.println("MultiClientTest2 successful...");
	}
	
	/**
	 * We introduce a ThreadLocal counter in one widget (text field) and increase it every time
	 * a POST is sent to this widget. The counter value is then written to another widget (label).
	 * Then we check that each client sees its own value of the counter and there is no
	 * interference between clients. 
	 */
	@Ignore("Sporadic failure: no session cookie received")
	@Test
	public void threadPerSessionWorksForNonGlobalWidgets() throws Throwable {
		int nrClients = 50;
		final WidgetPage<?> page = widgetApp.createWidgetPage(nextUrl());
		final Label label = new Label(page, "label","0");
		page.append(label);
		final TextField tf = new TextField(page, "textField", "Default text") {

			private static final long serialVersionUID = 1L;
			private final ThreadLocal<Integer> counter = new ThreadLocal<Integer>() { 
				
				@Override
				protected Integer initialValue() {
					return 0;
				}
				
			};

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				counter.set(counter.get()+1);
				label.setText(counter.get()+"", req); // increase label counter by 1
			}
			
		}; 
		tf.triggerAction(label, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		page.append(tf);
		
		List<Future<?>> futures = new ArrayList<>(nrClients);
		ExecutorService exec = Executors.newFixedThreadPool(nrClients);
		final CountDownLatch latch1 = new CountDownLatch(1);
		final CountDownLatch clientLatch = new CountDownLatch(nrClients);
		// here we establish the connections in a single thread, so no problems
		// should occur in the page initialization
		for (int i=0;i<nrClients;i++) {
			final WidgetLoader client = new WidgetLoader(page, HTTP_PORT);
			client.connect(); // sporadic: no cookie received!
			client.assertHasWidget(tf);
			client.assertHasWidget(label);
			final TestTextField textField = client.getWidget(tf.getId());
			final TestLabel testLabel = client.getWidget(label.getId());
			textField.initPOSTClient();
			testLabel.initGETClient();
			Callable<Void> task = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					clientLatch.countDown();
					latch1.await();
					int cnt = Integer.parseInt(testLabel.getText());
					Assert.assertEquals(0, cnt);
					for (int j=0;j<10;j++) {
						textField.sendPOST(true).get(5,TimeUnit.SECONDS);
						cnt = Integer.parseInt(testLabel.getText());
						//	System.out.println("  Found counter " + cnt);
						Assert.assertEquals(j+1, cnt);
					}
					client.close().get(2, TimeUnit.SECONDS);
					return null;
				}
				
			}; 
			futures.add(exec.submit(task));
		}
		Assert.assertTrue("Clients not ready",clientLatch.await(60, TimeUnit.SECONDS));
		latch1.countDown();
		
		// we wait for all clients to reach a specific point in their execution schedule, where all the relevant AsynClients have been initialized,
		// so that for the following tests we can be sure that there is no artifical synchronization from creation of the clients.
		
		// wait for all clients to finish their tasks
		for (Future<?> f: futures) {
			try {
				f.get(10,TimeUnit.SECONDS);
			} catch(ExecutionException e) {
				System.out.println("There was an exception in some client task...");
				e.printStackTrace();
				throw e.getCause();
			}
		}
		// FIXME
		System.out.println("MultiClientTest successful...");
	}
	

}
