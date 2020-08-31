package org.ogema.messaging.configuration;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.ResourceList;
import org.ogema.messaging.configuration.localisation.SelectConnectorDictionary;
import org.ogema.messaging.configuration.localisation.SelectConnectorDictionary_de;
import org.ogema.messaging.configuration.localisation.SelectConnectorDictionary_en;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.extended.plus.InitWidget;
import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.PageSnippetI;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.accordion.AccordionData;
import de.iwes.widgets.html.accordion.TemplateAccordion;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.emptywidget.EmptyData;
import de.iwes.widgets.html.emptywidget.EmptyWidget;
import de.iwes.widgets.html.form.dropdown.EnumDropdown;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.messaging.MessageReader;
import de.iwes.widgets.messaging.MessagingApp;
import de.iwes.widgets.template.PageSnippetTemplate;

public class PageInit {

	private final WidgetPage<?> page;
	private final Header header;
	private final Alert info;
	private final UsersInit userData;
	
	private final TemplateAccordion<MessagingApp> appAccordion;
	
	@SuppressWarnings("serial")
	public PageInit(final WidgetPage<SelectConnectorDictionary> page, final ApplicationManager appMan, 
			final ResourceList<de.iwes.widgets.messaging.model.MessagingApp> appList, final MessageReader reader) {
		page.registerLocalisation(SelectConnectorDictionary_de.class).registerLocalisation(SelectConnectorDictionary_en.class);
		this.page = page;
		this.header =  new Header(page, "header", "Message forwarding configurations") {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(page.getDictionary(req).header(),req);
			}
			
		};
		header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
		this.info = new Alert(page, "description","Explanation") {

			@Override
	    	public void onGET(OgemaHttpRequest req) {
	    		setHtml(page.getDictionary(req).description(), req);
	    		allowDismiss(true, req);
	    		autoDismiss(-1, req);
	    	}
	    	
	    };
	    info.addDefaultStyle(AlertData.BOOTSTRAP_INFO);
	    info.setDefaultVisibility(true);
	    this.userData = new UsersInit(page, "userData",  reader);
	    final PageSnippetTemplate<MessagingApp> template = new PageSnippetTemplate<MessagingApp>() {

			@Override
			public String getId(MessagingApp object) {
				return ResourceUtils.getValidResourceName(object.getMessagingId());
			}

			@Override
			public String getLabel(MessagingApp object, OgemaLocale locale) {
				return object.getName();
			}

			@Override
			public PageSnippetI getSnippet(MessagingApp item, OgemaHttpRequest req) {
				final String line = getId(item);
				final PageSnippet snippet = new PageSnippet(appAccordion, line + "_snippet", req);
				boolean allApps = item instanceof AllMessagingApps;
				if (!allApps) {
					Label symbolicName = new Label(appAccordion, line + "_symbN_", req) {
						
						public void onGET(OgemaHttpRequest req) {
							final String text = item.getName() + ": " 
									+ item.getBundleSymbolicName() + ", version " + item.getVersion();
							setText(text, req);
						}
						
					};
					snippet.append(symbolicName,req).linebreak(req);
				}
				String description = item.getDescription();
				if (description != null && !description.trim().isEmpty()) {
					Label descriptionLabel = new Label(appAccordion,line + "_description", "Description: " + description, req);
					snippet.append(descriptionLabel, req).linebreak(req);
				}
				final DynamicTable<String> usersTab = new DynamicTable<String>(appAccordion, line+ "_userTab", req) {
					
					@Override
					public void onGET(OgemaHttpRequest req) {
						updateRows(userData.getUsers(req).keySet(), req);
					}
					
				};
				snippet.append(usersTab, req);
				usersTab.setRowTemplate(new UsersTemplate(userData, req, item, appList));
				return snippet;
				
			}
		};
	    this.appAccordion = new TemplateAccordion<MessagingApp>(page, "appAccordion", template) {

			@Override
			public void onGET(OgemaHttpRequest req) {
				update(Stream.concat(
						reader.getMessageSenders().stream(),
						Stream.of(new AllMessagingApps())
					).collect(Collectors.toList()), req);
			}
			
		};
		appAccordion.addDefaultStyle(AccordionData.BOOTSTRAP_GREEN);
	    
