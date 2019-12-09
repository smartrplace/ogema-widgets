package de.iee.sema.remote.user.administration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ogema.accesscontrol.PermissionManager;
import org.ogema.core.application.ApplicationManager;
import org.ogema.tools.resource.util.ResourceUtils;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionUpdate;
import org.osgi.service.permissionadmin.PermissionInfo;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.buttonconfirm.ButtonConfirm;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.checkbox.Checkbox2;
import de.iwes.widgets.html.form.checkbox.DefaultCheckboxEntry;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.form.textfield.TextFieldType;
import de.iwes.widgets.html.html5.Flexbox;
import de.iwes.widgets.html.html5.SimpleGrid;
import de.iwes.widgets.html.html5.TemplateGrid;
import de.iwes.widgets.html.html5.flexbox.AlignContent;
import de.iwes.widgets.html.html5.grid.AlignItems;
import de.iwes.widgets.html.popup.Popup;
import de.iwes.widgets.html.textarea.TextArea;

// TODO distinguish between allowed and denied permissions
class EditPageInit {

	private final WidgetPage<?> page;
	private final Header header;
	private final Alert alert;
	private final Flexbox filterFlex;
	private final Label filterLabel;
	private final Header userHeader;
	private final TextField userFilter;
	private final TemplateGrid<String> usersTable;
	
	private final Popup copyPopup;
	private final TextField copyId;
	private final TextField copyPw;
	private final TextArea copyPerms;
	private final Checkbox2 copyNatural;
	private final Button copyUserSubmit;
	
	private final Popup editPopup;
	private final Label editUserName;
	private final TextArea editPerms;
	private final Button editUserSubmit;
	
	private final Header furtherPermsHeader;
	private final SimpleGrid furtherPerms;
	
