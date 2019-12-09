
package org.ogema.messaging.telegram.connector;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.buttonconfirm.ButtonConfirm;
import de.iwes.widgets.html.buttonconfirm.ButtonConfirmData;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.form.textfield.ValueInputField;
import de.iwes.widgets.html.html5.TemplateGrid;
import de.iwes.widgets.html.popup.Popup;

class ContactsPage {

	private final WidgetPage<?> page;
	private final Header header;
	private final Alert alert;
	private final TemplateGrid<TelegramContact> contacts;
	
	// create new user
	private final Button openCreatePopup;
	private final TextField firstName;
	private final TextField lastName;
	private final ValueInputField<Long> chatId;
	private final Button createSubmit;
	private final Popup createPopup;
	
	@SuppressWarnings("serial")
	public ContactsPage(final WidgetPage<?> page, final ResourceList<TelegramContact> contactList) {
		this.page = page;
		this.header = new Header(page, "header", "Telegram contacts");
		header.setDefaultColor("blue");
		header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_LEFT);
		
		this.alert = new Alert(page, "alert", "");
		alert.setDefaultVisibility(false);
		
		final RowTemplate<TelegramContact> contactsTemplate = new RowTemplate<TelegramContact>() {
			
			final Map<String, Object> header;
			
			{
				final Map<String, Object> headerLocal = new LinkedHashMap<>();
				headerLocal.put("name", "User");
				headerLocal.put("chatid", "Chat id");
				headerLocal.put("delete", "Delete");
				header = Collections.unmodifiableMap(headerLocal);
			}
			
			
			@Override
			public String getLineId(TelegramContact object) {
				return ResourceUtils.getValidResourceName(object.getPath());
			}
			
			@Override
			public Map<String, Object> getHeader() {
				return header;
			}
			
			@Override
			public Row addRow(TelegramContact object, OgemaHttpRequest req) {
				final Row row = new Row();
				final String lineId = getLineId(object);
				final String fullName =object.firstName().getValue() + " " + object.lastName().getValue(); 
				row.addCell("name", fullName);
				row.addCell("chatid", object.chatId().getValue());
				final ButtonConfirm delete = new ButtonConfirm(contacts, "delete_" + lineId, req) {
					
					@Override
					public void onPOSTComplete(String data, OgemaHttpRequest req) {
						object.delete();
					}
					
				};
				delete.triggerAction(contacts, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
				delete.setConfirmBtnMsg("Delete", req);
				delete.setConfirmMsg("Do you really want to delete the user " + fullName, req);
				delete.setConfirmPopupTitle("Delete user", req);
				delete.setCancelBtnMsg("Cancel", req);
				delete.setText("Delete user", req);
				delete.addStyle(ButtonConfirmData.CONFIRM_RED, req);
				delete.addStyle(ButtonConfirmData.CONFIRM_LIGHT_BLUE, req);
				row.addCell("delete", delete);
				return row;
			}
			
		};
		this.contacts = new TemplateGrid<TelegramContact>(page, "contacts", false, contactsTemplate) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				update(contactList.getAllElements(), req);
			}
		};
		contacts.setDefaultAppendFillColumn(true);
		contacts.setDefaultColumnGap("3em");
		contacts.setDefaultRowGap("1em");
		
		this.openCreatePopup = new Button(page, "openCreatePopup", "Create new receiver");
		this.firstName = new TextField(page, "firstName");
		this.lastName = new TextField(page, "lastName");
		this.chatId = new  ValueInputField<Long>(page, "chatId", Long.class);
		this.createSubmit = new Button(page, "createSubmit", "Submit") {
			
			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				final String first = firstName.getValue(req).trim();
				final String last = lastName.getValue(req).trim();
				final Long chat = chatId.getNumericalValue(req);
				if (first.isEmpty() && last.isEmpty()) {
					alert.showAlert("Please enter a name", false, req);
					return;
				}
				if (chat == null) {
					alert.showAlert("Please enter your chat id", false, req);
					return;
				}
				if (contactList.getAllElements().stream()
						.filter(c-> first.equalsIgnoreCase(c.firstName().getValue()) && last.equalsIgnoreCase(c.lastName().getValue()))
						.findAny()
						.isPresent()) {
					alert.showAlert("User " + first + " " + last + " already exists", false, req);
					return;
				}
				if (contactList.getAllElements().stream()
						.filter(c-> chat.longValue() == c.chatId().getValue())
						.findAny()
						.isPresent()) {
					alert.showAlert("Contact with chat id " + chat + " already exists", false, req);
					return;
				}
				final TelegramContact contact = contactList.add();
				contact.firstName().<StringResource> create().setValue(first);
				contact.lastName().<StringResource> create().setValue(last);
				contact.chatId().<TimeResource> create().setValue(chat);
				contact.activate(true);
				alert.showAlert("New telegram contact has been created: " + first + " " + last, true, req);
			}
			
		};
		this.createPopup = new Popup(page, "createPopup", true);
		final PageSnippet createBody = new PageSnippet(page, "createBody", true);
		int row = 0;
		final StaticTable table = new StaticTable(3, 2)
				.setContent(row, 0, "First name").setContent(row++, 1, firstName)
				.setContent(row, 0, "Last name").setContent(row++, 1, lastName)
				.setContent(row, 0, "Chat id").setContent(row++, 1, chatId);
		createBody.append(table, null);
		createPopup.setBody(createBody, null);
		createPopup.setFooter(createSubmit, null);
		createPopup.setTitle("Create new Telegram receiver", null);

		buildPage();
		setDependencies();
	}
	
	private final void buildPage() {
		page.append(header).linebreak().append(alert).linebreak()
			.append(openCreatePopup).linebreak()
			.append(contacts).linebreak()
			.append(createPopup);
	}

	private final void setDependencies() {
		openCreatePopup.triggerAction(createPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		createSubmit.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		createSubmit.triggerAction(contacts, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
	}
	
}