		buildPage();
		setDependencies();
	}
	
	private final void buildPage() {
		page.append(header).linebreak().append(info).linebreak()
			.append(appAccordion).linebreak()
			.append(userData);
	}
	
	private final void setDependencies() {
		userData.triggerAction(appAccordion, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
	}
	
	public static Map<MessageListener, List<String>> getInitListeners(MessageReader reader) {
		Map<MessageListener, List<String>> listeners = reader.getMessageListeners().values().stream()
				.collect(Collectors.toMap(Function.identity(), MessageListener::getKnownUsers));
		return listeners;
	}
	public static Map<String, List<MessageListener>> getUsers(MessageReader reader) {
		Map<MessageListener, List<String>> listeners = getInitListeners(reader);
		
		return UsersData.setUsersStatic(listeners);
		//Map<MessageListener, List<String>> users = listeners.entrySet().stream()
		//		.flatMap(entry -> entry.getValue().stream().map(user -> new SimpleEntry<>(user, entry.getKey())))
		//		.collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
		//return users;
	}
	public static List<MessageListener> getListenersForUser(String userName, MessageReader reader) {
		return getUsers(reader).get(userName);
	}
	
	@SuppressWarnings("serial")
	static class UsersInit extends EmptyWidget implements InitWidget {
		
		private final MessageReader reader;

		public UsersInit(WidgetPage<?> page, String id, MessageReader reader) {
			super(page, id);
			this.reader = reader;
		}

		@Override
		public void init(OgemaHttpRequest req) {
			final Map<MessageListener, List<String>> listeners = getInitListeners(reader);
			//final Map<MessageListener, List<String>> listeners = reader.getMessageListeners().values().stream()
			//	.collect(Collectors.toMap(Function.identity(), MessageListener::getKnownUsers));
			((UsersData) getData(req)).setUsers(listeners);
		}
		
		@Override
		public EmptyData createNewSession() {
			return new UsersData(this);
		}
		
		public Map<String, List<MessageListener>> getUsers(final OgemaHttpRequest req) {
			return ((UsersData) getData(req)).getUsers();
		}
		
		public Map<MessageListener, List<String>> getListeners(final OgemaHttpRequest req) {
			return ((UsersData) getData(req)).getListeners();
		}
		
	}
	
	static class UsersData extends EmptyData {

		private Map<String, List<MessageListener>> users;
		private Map<MessageListener, List<String>> listeners;
		
		public UsersData(UsersInit empty) {
			super(empty);
		}

		public void setUsers(Map<MessageListener, List<String>> listeners) {
			this.listeners = listeners;
			this.users = listeners.entrySet().stream()
				.flatMap(entry -> entry.getValue().stream().map(user -> new SimpleEntry<>(user, entry.getKey())))
				.collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
		}
		
		public static Map<String, List<MessageListener>> setUsersStatic(Map<MessageListener, List<String>> listeners) {
			Map<String, List<MessageListener>> users = listeners.entrySet().stream()
				.flatMap(entry -> entry.getValue().stream().map(user -> new SimpleEntry<>(user, entry.getKey())))
				.collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
			return users;
		}

		public Map<String, List<MessageListener>> getUsers() {
			return users;
		}
		
		public Map<MessageListener, List<String>> getListeners() {
			return listeners;
		}
		
	}
	
	
	private static class UsersTemplate extends RowTemplate<String> {
		
		private final MessagingApp app;
		private final UsersInit init;
		private final OgemaHttpRequest req;
		private final ResourceList<de.iwes.widgets.messaging.model.MessagingApp> appResources;
		
		UsersTemplate(UsersInit init, OgemaHttpRequest req, 
				MessagingApp app, ResourceList<de.iwes.widgets.messaging.model.MessagingApp> appResources) {
			this.init = init;
			this.req = req;
			this.app = app; 
			this.appResources = appResources;
		}
		
		@Override
		public Row addRow(String object, OgemaHttpRequest req) {
			final List<MessageListener> listeners = init.getUsers(req).get(object);
			if (listeners == null)
				return new Row();
			final Row row = new Row();
			final String lineId = getLineId(object);
			row.addCell("user", object);
			for (MessageListener l: listeners) {
				final EnumDropdown<MessagePriority> prioDrop = new MessagePriorityDropdown(init,
						lineId + "_x_" + ResourceUtils.getValidResourceName(l.getId()) + "_prio",
						l, object,
						appResources, app,
						req);
				/*@SuppressWarnings("serial")
				final EnumDropdown<MessagePriority> prioDrop = new EnumDropdown<MessagePriority>(init, lineId + "_x_" + ResourceUtils.getValidResourceName(l.getId()) + "_prio", req, MessagePriority.class) {
					
					@Override
					public void onGET(OgemaHttpRequest req) {
						final de.iwes.widgets.messaging.model.MessagingApp mapp = get(false);
						if (mapp == null) {
							selectItem(MessagePriority.NONE, req);
							return;
						}
						final Resource r = mapp.services().getSubResource(ResourceUtils.getValidResourceName(l.getId()));
						if (!(r instanceof MessagingService) || !r.isActive()) {
							selectItem(MessagePriority.NONE, req);
							return;
						}
						final Resource u = ((MessagingService) r).users().getSubResource(ResourceUtils.getValidResourceName(object));
						final UserConfig cfg = u instanceof UserConfig && u.isActive() ? (UserConfig) u : null;
						if (cfg == null) {
							selectItem(MessagePriority.NONE, req);
							return;
						}
						MessagePriority prio = MessagePriority.NONE;
						try {
							prio = MessagePriority.forInteger(cfg.priority().getValue());
						} catch (Exception e) {}
						selectItem(prio, req);
					}
					
					@Override
					public void onPOSTComplete(String data, OgemaHttpRequest req) {
						final MessagePriority prio = getSelectedItem(req);
						final boolean isUnset = prio == MessagePriority.NONE;
						final de.iwes.widgets.messaging.model.MessagingApp mapp = get(!isUnset);
						if (mapp == null)
							return;
						if (!isUnset && !mapp.isActive())
							mapp.activate(false);
						final MessagingService service = 
								mapp.services().getSubResource(ResourceUtils.getValidResourceName(l.getId()), MessagingService.class).create();
						if (!service.isActive()) {
							if (isUnset)
								return;
							service.serviceId().<StringResource> create().setValue(l.getId());
							service.activate(true);
						}
						final UserConfig cfg = service.users().getSubResource(ResourceUtils.getValidResourceName(object), UserConfig.class).create();
						if (!cfg.isActive()) {
							if (isUnset)
								return;
							cfg.userName().<StringResource> create().setValue(object);
						} else if (isUnset) {
							cfg.delete();
							return;
						}
						cfg.priority().<IntegerResource> create().setValue(prio.getPriority());
						cfg.activate(true);
					}
				};*/
				row.addCell(ResourceUtils.getValidResourceName(l.getId()), prioDrop);
				prioDrop.selectDefaultItem(MessagePriority.NONE);
			}
			return row;
		}
		
		@Override
		public String getLineId(String object) {
			return ResourceUtils.getValidResourceName(app.getMessagingId() + "_x_" + object);
		}

		@Override
		public Map<String, Object> getHeader() {
			final Map<String, Object> header = new LinkedHashMap<>();
			header.put("user", "User");
			header.putAll(init.getListeners(req).keySet().stream()
				.map(MessageListener::getId)
				.collect(Collectors.toMap(ResourceUtils::getValidResourceName, Function.identity())));
			return header;
		}
		
		
		
	}
	
	
}