	@SuppressWarnings("serial")
	EditPageInit(final WidgetPage<?> page, final ApplicationManager appMan, 
			final PermissionManager permMan, final ConditionalPermissionAdmin cpa) {
		this.page = page;
		this.header = new Header(page, "header", "User Permission Administation");
//		header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
		header.setDefaultColor("blue");
		
		this.alert = new Alert(page, "alert", "");
		alert.setDefaultVisibility(false);
		
		this.userHeader = new Header(page, "userHeader", "User-specific permissions");
		userHeader.setDefaultColor("blue");
		
		this.filterLabel = new Label(page, "filterLabel");
		filterLabel.setDefaultText("Filter users:");
		this.userFilter = new TextField(page, "userFilter");
		this.filterFlex = new Flexbox(page, "filterFlxe", true);
		filterFlex.addItem(filterLabel, null).addItem(userFilter, null);
		filterFlex.setDefaultAlignContent(AlignContent.FLEX_LEFT);
		userFilter.setDefaultMargin("1em", false, true, false, false);
			
		final RowTemplate<String> usersTemplate = new RowTemplate<String>() {
			
			private final Map<String, Object> header;
			 
			{
				final Map<String, Object> headerLocal = new LinkedHashMap<>();
				headerLocal.put("user", "User");
				headerLocal.put("natural", "User type");
				headerLocal.put("perms", "Permissions");
				headerLocal.put("edit", "Edit");
				headerLocal.put("copy", "Copy");
				headerLocal.put("delete", "Delete");
				header = Collections.unmodifiableMap(headerLocal);
			}
			
			@Override
			public String getLineId(String object) {
				return ResourceUtils.getValidResourceName(object);
			}
			
			@Override
			public Map<String, Object> getHeader() {
				return header;
			}
			
			@Override
			public Row addRow(String user, OgemaHttpRequest req) {
				final Row row = new Row();
				final String lineId = getLineId(user);
				row.addCell("user", user);
				final Label nat = new Label(usersTable, "natural_" + lineId, req) {
					@Override
					public void onGET(OgemaHttpRequest req) {
						final boolean natural = permMan.getAccessManager().isNatural(user);
						setText(natural ? "natural" : "machine", req);
					}
					
				};
				row.addCell("natural", nat);
				final Label lab = new Label(usersTable, "perms_" + lineId, req) {
					
					@Override
					public void onGET(OgemaHttpRequest req) {
						setHtml(cpa.newConditionalPermissionUpdate().getConditionalPermissionInfos().stream()
							.filter(perm -> appliesToSpecificUser(user, perm))
							.flatMap(perm -> Arrays.stream(perm.getPermissionInfos()))
							.map(PermissionInfo::getEncoded)
							.collect(Collectors.joining("<br>")), req);
					}
					
				};
				row.addCell("perms", lab);
				final Button editOpen = new Button(usersTable, "editOpen_" + lineId, "Open dialog", req) {
					
					public void onPOSTComplete(String data, OgemaHttpRequest req) {
						editUserName.setText(user, req);
						editPerms.setText(Arrays.stream(lab.getText(req).split("<br>"))
								.collect(Collectors.joining("\n")), req);
					}
					
				};
				editOpen.triggerAction(editPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET, req);
				editOpen.triggerAction(editUserName, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
				editOpen.triggerAction(editPerms, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);

				row.addCell("edit", editOpen);
				final Button copyOpen = new Button(usersTable, "copyOpen_" + lineId, "Open dialog", req) {
					
					public void onPOSTComplete(String data, OgemaHttpRequest req) {
						copyId.setValue(user, req);
						copyPerms.setText(Arrays.stream(lab.getText(req).split("<br>"))
								.collect(Collectors.joining("\n")), req);
						copyNatural.setCheckboxList(Collections.singletonList(
								new DefaultCheckboxEntry("natural", "", permMan.getAccessManager().isNatural(user))), req);
						copyPw.setValue("", req);
					}
					
				};
				copyOpen.triggerAction(copyPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET, req);
				copyOpen.triggerAction(copyId, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
				copyOpen.triggerAction(copyPerms, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
				copyOpen.triggerAction(copyNatural, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
				
				row.addCell("copy", copyOpen);
				
				ButtonConfirm deleteButton = new ButtonConfirm(page, "deleteButton" + lineId, "", req) {
					public void onPOSTComplete(String s, OgemaHttpRequest req) {
						
						// Uninstall permissions for given user
						uninstallPoliciesForUser(cpa);
						
						// Deleting the user account
						appMan.getAdministrationManager().removeUserAccount(user);
						
					}
					
					private void uninstallPoliciesForUser(final ConditionalPermissionAdmin cpa) {
						boolean done =false;
						while (!done) {
							ConditionalPermissionUpdate cpu = cpa.newConditionalPermissionUpdate();
							cpu.getConditionalPermissionInfos().removeIf(cpi -> appliesToSpecificUser(user, cpi));
							done = cpu.commit();
						}
					}
				};
				deleteButton.setDefaultText("Delete");
				deleteButton.setConfirmPopupTitle("Deleting " + user, req);
				deleteButton.setConfirmMsg("Do you really want to delete this user (" + user + ") with all permissions?", req);
				deleteButton.triggerAction(usersTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
				
				row.addCell("delete", deleteButton);
				return row;
			}
		};
		this.usersTable = new TemplateGrid<String>(page, "usersTable", false, usersTemplate) {
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				final String filter = userFilter.getValue(req).toLowerCase();
				update(
					permMan.getAccessManager().getAllUsers().stream()
						.filter(user -> user.toLowerCase().startsWith(filter))
						.collect(Collectors.toList())
					, req);
			}
			
		};
		usersTable.setDefaultAppendFillColumn(true);
		usersTable.setDefaultAlignItems(AlignItems.CENTER);
		usersTable.setDefaultColumnGap("2em");
		usersTable.setDefaultRowGap("1em");
		
		this.furtherPermsHeader = new Header(page, "furtherPermsHeader", "Further user-related permissions");
		furtherPermsHeader.setDefaultHeaderType(2);
		furtherPermsHeader.setDefaultColor("blue");
		this.furtherPerms = new SimpleGrid(page, "furtherPerms", false) {
			
			final AtomicInteger cnt = new AtomicInteger(0);
			
			public void onGET(OgemaHttpRequest req) {
				clear(req);
				cpa.newConditionalPermissionUpdate().getConditionalPermissionInfos().stream()
					.filter(EditPageInit::isNonSpecificUserPermission)
					.map(ConditionalPermissionInfo::getEncoded)
					.map(str -> new Label(furtherPerms, "fplab_" + cnt.getAndIncrement(), req) {
						
						@Override
						public void onGET(OgemaHttpRequest req) {
							setHtml(str, req);
						}
						
					})
					.forEach(w -> furtherPerms.addItem(w, true, req));
			}
			
		};
		
		this.copyPopup = new Popup(page, "copyPopup", true);
		this.copyId = new TextField(page, "copyId");
		this.copyPw = new TextField(page, "copyPw");
		copyPw.setDefaultType(TextFieldType.PASSWORD);
		copyPw.setDefaultPlaceholder("Like user id");
		this.copyPerms = new TextArea(page, "copyPerms");
		copyPerms.setDefaultWidth("90%");
		copyPerms.setDefaultRows(10); // no effect
		copyPerms.setDefaultSendValueOnChange(true);
		this.copyNatural = new Checkbox2(page, "copyNatural");
		copyNatural.setDefaultCheckboxList(Collections.singletonList(new DefaultCheckboxEntry("natural", "", false)));
		this.copyUserSubmit = new Button(page, "copyUserSubmit", "Create user") {
			
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				final String id = copyId.getValue(req);
				if (id == null || id.isEmpty()) {
					alert.showAlert("Please enter a user id", false, req);
					return;
				}
				if (permMan.getAccessManager().getRole(id) != null) {
					alert.showAlert("User " + id + " already exists", false, req);
					return;
				}
				String pw = copyPw.getValue(req);
				if (pw == null || pw.isEmpty())
					pw = id;
				final boolean natural = copyNatural.isChecked("natural", req);
				final boolean created = permMan.getAccessManager().createUser(id, pw, natural);
				if (!created) {
					alert.showAlert("Unknown error", false, req);
					return;
				}
				try {
					final List<String> policyNames = CreatePageInit.getPolicyNames(cpa);
					final String policyID = getValidPolicyID(policyNames, id);
					final StringBuilder sb = new StringBuilder();
					sb.append(ConditionalPermissionInfo.ALLOW).append(" { [org.osgi.service.condpermadmin.BundleLocationCondition \"urp:")
						.append(id).append("\"]");
					try (final BufferedReader reader = new BufferedReader(new StringReader(copyPerms.getText(req)))) {
						String line;
						while ((line = reader.readLine()) != null) {
							line = line.trim();
							if (CreatePageInit.isEmptyOrCommentLine(line))
								continue;
							sb.append(line);
						}
					}
					sb.append('}').append(' ').append(policyID);
					CreatePageInit.installPolicies(Stream.of(sb.toString()), cpa);
					alert.showAlert("New user created: " + id + " (" + (natural ? "natural" : "machine") + " user)", true, req);
				} catch (Exception e) {
					alert.showAlert("An error occured: " + e, false, req);
				}
			}
			
		};
		int row = 0;
		final StaticTable copyTable = new StaticTable(4, 2, new int[] {3,9});
		copyTable.setContent(row, 0, "User").setContent(row++, 1, copyId)
			.setContent(row, 0, "Password").setContent(row++, 1, copyPw)
			.setContent(row, 0, "Natural user?").setContent(row++, 1, copyNatural)
			.setContent(row, 0, "Permissions").setContent(row++, 1, copyPerms);
		final PageSnippet bodySnippet = new PageSnippet(page, "copyPopupBody", true);
		bodySnippet.append(copyTable, null);
		copyPopup.setBody(bodySnippet, null);
		copyPopup.setHeader("Copy user", null);
		copyPopup.setFooter(copyUserSubmit, null);
		copyPopup.setWidth("80vw", null);

		this.editPopup = new Popup(page, "editPopup", true);
		this.editUserName = new Label(page, "editUserName");
		this.editPerms = new TextArea(page, "editPerms");
		editPerms.setDefaultWidth("90%");
		editPerms.setDefaultRows(10);
		this.editUserSubmit = new Button(page, "editUserSubmit", "Save changes") {
			
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				final String user = editUserName.getText(req);
				if (user == null || user.isEmpty())
					return;
				final ConditionalPermissionUpdate cpu = cpa.newConditionalPermissionUpdate();
				final List<ConditionalPermissionInfo> infos = cpu.getConditionalPermissionInfos();
				Iterator<ConditionalPermissionInfo> it = infos.iterator();
				while (it.hasNext()) {
					if (appliesToSpecificUser(user, it.next()))
						it.remove();
				}
				final String perms = editPerms.getText(req).trim();
				if (!perms.isEmpty()) {
					final List<String> policyNames = CreatePageInit.getPolicyNames(cpa);
					final String policyID = getValidPolicyID(policyNames, user);
					final StringBuilder sb = new StringBuilder();
					sb.append(ConditionalPermissionInfo.ALLOW).append(" { [org.osgi.service.condpermadmin.BundleLocationCondition \"urp:")
						.append(user).append("\"]");
					try (final BufferedReader reader = new BufferedReader(new StringReader(perms))) {
						String line;
						while ((line = reader.readLine()) != null) {
							line = line.trim();
							if (CreatePageInit.isEmptyOrCommentLine(line))
								continue;
							sb.append(line);
						}
					} catch (IOException e) {
						alert.showAlert("Error: " + e, false, req);
						return;
					}
					sb.append('}').append(' ').append(policyID);
					final ConditionalPermissionInfo cpi;
					try {
						cpi = cpa.newConditionalPermissionInfo(sb.toString());
					} catch (IllegalArgumentException e) {
						alert.showAlert("Permission format invalid", false, req);
						return;
					}
					infos.add(cpi);
				}
				if (cpu.commit())
					alert.showAlert("Permissions edited", true, req);
				else
					alert.showAlert("Commit failed", false, req);
			}
			
		};
		
		row = 0;
		final StaticTable editTable = new StaticTable(2, 2, new int[] {3,9});
		editTable.setContent(row, 0, "User").setContent(row++, 1, editUserName)
			.setContent(row, 0, "Permissions").setContent(row++, 1, editPerms);
		final PageSnippet editBodySnippet = new PageSnippet(page, "editPopupBody", true);
		editBodySnippet.append(editTable, null);
		editPopup.setBody(editBodySnippet, null);
		editPopup.setHeader("Edit user", null);
		editPopup.setFooter(editUserSubmit, null);
		editPopup.setWidth("80vw", null);
		
		buildPage();
		setDependencies();
	}
	
	private final void buildPage() {
		page.append(header).linebreak().append(alert)
			.append(userHeader).linebreak()
			.append(filterFlex).linebreak()	// TODO description
			.append(usersTable).linebreak()
			.append(furtherPermsHeader).linebreak()
			.append(furtherPerms).linebreak()
			.append(copyPopup).linebreak()
			.append(editPopup);
	}
	
	private final void setDependencies() {
		userFilter.triggerAction(usersTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		copyUserSubmit.triggerAction(copyPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		copyUserSubmit.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		copyUserSubmit.triggerAction(usersTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editUserSubmit.triggerAction(editPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		editUserSubmit.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		editUserSubmit.triggerAction(usersTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
	}
	
	private static boolean appliesToSpecificUser(final String user, final ConditionalPermissionInfo cpi) {
		return Arrays.stream(cpi.getConditionInfos())
			.filter(cond -> cond.getEncoded().contains("org.osgi.service.condpermadmin.BundleLocationCondition")) // XXX this is ugly
			.filter(cond -> cond.getArgs().length > 0)
			.filter(cond -> Arrays.stream(cond.getArgs())
								.filter(arg -> arg.equals("urp:" + user))
								.findAny().isPresent())
			.findAny().isPresent();
	}
	
	private static boolean isNonSpecificUserPermission(final ConditionalPermissionInfo cpi) {
		return Arrays.stream(cpi.getConditionInfos())
			.filter(cond -> cond.getEncoded().contains("org.osgi.service.condpermadmin.BundleLocationCondition")) // XXX this is ugly
			.filter(cond -> cond.getArgs().length > 0)
			.filter(cond -> Arrays.stream(cond.getArgs())
								.filter(arg -> arg.contains("urp:") && arg.contains("*"))
								.findAny().isPresent())
			.findAny().isPresent();
	}
	
	private static Integer parseCounter(final String name) {
		try {
			return Integer.parseInt(name);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	/*
	 * This method returns a valid policy id (needed for a new permission) containing "<id>_" with an unused number at the end.
	 */
	public static String getValidPolicyID(List<String> policyNames, String id) {
		return id + "_" +
		(policyNames.stream()
		.filter(name -> name.startsWith(id))
		.filter(name -> name.length() > id.length())
		.map(name -> name.substring(id.length()))
		.map(EditPageInit::parseCounter)
		.filter(Objects::nonNull)
		.mapToInt(Integer::intValue)
		.max()
		.orElse(-1)
		+ 1);
	}
	
}
