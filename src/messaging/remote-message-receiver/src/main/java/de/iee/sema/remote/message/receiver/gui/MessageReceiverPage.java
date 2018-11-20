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
package de.iee.sema.remote.message.receiver.gui;

import java.io.FilePermission;
import java.io.IOException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ogema.accesscontrol.ResourcePermission;
import org.ogema.core.administration.UserAccount;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.StringResource;
import org.ogema.tools.resource.util.ResourceUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionUpdate;
import org.osgi.service.permissionadmin.PermissionInfo;

import de.iee.sema.remote.message.receiver.model.ClientData;
import de.iee.sema.remote.message.receiver.model.RemoteMessage;
import de.iee.sema.remote.message.receiver.template.ClientDataTemplate;
import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.ButtonData;
import de.iwes.widgets.html.form.checkbox.Checkbox2;
import de.iwes.widgets.html.form.checkbox.DefaultCheckboxEntry;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.popup.Popup;

@Component(
		service=LazyWidgetPage.class,
		property= {
				LazyWidgetPage.BASE_URL + "=/de/iwes/ogema/apps/remotemessagereceiver", 
				LazyWidgetPage.RELATIVE_URL + "=useroverview.html",
				LazyWidgetPage.START_PAGE + "=true",
				LazyWidgetPage.MENU_ENTRY + "=User Overview"
		}
)
public class MessageReceiverPage implements LazyWidgetPage {
	
	private static final String CHECK_MSG_PERM = "msgPerm";
	private static final String CHECK_FILE_PERM = "filePerm";

	@Reference
	private ConditionalPermissionAdmin cpa;
	
