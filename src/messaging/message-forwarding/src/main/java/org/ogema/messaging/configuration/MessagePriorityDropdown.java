package org.ogema.messaging.configuration;

import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.widgets.api.messaging.listener.MessageListener;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.dropdown.EnumDropdown;
import de.iwes.widgets.messaging.MessagingApp;
import de.iwes.widgets.messaging.model.MessagingService;
import de.iwes.widgets.messaging.model.UserConfig;

@SuppressWarnings("serial")
public class MessagePriorityDropdown extends EnumDropdown<MessagePriority> {
	protected final MessageListener l;
	protected final String object;
	protected final ResourceList<de.iwes.widgets.messaging.model.MessagingApp> appResources;
	protected final MessagingApp app;
	
	public MessagePriorityDropdown(OgemaWidget parent, String id, 
			MessageListener l, String userName,
			final ResourceList<de.iwes.widgets.messaging.model.MessagingApp> appResources,
			final MessagingApp app,
			OgemaHttpRequest req) {
		super(parent, id, req, MessagePriority.class);
		this.l = l;
		this.object = userName;
		this.appResources = appResources;
		this.app = app;
		finishConstructor();
	}

	public MessagePriorityDropdown(WidgetPage<?> page, String id, 
			MessageListener l, String userName,
			final ResourceList<de.iwes.widgets.messaging.model.MessagingApp> appResources,
			final MessagingApp app) {
		super(page, id, MessagePriority.class);
		this.l = l;
		this.object = userName;
		this.appResources = appResources;
		this.app = app;
		finishConstructor();
	}

	private void finishConstructor() {
		setTemplate(new DefaultEnumTemplate<MessagePriority>() {
			@Override
			public String getLabel(MessagePriority object, OgemaLocale locale) {
				switch(object) {
				case LOW:
					return "ALL";
				case MEDIUM:
					return "MEDIUM";
				case HIGH:
					return "HIGH ONLY";
				case NONE:
					return "NONE";
				}
				throw new IllegalStateException("Unknown priority:"+object);
			}
		});		
	}
	
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
	
	private de.iwes.widgets.messaging.model.MessagingApp get(final boolean doCreate) {
			//final ResourceList<de.iwes.widgets.messaging.model.MessagingApp> appResources,
			//final MessagingApp app) {
		return appResources.getAllElements().stream()
			.filter(appRes -> appRes.appId().isActive() && app.getMessagingId().equals(appRes.appId().getValue()))
			.findAny()
			.orElseGet(() -> {
				if (!doCreate)
					return null;
				final de.iwes.widgets.messaging.model.MessagingApp mapp = appResources.getSubResource(
						ResourceUtils.getValidResourceName(app.getMessagingId()), de.iwes.widgets.messaging.model.MessagingApp.class);
				mapp.appId().<StringResource> create().setValue(app.getMessagingId());
				mapp.active().<BooleanResource> create().setValue(true);
				mapp.activate(true);
				return mapp;
			});
		
	}


}
