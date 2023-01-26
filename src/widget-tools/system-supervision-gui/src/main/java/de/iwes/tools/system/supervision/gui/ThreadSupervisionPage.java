package de.iwes.tools.system.supervision.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.resourcemanager.ResourceAccess;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.accordion.Accordion;
import de.iwes.widgets.html.accordion.AccordionData;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.ButtonData;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.html5.Flexbox;
import de.iwes.widgets.html.html5.flexbox.AlignItems;
import de.iwes.widgets.html.textarea.TextArea;

public class ThreadSupervisionPage {
	
	private final WidgetPage<?> page;
	private final Header header;
	private final Alert alert;
	private final Button threadDumpButton;
	private final Button threadDumpFocused;
	private final TextArea threadDump;
	
	private final Label basicLockInfo;
	private final Button basicLockUpdate;
	private final Button lockWaitingThreadsButton;
	private final Button lockDataFocused;
	private final TextArea lockData;
	
	public ThreadSupervisionPage(final WidgetPage<?> page, final ApplicationManager am) {
		this.page = page;
		this.header =new Header(page, "header", true);
		header.setDefaultColor("blue");
		header.setDefaultText("System supervision: threads and locks");
		header.addDefaultStyle(HeaderData.CENTERED);
		
		this.alert = new Alert(page, "alert", "");
		alert.setDefaultVisibility(false);
		threadDump = new TextArea(page, "threadDump");
		threadDumpButton = new Button(page, "createThreadDump") {
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				java.lang.management.ThreadMXBean bean = java.lang.management.ManagementFactory.getThreadMXBean();
				java.lang.management.ThreadInfo[] infos = bean.dumpAllThreads(true, true);
				
				threadDump.setText(Arrays.stream(infos).map(Object::toString).collect(Collectors.joining()), req);
				threadDump.setRows(50, req);
				threadDump.setCols(125, req);
				threadDump.setSelected(true, req);
				threadDumpFocused.enable(req);
			}
			
		};
		threadDumpButton.setDefaultText("Get a thread dump");
		threadDumpFocused = new Button(page, "threadDumpFocused") {
			
			@Override
			protected void setDefaultValues(ButtonData opt) {
				super.setDefaultValues(opt);
				opt.disable();
			}
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				threadDump.setSelected(true, req);
			}
			
		};
		threadDumpFocused.setDefaultText("Select text");
		
		basicLockInfo = new Label(page, "basicLockInfo") {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				try {
					final ResourceAccess ra = am.getResourceAccess();
					final java.lang.reflect.Method m = ra.getClass().getDeclaredMethod("getDatabaseManager");
					m.setAccessible(true);
					final /*ResourceDBManager*/ Object dbManager = m.invoke(ra);
					final java.lang.reflect.Field f = dbManager.getClass().getDeclaredField("commitLock");
					f.setAccessible(true);
					final ReentrantReadWriteLock lock = (ReentrantReadWriteLock) f.get(dbManager);
					final int readHolds = lock.getReadHoldCount();
					final int writeHolds = lock.getWriteHoldCount();
					final int queueLength = lock.getQueueLength();
					setText("Resource lock queue length: " + queueLength + ", read locks held: " + readHolds + ", write locks held: " + writeHolds, req);
				} catch (Exception e) {
					setText("An error occured " + e, req);
				}
			}
			
		};
		basicLockUpdate = new Button(page, "basicLockUpdate");
		basicLockUpdate.setDefaultText("Update");
		basicLockUpdate.setDefaultToolTip("Update basic resource lock information");
		lockData = new TextArea(page, "lockData");
		lockWaitingThreadsButton = new Button(page, "lockWaitingThreadsButton") {
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				try {
					final ResourceAccess ra = am.getResourceAccess();
					final java.lang.reflect.Method m = ra.getClass().getDeclaredMethod("getDatabaseManager");
					m.setAccessible(true);
					final /*ResourceDBManager*/ Object dbManager = m.invoke(ra);
					final java.lang.reflect.Field f = dbManager.getClass().getDeclaredField("commitLock");
					f.setAccessible(true);
					final ReentrantReadWriteLock lock = (ReentrantReadWriteLock) f.get(dbManager);
					final java.lang.reflect.Method m2 = ReentrantReadWriteLock.class.getDeclaredMethod("getQueuedThreads");
					m2.setAccessible(true);
					/*final*/ Collection<Thread> threads = (Collection<Thread>) m2.invoke(lock);
					final java.lang.reflect.Method m3 = ReentrantReadWriteLock.class.getDeclaredMethod("getOwner");
					m3.setAccessible(true);
					final Thread owner = (Thread) m3.invoke(lock);
					if (owner != null) {
						final List<Thread> threads2 = new ArrayList<>();
						threads2.add(owner);
						if (!threads.isEmpty())
							threads2.addAll(threads);
						threads = threads2;
					}
					final int sz = threads.size();
					final String stackTrace = threads.stream().map(ThreadSupervisionPage::getThreadStackTrace).collect(Collectors.joining("\n\n")); 
					lockData.setText(sz == 0 ? "None waiting" : stackTrace, req);
					lockData.setRows(sz == 0 ? 5 : sz > 4 ? 50 : 25, req);
					lockData.setCols(sz == 0 ? 20 : 125, req);
					lockData.setSelected(sz > 0, req);
					if (sz > 0)
						lockDataFocused.enable(req);
					else
						lockDataFocused.disable(req);
				} catch (Exception e) {
					lockData.setText(e.toString(), req);
					lockData.setRows(10, req);
					lockData.setCols(125, req);
				}
			}
			
		};
		lockWaitingThreadsButton.setDefaultText("Get threads waiting for resource lock");
		lockWaitingThreadsButton.setDefaultToolTip("Retrieve stack traces for all threads currently waiting to acquire the resource lock, including the owner of the write lock at first position, if any");
		lockDataFocused = new Button(page, "lockDataFocused") {
			
			@Override
			protected void setDefaultValues(ButtonData opt) {
				super.setDefaultValues(opt);
				opt.disable();
			}
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				lockData.setSelected(true, req);
			}
			
		};
		lockDataFocused.setDefaultText("Select text");
		
		buildPage();
		setDependencies();
	}
	
	private final void buildPage() {
		page.append(header).linebreak().append(alert);
		final Accordion acc = new Accordion(page, "accordion", true);
		acc.addDefaultStyle(AccordionData.BOOTSTRAP_LIGHT_BLUE);
		
		final PageSnippet threads = new PageSnippet(page, "threadSnippet", true);
		final Flexbox flex = new Flexbox(page, "threadDumpButtons", true);
		flex.addItem(threadDumpButton, null).addItem(threadDumpFocused, null);
		flex.setColumnGap("1em", null);
		threads.append(flex, null).linebreak(null);
		threads.append(threadDump, null);
		acc.addItem("Threads", threads, null);
		
		final PageSnippet locks = new PageSnippet(page, "locksSnippet", true);
		final Flexbox locksFlex = new Flexbox(page, "locksButtons", true);
		locksFlex.setAlignItems(AlignItems.BASELINE, null);
		locksFlex.addItem(basicLockInfo, null).addItem(basicLockUpdate, null).addItem(lockWaitingThreadsButton, null).addItem(lockDataFocused, null);
		locksFlex.setColumnGap("1em", null);
		locks.append(locksFlex, null).linebreak(null);
		locks.append(lockData, null);
		acc.addItem("Resource lock", locks, null);
		
		page.append(acc);
		
	}
	
	private final void setDependencies() {
		threadDumpButton.triggerAction(threadDump, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		threadDumpButton.triggerAction(threadDumpFocused, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		threadDumpFocused.triggerAction(threadDump, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		basicLockUpdate.triggerAction(basicLockInfo, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		lockWaitingThreadsButton.triggerAction(lockData, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		lockWaitingThreadsButton.triggerAction(lockDataFocused, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		lockDataFocused.triggerAction(lockData, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
	}
	
	private static String getThreadStackTrace(final Thread thread) {
		return "\"" + thread.getName() + "\" Id=" + thread.getId() + " " + thread.getState().name() + "\n    " + 
				Arrays.stream(thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n    "));
	}

}