	@Override
	public void init(ApplicationManager appMan, WidgetPage<?> page) {
		@SuppressWarnings("unchecked")
		ResourceList<ClientData> remoteMessages = appMan.getResourceManagement().createResource("clientData", ResourceList.class);
		remoteMessages.setElementType(ClientData.class);
		remoteMessages.activate(true);
		
		final Header header = new Header(page, "header", "Remote Receiver App - Users");
		header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
		
		final Alert alert = new Alert(page, "alert", "");
		alert.setDefaultVisibility(false);

		// Existing
		final DynamicTable<ClientData> clientDataTable = new DynamicTable<ClientData>(page, "clientDataTable", true) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				// get all ClientData resources
				List<ClientData> clientDatas = appMan.getResourceAccess().getResources(ClientData.class);
				updateRows(clientDatas, req);
			}

		};

		final RowTemplate<ClientData> clientDataTemplate = new ClientDataTemplate(appMan, page, alert,
				clientDataTable);

		clientDataTable.setRowTemplate(clientDataTemplate);
		clientDataTable.setDefaultRowIdComparator(null);

		final List<WidgetStyle<?>> styles = new ArrayList<>();
		styles.add(WidgetData.TEXT_ALIGNMENT_CENTERED);
		clientDataTable.setDefaultStyles(styles);

		final Popup newPopup = new Popup(page, "newPopup", true);
		newPopup.setTitle("New client configuration", null);

		final StaticTable newTable = new StaticTable(3, 2, new int[] {4,8});
		final PageSnippet newSnippet = new PageSnippet(page, "newSnippet", true);

		final TextField newNameTextField = new TextField(page, "newNameTextField");
		final TextField newPasswordTextField = new TextField(page, "newPasswordTextField");
		final Checkbox2 permCheck = new Checkbox2(page, "permCheck");
		permCheck.setDefaultCheckboxList(Arrays.asList(
				new DefaultCheckboxEntry(CHECK_MSG_PERM, "Add message permission", true),
				new DefaultCheckboxEntry(CHECK_FILE_PERM, "Add file upload permission", true)
		));
		permCheck.setDefaultToolTip("A message permissions allows the user to upload message resources via the"
				+ " OGEMA REST interface. The file permission allows the user to upload files for backup purposes.");
		newTable.setContent(0, 0, "Name");
		newTable.setContent(1, 0, "Password");
		newTable.setContent(0, 1, newNameTextField);
		newTable.setContent(1, 1, newPasswordTextField);
		newTable.setContent(2, 1, permCheck);

		final Button createNewButton = new Button(page, "createNewButton", "Create") {

			private static final long serialVersionUID = 1L;

			public void onPOSTComplete(String value, OgemaHttpRequest req) {
				String name = newNameTextField.getValue(req);
				String password = newPasswordTextField.getValue(req);
				
				if (!ResourceUtils.isValidResourcePath(name) || password.isEmpty() || name.contains("/")) {
					alert.showAlert("Invalid user name", false, req);
					return;
				}
				
				// create a ClientData Resource for the entered values
				ClientData cd = remoteMessages.getSubResource(name, ClientData.class).create();
				cd.userName().<StringResource>create().setValue(name);
				cd.messages().<ResourceList<RemoteMessage>>create();
				cd.activate(true);

				// Create REST User
				UserAccount account = appMan.getAdministrationManager().createUserAccount(name, false);
				account.setNewPassword(name, password);
				
				// adding permissions
				final boolean addResourcePerm = permCheck.isChecked(CHECK_MSG_PERM, req);
				final boolean addFilePerm = permCheck.isChecked(CHECK_FILE_PERM, req);
				final List<Permission> perms = new ArrayList<>();
				if (addResourcePerm) {
					final StringJoiner joiner = new StringJoiner(",");
					joiner.add(ResourcePermission.READ).add(ResourcePermission.WRITE).add(ResourcePermission.ACTIVITY)
							.add(ResourcePermission.CREATE).add(ResourcePermission.ADDSUB);
	
					final ResourcePermission perm = new ResourcePermission("path=" + cd.getPath() + "/*", joiner.toString());
					perms.add(perm);
				}
				if (addFilePerm) {
					try {
						final List<FilePermission> filePermissions = getUploadServletConfigs()
							.map(folder -> new FilePermission(folder + "/" + name + "/*", "read,write"))
							.collect(Collectors.toList());
						perms.addAll(filePermissions);
					} catch (NoClassDefFoundError e) {} // config admin is an optional dependency
				}
				if (!perms.isEmpty() && !addPermissions(cpa, perms, name)) {
					appMan.getAdministrationManager().removeUserAccount(name);
					alert.showAlert("could not create user, permission creation failed", false, req);
				} else {
					alert.showAlert("New user " + name + " created", true, req);
				}
				

			}
			
			private final boolean addPermissions(final ConditionalPermissionAdmin cpa, final List<Permission> permissions, final String userName) {
				boolean success = false;
				final PermissionInfo[] infos = permissions.stream()
					.map(perm -> new PermissionInfo(perm.getClass().getName(), perm.getName(), perm.getActions()))
					.toArray(PermissionInfo[]::new);
				for (int i=0; i < 5; i++) { // retry if permission creation fails
					final ConditionalPermissionUpdate update = cpa.newConditionalPermissionUpdate();
						final ConditionalPermissionInfo info = cpa.newConditionalPermissionInfo(null,
								new ConditionInfo[] {
										new ConditionInfo("org.osgi.service.condpermadmin.BundleLocationCondition",
												new String[] { "urp:" + userName }) },
								infos,
								"allow");
						update.getConditionalPermissionInfos().add(info);
					if (update.commit()) {
						success = true;
						break;
					}
				}
				return success;
			}
			
			/**
			 * See 
			 * https://github.com/smartrplace/smartrplace-tools/tree/master/src/file-upload
			 * https://github.com/smartrplace/smartrplace-tools/blob/master/src/file-upload/file-upload-servlet-api/src/main/java/org/smartrplace/tools/upload/server/FileUploadConstants.java
			 * 
			 * @throws NoClassDefFoundError config admin is an optional dependency
			 */
			private final Stream<String> getUploadServletConfigs() {
				final BundleContext ctx = appMan.getAppID().getBundle().getBundleContext();
				final ServiceReference<org.osgi.service.cm.ConfigurationAdmin> caRef = ctx.getServiceReference(org.osgi.service.cm.ConfigurationAdmin.class);
				if (caRef == null)
					return Stream.empty();
				try {
					final org.osgi.service.cm.ConfigurationAdmin ca = ctx.getService(caRef);
					if (ca == null)
						return Stream.empty();
					final org.osgi.service.cm.Configuration[] configs = ca.listConfigurations("(service.pid=org.smartrplace.tools.UploadServlet)");
					if (configs == null || configs.length == 0)
						return Stream.empty();
					return Arrays.stream(configs).map(config -> {
							final Object folder = config.getProperties().get("uploadFolder");
							if (!(folder instanceof String))
								return (String) null;
							String fold = (String) folder;
							if (fold.endsWith("/"))
								fold = fold.substring(0, fold.length()-1);
							return fold;
						}).filter(folder -> folder != null);
				} catch (InvalidSyntaxException e) {
					throw new RuntimeException(e);
				} catch (IOException e) {
					return Stream.empty();
				} finally {
					ctx.ungetService(caRef);
				}
			}

		};
		createNewButton.triggerAction(clientDataTable, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		createNewButton.triggerAction(newPopup, TriggeringAction.POST_REQUEST, TriggeredAction.HIDE_WIDGET);
		createNewButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		createNewButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);

		// build popup for creating new UserData
		newSnippet.append(newTable, null);
		newSnippet.append(createNewButton, null);
		newPopup.setBody(newSnippet, null);

		final Button newButton = new Button(page, "newButton", "New receiver");
		newButton.triggerAction(newPopup, TriggeringAction.POST_REQUEST, TriggeredAction.SHOW_WIDGET);
		newButton.addDefaultStyle(ButtonData.BOOTSTRAP_GREEN);

		// build page
		page.append(header).linebreak().append(alert).linebreak().append(clientDataTable).append(newButton)
				.append(newPopup);
	}
}
